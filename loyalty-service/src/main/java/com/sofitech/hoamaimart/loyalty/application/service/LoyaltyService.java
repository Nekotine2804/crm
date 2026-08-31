package com.sofitech.hoamaimart.loyalty.application.service;

import com.sofitech.hoamaimart.loyalty.domain.model.LoyaltyAccount;
import com.sofitech.hoamaimart.loyalty.domain.model.Points;
import com.sofitech.hoamaimart.loyalty.domain.port.in.LoyaltyCommandService;
import com.sofitech.hoamaimart.loyalty.domain.port.out.LoyaltyRepository;
import com.sofitech.hoamaimart.shared.error.BusinessErrorCode;
import com.sofitech.hoamaimart.shared.error.BusinessException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Application service: xử lý use case loyalty.
 * Convert IllegalArgumentException (domain) → BusinessException (gắn error code).
 */
public class LoyaltyService implements LoyaltyCommandService {

    private final LoyaltyRepository loyaltyRepository;

    public LoyaltyService(LoyaltyRepository loyaltyRepository) {
        this.loyaltyRepository = loyaltyRepository;
    }

    @Override
    public void addPoints(UUID customerId, BigDecimal transactionAmount) {
        LoyaltyAccount account = loyaltyRepository.findByCustomerId(customerId)
                .orElseGet(() -> LoyaltyAccount.createNew(customerId));

        account.addPointsFromTransaction(transactionAmount);

        loyaltyRepository.recordSpending(
                customerId,
                UUID.randomUUID(),
                transactionAmount,
                Instant.now()
        );

        BigDecimal rollingSpending = loyaltyRepository.calculateRollingWindowSpending(customerId);
        account.evaluateTier(rollingSpending.longValue());

        loyaltyRepository.save(account);
    }

    @Override
    public LoyaltyAccount redeem(UUID customerId, int pointsToRedeem) {
        // 1. Validate input
        if (pointsToRedeem <= 0) {
            throw BusinessException.of(
                BusinessErrorCode.LOYALTY_INVALID_POINTS,
                "Số điểm quy đổi phải > 0, nhận: " + pointsToRedeem
            );
        }

        // 2. Tìm tài khoản
        LoyaltyAccount account = loyaltyRepository.findByCustomerId(customerId)
                .orElseThrow(() -> BusinessException.of(
                    BusinessErrorCode.LOYALTY_ACCOUNT_NOT_FOUND,
                    "Khách hàng chưa có tài khoản loyalty: " + customerId
                ));

        // 3. Trừ điểm (domain throw IllegalArgumentException)
        try {
            account.redeem(Points.of(pointsToRedeem));
        } catch (IllegalArgumentException e) {
            throw BusinessException.of(BusinessErrorCode.LOYALTY_INSUFFICIENT_POINTS, e.getMessage());
        }

        return loyaltyRepository.save(account);
    }
}