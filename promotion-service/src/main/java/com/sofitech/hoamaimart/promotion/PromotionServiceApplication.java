package com.sofitech.hoamaimart.promotion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Bounded context "promotion" chạy độc lập: DB riêng (hoa_mai_promotion), deploy riêng,
 * scale riêng. Giao tiếp với service khác CHỈ qua:
 *  - RabbitMQ message (adapter/out/messaging publish, adapter/in/messaging consume)
 *  - hoặc REST gọi qua API Gateway (Spring Cloud Gateway) nếu cần đồng bộ
 * KHÔNG bao giờ import trực tiếp code của service khác.
 */
@SpringBootApplication
public class PromotionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PromotionServiceApplication.class, args);
    }
}