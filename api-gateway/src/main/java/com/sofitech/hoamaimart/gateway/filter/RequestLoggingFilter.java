package com.sofitech.hoamaimart.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Ví dụ 1 global filter — chạy cho MỌI request đi qua gateway, tương đương
 * chỗ trước đây gán vào "plugin" của Kong. Đây là chỗ thêm sau này:
 *   - correlation-id cho tracing xuyên service
 *   - auth check (JWT) nếu tích hợp lại Keycloak
 *   - request/response logging tập trung
 */
@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("Incoming request: {} {}",
                exchange.getRequest().getMethod(),
                exchange.getRequest().getURI());
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1; // chạy trước các filter khác
    }
}
