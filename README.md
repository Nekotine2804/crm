# Hướng dẫn cài đặt Hoa Mai Mart Loyalty & CRM

Hướng dẫn chạy môi trường phát triển local cho hệ thống microservices Hoa Mai Mart Loyalty & CRM. Hệ thống gồm API Gateway, các service quản lý khách hàng, loyalty, giao dịch POS, khuyến mãi, thông báo; PostgreSQL riêng cho từng service và RabbitMQ để trao đổi sự kiện.

Tài liệu kiến trúc và hợp đồng sự kiện: [Architecture Proposal](docs/03-architect-proposal.md).

## 1. Yêu cầu

- JDK 21
- Maven 3.9 trở lên
- Docker Engine và Docker Compose v2
- `curl` (không bắt buộc, dùng để kiểm tra API)

Kiểm tra môi trường:

```bash
java -version
mvn -version
docker compose version
```

## 2. Khởi động hạ tầng

Từ thư mục gốc dự án, chạy RabbitMQ và năm PostgreSQL database:

```bash
docker compose -f infra/docker-compose.yml up -d
docker compose -f infra/docker-compose.yml ps
```

| Thành phần | Địa chỉ | Tài khoản |
|---|---|---|
| RabbitMQ AMQP | `localhost:5672` | `guest` / `guest` |
| RabbitMQ Management UI | `http://localhost:15672` | `guest` / `guest` |
| Customer PostgreSQL | `localhost:5441/hoa_mai_customer` | `crm_user` / `change-me` |
| Loyalty PostgreSQL | `localhost:5442/hoa_mai_loyalty` | `crm_user` / `change-me` |
| Transaction PostgreSQL | `localhost:5443/hoa_mai_transaction` | `crm_user` / `change-me` |
| Promotion PostgreSQL | `localhost:5444/hoa_mai_promotion` | `crm_user` / `change-me` |
| Notification PostgreSQL | `localhost:5445/hoa_mai_notification` | `crm_user` / `change-me` |

> Cấu hình mặc định trong từng `application.yml` trỏ tới PostgreSQL tại cổng `5432`. Khi chạy local với Docker Compose, cần dùng các biến môi trường trong phần 4 để trỏ đúng database.

## 3. Build mã nguồn

```bash
mvn clean install -DskipTests
```

Chạy toàn bộ test khi cần:

```bash
mvn test
```

## 4. Chạy ứng dụng

Mở một terminal riêng cho mỗi service. Khởi động theo thứ tự dưới đây để consumer sẵn sàng nhận event trước producer.

### 4.1 Loyalty Service

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5442/hoa_mai_loyalty \
SPRING_DATASOURCE_USERNAME=crm_user \
SPRING_DATASOURCE_PASSWORD=change-me \
mvn -pl loyalty-service spring-boot:run
```

### 4.2 Notification Service

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5445/hoa_mai_notification \
SPRING_DATASOURCE_USERNAME=crm_user \
SPRING_DATASOURCE_PASSWORD=change-me \
mvn -pl notification-service spring-boot:run
```

### 4.3 Promotion Service

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5444/hoa_mai_promotion \
SPRING_DATASOURCE_USERNAME=crm_user \
SPRING_DATASOURCE_PASSWORD=change-me \
mvn -pl promotion-service spring-boot:run
```

### 4.4 Customer Service

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5441/hoa_mai_customer \
SPRING_DATASOURCE_USERNAME=crm_user \
SPRING_DATASOURCE_PASSWORD=change-me \
mvn -pl customer-service spring-boot:run
```

### 4.5 Transaction Service

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5443/hoa_mai_transaction \
SPRING_DATASOURCE_USERNAME=crm_user \
SPRING_DATASOURCE_PASSWORD=change-me \
mvn -pl transaction-service spring-boot:run
```

### 4.6 API Gateway

```bash
mvn -pl api-gateway spring-boot:run
```

## 5. Truy cập dịch vụ

| Dịch vụ | Cổng | Swagger UI |
|---|---:|---|
| API Gateway | 8000 | `http://localhost:8000/swagger-ui.html` |
| Customer Service | 8081 | `http://localhost:8081/swagger-ui.html` |
| Loyalty Service | 8082 | `http://localhost:8082/swagger-ui.html` |
| Transaction Service | 8083 | `http://localhost:8083/swagger-ui.html` |
| Promotion Service | 8084 | `http://localhost:8084/swagger-ui.html` |
| Notification Service | 8085 | `http://localhost:8085/swagger-ui.html` |

Các API có thể được gọi qua API Gateway ở `http://localhost:8000`. Ví dụ route customer: `http://localhost:8000/api/customers`.

## 6. Kiểm tra trạng thái

Sau khi mọi service khởi động xong, chạy:

```bash
curl http://localhost:8000/actuator/health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
curl http://localhost:8084/actuator/health
curl http://localhost:8085/actuator/health
```

Kết quả mong đợi:

```json
{"status":"UP"}
```

## 7. Dừng môi trường

Dừng từng ứng dụng Java bằng `Ctrl+C` tại terminal đang chạy. Dừng Docker containers:

```bash
docker compose -f infra/docker-compose.yml down
```

Để xóa cả dữ liệu local trong PostgreSQL và RabbitMQ:

```bash
docker compose -f infra/docker-compose.yml down -v
```

> `down -v` xóa toàn bộ dữ liệu Docker volume của môi trường local và không thể khôi phục bằng Docker Compose.

## 8. Khắc phục lỗi thường gặp

**Không kết nối được database:** kiểm tra các container bằng `docker compose -f infra/docker-compose.yml ps`, sau đó xác nhận đúng `SPRING_DATASOURCE_URL`, username và password ở phần 4.

**Cổng đang được sử dụng:** kiểm tra tiến trình đang lắng nghe:

```bash
ss -ltnp | grep -E ':8000|:8081|:8082|:8083|:8084|:8085|:5672|:15672'
```

**Event không được xử lý:** mở RabbitMQ Management UI, kiểm tra exchange `hoamai.exchange`, queue, binding và trạng thái consumer của Loyalty/Notification Service.
