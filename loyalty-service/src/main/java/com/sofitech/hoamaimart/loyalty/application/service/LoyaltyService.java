package com.sofitech.hoamaimart.loyalty.application.service;

import com.sofitech.hoamaimart.loyalty.domain.model.LoyaltyAccount;
import com.sofitech.hoamaimart.loyalty.domain.model.Points;
import com.sofitech.hoamaimart.loyalty.domain.port.in.LoyaltyCommandService;
import com.sofitech.hoamaimart.loyalty.domain.port.out.LoyaltyRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Application service: xử lý use case loyalty.
 */
public class LoyaltyService implements LoyaltyCommandService {

    private final LoyaltyRepository loyaltyRepository;

    public LoyaltyService(LoyaltyRepository loyaltyRepository) {
        this.loyaltyRepository = loyaltyRepository;
    }

    @Override
    public void addPoints(UUID customerId, BigDecimal transactionAmount) {
        // 1. Tìm hoặc tạo tài khoản loyalty
        LoyaltyAccount account = loyaltyRepository.findByCustomerId(customerId)
                .orElseGet(() -> LoyaltyAccount.createNew(customerId));

        // 2. Cộng điểm tích lũy
        account.addPointsFromTransaction(transactionAmount);

        // 3. Ghi nhận chi tiêu
        loyaltyRepository.recordSpending(
                customerId,
                UUID.randomUUID(),
                transactionAmount,
                Instant.now()
        );

        // 4. Cập nhật tier dựa trên rolling window spending
        BigDecimal rollingSpending = loyaltyRepository.calculateRollingWindowSpending(customerId);
        account.evaluateTier(rollingSpending.longValue());

        // 5. Lưu vào DB
        loyaltyRepository.save(account);
    }

    @Override
    public LoyaltyAccount redeem(UUID customerId, int pointsToRedeem) {
        // 1. Validate input
        if (pointsToRedeem <= 0) {
            throw new IllegalArgumentException("Số điểm quy đổi phải > 0");
        }

        // 2. Tìm tài khoản loyalty (phải tồn tại - không tự tạo mới khi redeem)
        LoyaltyAccount account = loyaltyRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new IllegalArgumentException(
                    "Khách hàng chưa có tài khoản loyalty. Hãy mua hàng trước."
                ));

        // 3. Trừ điểm (domain sẽ throw nếu không đủ)
        account.redeem(Points.of(pointsToRedeem));

        // 4. Lưu vào DB
        return loyaltyRepository.save(account);
    }
}