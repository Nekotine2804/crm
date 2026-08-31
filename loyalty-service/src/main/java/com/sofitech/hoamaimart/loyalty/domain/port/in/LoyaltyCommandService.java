package com.sofitech.hoamaimart.loyalty.domain.port.in;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Port IN: command service interface.
 */
public interface LoyaltyCommandService {

    /**
     * Cộng điểm loyalty cho khách hàng.
     * Nếu tài khoản chưa có, tự động tạo mới.
     */
    void addPoints(UUID customerId, BigDecimal transactionAmount);
}