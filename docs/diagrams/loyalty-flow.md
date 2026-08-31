# Loyalty Service — Sequence Diagrams

## 1. Khách mua hàng → cộng điểm + cập nhật tier

```mermaid
sequenceDiagram
    autonumber
    participant POS as POS Client
    participant TXN as TransactionService
    participant DB as PostgreSQL (txn)
    participant MQ as RabbitMQ
    participant LOY as LoyaltyService
    participant LDB as PostgreSQL (loyalty)

    POS->>TXN: POST /api/transactions {customerId, amount}
    TXN->>DB: INSERT transaction (status=COMPLETED)
    TXN-->>POS: 201 Created {transactionId}
    TXN->>MQ: publish "transaction.completed"
    Note over MQ: routingKey=transaction.completed<br/>exchange=hoamai.exchange

    MQ->>LOY: @RabbitListener consume event
    LOY->>LDB: SELECT loyalty_account WHERE customerId=?
    LOY->>LDB: INSERT spending_history {amount, spentAt=now}
    LOY->>LDB: SELECT SUM(amount) WHERE spentAt >= now-12months
    Note over LOY,LDB: Tính rolling window spending
    LOY->>LOY: account.addPoints(amount / 10.000)
    LOY->>LOY: account.evaluateTier(rollingSpending)
    LOY->>LDB: UPDATE loyalty_accounts SET points=?, tier=?
```

## 2. Khách ngừng mua → tự rớt hạng

```mermaid
sequenceDiagram
    autonumber
    participant Khach as Khách hàng
    participant LOY as LoyaltyService
    participant LDB as PostgreSQL (loyalty)

    Note over Khach,LDB: Tháng 1: Khách mua 2.000.000đ → tier=GOLD
    Note over Khach,LDB: Tháng 2 → 11: Không mua<br/>(rolling window vẫn còn tháng 1)
    Note over Khach,LDB: Tháng 12: giao dịch tháng 1 trượt ra khỏi<br/>rolling window (12 tháng)

    Khach->>LOY: POST giao dịch mới 100.000đ
    LOY->>LDB: INSERT spending_history
    LOY->>LDB: SELECT SUM(amount) WHERE spentAt >= now-12months
    Note over LDB: Kết quả: 100.000đ<br/>(tháng 1 đã cũ, bị loại)
    LOY->>LOY: Tier.fromSpending(100.000) → BRONZE
    LOY->>LDB: UPDATE tier = BRONZE (rớt từ GOLD)
```

## 3. Cron job đánh giá tier hàng tháng

```mermaid
sequenceDiagram
    autonumber
    participant Cron as Cron Scheduler
    participant SCH as TierEvaluationScheduler
    participant LDB as PostgreSQL (loyalty)
    participant MQ as RabbitMQ
    participant NOTI as NotificationService

    Note over Cron: 0 0 0 1 * ?<br/>(00:00 ngày 1 mỗi tháng)

    Cron->>SCH: trigger evaluateAllTiers()
    SCH->>LDB: SELECT customers WHERE tier > BRONZE
    loop for each customer
        SCH->>LDB: SELECT SUM(amount) WHERE spentAt >= now-12months
        SCH->>SCH: evaluateTier(rollingSpending)
        SCH->>LDB: UPDATE tier
    end

    Note over Cron: 0 0 9 ? * MON<br/>(Thứ 2 hàng tuần, 09:00)

    Cron->>SCH: trigger detectDormantCustomers()
    SCH->>LDB: SELECT customers WHERE last_tx > 90 days ago
    SCH->>MQ: publish "loyalty.dormant.detected"
    MQ->>NOTI: Gửi email re-engagement
```

## Tier Thresholds

| Tier | Chi tiêu 12 tháng | Mục đích FMCG |
|------|-------------------|---------------|
| BRONZE | < 500.000đ | Mặc định |
| SILVER | ≥ 500.000đ | Quay lại ~1 lần/tuần |
| GOLD | ≥ 2.000.000đ | Khách trung thành |
| PLATINUM | ≥ 10.000.000đ | VIP |

## Cron schedule

| Cron | Mô tả |
|------|-------|
| `0 0 0 1 * ?` | Đánh giá tier tất cả khách |
| `0 0 2 1 * ?` | Cleanup spending_history > 13 tháng |
| `0 0 9 ? * MON` | Phát hiện dormant (>90 ngày) |
| `0 0 10 20 * ?` | Cảnh báo sắp rớt hạng |

## Tại sao rolling window cho FMCG?

```
❌ Cách cũ: tier theo tổng điểm tích lũy (lifetime)
   → Khách mua 1 lần 100tr vẫn Platinum cả đời (không công bằng)

✅ Cách mới: tier theo rolling window 12 tháng
   → Khách ngừng mua → tự rớt hạng (chính xác, fair)
   → FMCG = mua lặp lại thường xuyên → khuyến khích tần suất
   → Cleanup tự động: data > 13 tháng bị xóa (DB nhẹ)
```