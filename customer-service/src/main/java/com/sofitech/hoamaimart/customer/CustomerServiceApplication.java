package com.sofitech.hoamaimart.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Bounded context "customer" chạy độc lập: DB riêng (hoa_mai_customer), deploy riêng,
 * scale riêng. Giao tiếp với service khác CHỈ qua:
 *  - Kafka event (adapter/out/messaging publish, adapter/in/messaging consume)
 *  - hoặc REST gọi qua API Gateway (Spring Cloud Gateway) nếu cần đồng bộ
 * KHÔNG bao giờ import trực tiếp code của service khác.
 */
@SpringBootApplication
public class CustomerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }
}
