package com.sofitech.hoamaimart.loyalty.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain model: Point Transaction.
 * Ghi nhận mọi thay đổi điểm (tích, đổi, điều chỉnh, hoàn).
 * Dùng cho audit trail và lịch sử.
 */
public class PointTransaction {

    private final UUID id;
    private final UUID loyaltyAccountId;
    private final UUID customerId;
    private final PointTransactionType type;
    private final int points;           // Số điểm thay đổi (dương = cộng, âm = trừ)
    private final int balanceAfter;    // Số dư sau giao dịch
    private final String referenceId;  // ID tham chiếu (transaction ID, redemption ID, etc.)
    private final String description;
    private final Instant createdAt;

    public PointTransaction(
            UUID id,
            UUID loyaltyAccountId,
            UUID customerId,
            PointTransactionType type,
            int points,
            int balanceAfter,
            String referenceId,
            String description,
            Instant createdAt
    ) {
        this.id = id;
        this.loyaltyAccountId = loyaltyAccountId;
        this.customerId = customerId;
        this.type = type;
        this.points = points;
        this.balanceAfter = balanceAfter;
        this.referenceId = referenceId;
        this.description = description;
        this.createdAt = createdAt;
    }

    /**
     * Factory: tạo transaction tích điểm.
     */
    public static PointTransaction earn(
            UUID loyaltyAccountId,
            UUID customerId,
            int earnedPoints,
            int balanceAfter,
            String transactionId
    ) {
        return new PointTransaction(
                UUID.randomUUID(),
                loyaltyAccountId,
                customerId,
                PointTransactionType.EARN,
                earnedPoints,
                balanceAfter,
                transactionId,
                "Tích điểm từ giao dịch",
                Instant.now()
        );
    }

    /**
     * Factory: tạo transaction đổi điểm.
     */
    public static PointTransaction redeem(
            UUID loyaltyAccountId,
            UUID customerId,
            int redeemedPoints,
            int balanceAfter,
            String redemptionId
    ) {
        return new PointTransaction(
                UUID.randomUUID(),
                loyaltyAccountId,
                customerId,
                PointTransactionType.REDEEM,
                -redeemedPoints,  // Trừ điểm
                balanceAfter,
                redemptionId,
                "Đổi điểm",
                Instant.now()
        );
    }

    /**
     * Factory: tạo transaction hoàn điểm (refund).
     */
    public static PointTransaction refund(
            UUID loyaltyAccountId,
            UUID customerId,
            int refundPoints,
            int balanceAfter,
            String originalTransactionId
    ) {
        return new PointTransaction(
                UUID.randomUUID(),
                loyaltyAccountId,
                customerId,
                PointTransactionType.REFUND,
                -refundPoints,  // Trừ điểm đã hoàn
                balanceAfter,
                originalTransactionId,
                "Hoàn điểm từ refund",
                Instant.now()
        );
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getLoyaltyAccountId() { return loyaltyAccountId; }
    public UUID getCustomerId() { return customerId; }
    public PointTransactionType getType() { return type; }
    public int getPoints() { return points; }
    public int getBalanceAfter() { return balanceAfter; }
    public String getReferenceId() { return referenceId; }
    public String getDescription() { return description; }
    public Instant getCreatedAt() { return createdAt; }
}
