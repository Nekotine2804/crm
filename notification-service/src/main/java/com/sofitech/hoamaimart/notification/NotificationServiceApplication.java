package com.sofitech.hoamaimart.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Bounded context "notification" chạy độc lập: DB riêng (hoa_mai_notification), deploy riêng,
 * scale riêng. Giao tiếp với service khác CHỈ qua:
 *  - Kafka event (adapter/out/messaging publish, adapter/in/messaging consume)
 *  - hoặc REST gọi qua API Gateway (Spring Cloud Gateway) nếu cần đồng bộ
 * KHÔNG bao giờ import trực tiếp code của service khác.
 */
@SpringBootApplication
public class NotificationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
