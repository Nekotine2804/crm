package com.sofitech.hoamaimart.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Bounded context "transaction" chạy độc lập: DB riêng (hoa_mai_transaction), deploy riêng,
 * scale riêng. Giao tiếp với service khác CHỈ qua:
 *  - Kafka event (adapter/out/messaging publish, adapter/in/messaging consume)
 *  - hoặc REST gọi qua API Gateway (Spring Cloud Gateway) nếu cần đồng bộ
 * KHÔNG bao giờ import trực tiếp code của service khác.
 */
@SpringBootApplication
public class TransactionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(TransactionServiceApplication.class, args);
    }
}
