package com.sofitech.hoamaimart.loyalty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Bounded context "loyalty" chạy độc lập: DB riêng (hoa_mai_loyalty), deploy riêng,
 * scale riêng. Giao tiếp với service khác CHỈ qua:
 *  - RabbitMQ message (adapter/out/messaging publish, adapter/in/messaging consume)
 *  - hoặc REST gọi qua API Gateway (Spring Cloud Gateway) nếu cần đồng bộ
 * KHÔNG bao giờ import trực tiếp code của service khác.
 */
@SpringBootApplication
public class LoyaltyServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LoyaltyServiceApplication.class, args);
    }
}