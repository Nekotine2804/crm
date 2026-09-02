package com.sofitech.hoamaimart.loyalty.domain.port.in;

import com.sofitech.hoamaimart.loyalty.domain.model.LoyaltyAccount;

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

    /**
     * Quy đổi điểm loyalty (redeem).
     * @param customerId ID khách hàng
     * @param pointsToRedeem số điểm muốn quy đổi
     * @param redemptionId mã tham chiếu duy nhất của lần quy đổi
     * @return LoyaltyAccount mới sau khi trừ điểm
     * @throws IllegalArgumentException nếu không đủ điểm
     */
    LoyaltyAccount redeem(UUID customerId, int pointsToRedeem, String redemptionId);
}
