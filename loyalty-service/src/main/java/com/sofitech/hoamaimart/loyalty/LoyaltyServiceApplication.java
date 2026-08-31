package com.sofitech.hoamaimart.loyalty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Loyalty Service - bounded context loyalty.
 * Scan shared.error để nhận GlobalExceptionHandler + BusinessErrorCode.
 */
@SpringBootApplication
@ComponentScan(basePackages = {
    "com.sofitech.hoamaimart.loyalty",
    "com.sofitech.hoamaimart.shared.error"
})
@EnableScheduling
public class LoyaltyServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LoyaltyServiceApplication.class, args);
    }
}