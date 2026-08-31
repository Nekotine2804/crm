package com.sofitech.hoamaimart.loyalty.application.service;

import com.sofitech.hoamaimart.loyalty.domain.model.LoyaltyAccount;
import com.sofitech.hoamaimart.loyalty.domain.port.in.LoyaltyCommandService;
import com.sofitech.hoamaimart.loyalty.domain.port.out.LoyaltyRepository;

import java.math.BigDecimal;
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

        // 2. Cộng điểm tích lũy (dùng cho khuyến mãi)
        account.addPointsFromTransaction(transactionAmount);

        // 3. Ghi nhận chi tiêu (cho rolling window)
        loyaltyRepository.recordSpending(
                customerId,
                UUID.randomUUID(), // transactionId từ event
                transactionAmount,
                java.time.Instant.now()
        );

        // 4. Cập nhật tier dựa trên rolling window spending
        BigDecimal rollingSpending = loyaltyRepository.calculateRollingWindowSpending(customerId);
        account.evaluateTier(rollingSpending.longValue());

        // 5. Lưu vào DB
        loyaltyRepository.save(account);
    }
}