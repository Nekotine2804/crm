package com.sofitech.hoamaimart.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 1 cửa vào duy nhất cho mọi client (Store POS, Marketing dashboard...).
 * Client KHÔNG bao giờ gọi thẳng vào port riêng của từng service.
 *
 * Route + filter khai báo ở application.yml (RouteLocator kiểu YAML, đơn
 * giản, dễ đọc). Khi cần logic phức tạp hơn (vd. custom auth filter), thêm
 * class Java trong package "filter" (xem RequestLoggingFilter mẫu bên dưới).
 */
@SpringBootApplication
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
