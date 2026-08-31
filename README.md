# Hoa Mai Mart – Loyalty & CRM Platform (Microservices)

Chuyển từ Modular Monolith sang **Microservices**: mỗi bounded context giờ là
1 service Spring Boot độc lập, có DB riêng, deploy/scale riêng. Bên trong mỗi
service vẫn giữ **Hexagonal (Ports & Adapters)**; giao tiếp **giữa** các
service là **Event-Driven qua Kafka** (bất đồng bộ) hoặc REST qua
**Spring Cloud Gateway** (khi cần đồng bộ).

## Sơ đồ tổng quan

```
                        ┌───────────────────┐
   Store POS / Web  ──> │  api-gateway       │ ──> customer-service    (:8081, DB riêng)
   Marketing Dashboard  │ (Spring Cloud      │ ──> loyalty-service     (:8082, DB riêng)
                        │  Gateway, :8000)   │ ──> transaction-service (:8083, DB riêng)
                        └───────────────────┘ ──> promotion-service   (:8084, DB riêng)
                                              ──> notification-service(:8085, DB riêng)

   Tất cả service đều publish/subscribe qua:
                        ┌─────────────┐
                        │    Kafka    │
                        └─────────────┘
```

## Vì sao Spring Cloud Gateway (thay vì Kong / cloud API Gateway)

- **Cùng ngôn ngữ, cùng ecosystem**: `api-gateway` chỉ là 1 Spring Boot app
  khác (module `api-gateway/`) — route/filter viết bằng YAML + Java, không
  cần học thêm ngôn ngữ/tool riêng (Kong dùng Lua cho custom plugin).
- **Ít thành phần hạ tầng hơn**: đội IT chỉ 6 người, toàn Java — không cần
  vận hành thêm 1 hạ tầng ngoài (Kong/Nginx), giảm gánh nặng ops.
- **Cloud-agnostic**: không khóa vào 1 cloud provider cụ thể, chạy được ở
  bất kỳ đâu chạy được JVM — phù hợp vì hạ tầng khách hàng còn phân mảnh,
  chưa cam kết hẳn 1 cloud.
- **Đánh đổi cần nêu trong Risk Assessment**: Spring Cloud Gateway (WebFlux,
  reactive) có throughput/latency kém hơn 1 reverse proxy chuyên dụng như
  Kong (built trên Nginx) khi traffic rất lớn; và nếu sau này có service
  viết bằng ngôn ngữ khác (Python/Node), lợi thế "cùng ngôn ngữ" sẽ mất đi
  (nhưng Gateway vẫn route HTTP bình thường được, chỉ mất phần tiện lợi).

## Vì sao mỗi service vẫn theo Hexagonal + tại sao tách event ra Kafka

- **Database per service**: `transaction-service` không được query thẳng DB
  của `loyalty-service`. Muốn biết khách hàng có bao nhiêu điểm, phải gọi
  API `loyalty-service` (qua gateway) hoặc lắng nghe event.
- **Event-driven qua Kafka thay vì gọi đồng bộ**: khi `transaction-service`
  ghi nhận hóa đơn xong, nó publish `transaction.completed` lên Kafka.
  `loyalty-service` subscribe topic này để cộng điểm, `notification-service`
  subscribe để gửi SMS cảm ơn/ưu đãi — **không đồng bộ, không phụ thuộc
  transaction-service phải chờ 2 service kia phản hồi** → transaction-service
  vẫn hoạt động được dù loyalty-service đang down (resilience).
- **shared-kernel**: 1 module Maven build ra `.jar` chứa contract `DomainEvent`
  + schema event dùng chung, KHÔNG chứa business logic của service nào. Mỗi
  service khai báo nó như 1 dependency (`mvn install` module này trước).
- **Hexagonal bên trong từng service**: domain vẫn không phụ thuộc Spring/Kafka
  — khi đổi Kafka sang RabbitMQ hay đổi Postgres sang DB khác, chỉ sửa
  `adapter/`, domain/application không đổi.

## Cấu trúc thư mục

```
hoa-mai-mart-crm-ms/
├── shared-kernel/               # Maven lib: DomainEvent contract, exception, util chung
├── api-gateway/                 # Spring Cloud Gateway - cổng vào duy nhất (:8000)
│   └── src/main/java/.../gateway/
│       ├── filter/                # GlobalFilter tùy biến (logging, sau này auth/JWT...)
│       └── config/
│
├── customer-service/            # Mỗi service = 1 Spring Boot app riêng biệt
├── loyalty-service/
├── transaction-service/
├── promotion-service/
├── notification-service/
│   # Bên trong MỖI service ở trên:
│   └── src/main/java/.../<service>/
│       ├── domain/                 # LÕI - thuần Java, không phụ thuộc framework
│       │   ├── model/
│       │   ├── event/                # Domain event NỘI BỘ service (khác event Kafka ra ngoài)
│       │   ├── exception/
│       │   └── port/{in,out}/
│       ├── application/
│       │   ├── service/               # Use case
│       │   └── eventhandler/           # Xử lý Kafka event NHẬN từ service khác
│       ├── adapter/
│       │   ├── in/web/                 # REST Controller (đứng sau api-gateway)
│       │   ├── in/messaging/           # @KafkaListener - consume event từ service khác
│       │   ├── out/persistence/        # JPA - CHỈ thao tác DB của chính service này
│       │   ├── out/messaging/          # KafkaTemplate - publish event ra ngoài
│       │   └── out/client/             # Gọi REST service khác (qua gateway) nếu bắt buộc đồng bộ
│       └── config/                    # Cấu hình Kafka/Redis riêng của service
│
├── infra/docker-compose.yml     # Kafka, Redis, 1 Postgres/service (chạy local)
└── README.md
```

## Ví dụ luồng event xuyên service (đưa vào Sequence Diagram – 4.7)

1. Nhân viên quét hóa đơn tại POS → gọi `POST /api/transactions` qua api-gateway.
2. `transaction-service` lưu giao dịch vào DB riêng, publish event
   `transaction.completed` (customerId, storeId, amount) lên Kafka.
3. `loyalty-service` consume event → tính điểm theo hạng thành viên → lưu
   vào DB riêng → publish tiếp `loyalty.points.earned`.
4. `notification-service` consume `loyalty.points.earned` → gửi SMS
   "Bạn vừa được cộng X điểm".

`transaction-service` hoàn toàn không biết `notification-service` tồn tại.

## Chạy local (dev)

```bash
cd shared-kernel && mvn install -N        # build & install shared-kernel vào .m2 local
cd ../infra && docker compose up -d       # Kafka, Redis, Postgres x5
cd ../api-gateway && mvn spring-boot:run  # cổng vào ở :8000
# ở các terminal khác, chạy từng service:
cd ../customer-service && mvn spring-boot:run
```

## Lưu ý khi trình bày / đưa vào tài liệu bài SOFITECH

- Đây là **kiến trúc mục tiêu (target architecture)** để chứng minh năng lực
  thiết kế microservices; trong 5 ngày làm assignment, chỉ cần code POC thật
  cho 1–2 service (gợi ý: `transaction-service` + `loyalty-service`, vì đây
  là luồng nghiệp vụ lõi nhất), các service còn lại giữ nguyên skeleton +
  ghi rõ trong **Assumption Document**.
- Việc chuyển từ Monolith sang Microservices, và chọn Spring Cloud Gateway
  thay vì Kong/cloud API Gateway, nên là 2 mục riêng trong **Decision Log**
  (4.14): lý do, đánh đổi — đội IT chỉ 6 người là 1 rủi ro cần nêu trong
  **Risk Assessment** (4.12).
