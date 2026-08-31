package com.sofitech.hoamaimart.loyalty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Loyalty Service - bounded context loyalty.
 * Chạy độc lập, giao tiếp với service khác qua RabbitMQ.
 */
@SpringBootApplication
public class LoyaltyServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LoyaltyServiceApplication.class, args);
    }
}