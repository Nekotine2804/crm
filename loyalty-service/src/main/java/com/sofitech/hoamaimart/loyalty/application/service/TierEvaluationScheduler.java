package com.sofitech.hoamaimart.loyalty.application.service;

import com.sofitech.hoamaimart.loyalty.domain.model.LoyaltyAccount;
import com.sofitech.hoamaimart.loyalty.domain.port.out.LoyaltyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * Scheduled service: đánh giá tier định kỳ.
 * Chạy vào ngày 1 hàng tháng.
 */
@Service
public class TierEvaluationScheduler {

    private static final Logger log = LoggerFactory.getLogger(TierEvaluationScheduler.class);

    private final LoyaltyRepository loyaltyRepository;

    public TierEvaluationScheduler(LoyaltyRepository loyaltyRepository) {
        this.loyaltyRepository = loyaltyRepository;
    }

    /**
     * Chạy đánh giá tier hàng tháng (ngày 1, 00:00).
     */
    @Scheduled(cron = "0 0 0 1 * ?") // Ngày 1 mỗi tháng, 00:00
    public void evaluateAllTiers() {
        log.info("Starting monthly tier evaluation...");

        // TODO: Implement pagination for large datasets
        // For now, this would be called with a batch processor
        log.info("Tier evaluation completed");
    }

    /**
     * Đánh giá tier cho một khách hàng cụ thể.
     */
    public void evaluateTier(UUID customerId) {
        LoyaltyAccount account = loyaltyRepository.findByCustomerId(customerId).orElse(null);
        if (account == null) {
            return;
        }

        // Tính rolling window spending
        java.math.BigDecimal rollingSpending = loyaltyRepository.calculateRollingWindowSpending(customerId);

        // Kiểm tra nếu khách ngừng mua quá 90 ngày
        var lastTransaction = loyaltyRepository.findLastTransactionAt(customerId);
        if (lastTransaction.isPresent() && account.isDormant(lastTransaction.get())) {
            // Khách dormant → có thể rớt hạng ở lần đánh giá này
            log.info("Customer {} is dormant since {}", customerId, lastTransaction.get());
        }

        // Cập nhật tier
        account.evaluateTier(rollingSpending.longValue());
        loyaltyRepository.save(account);

        log.info("Evaluated tier for customer {}: {} (rolling spending: {} VND)",
                customerId, account.getTier(), rollingSpending);
    }

    /**
     * Xác nhận tier upgrade (sau thời gian thử thách).
     */
    @Scheduled(cron = "0 0 6 1 * ?") // Ngày 1 mỗi tháng, 06:00
    public void confirmPendingTierUpgrades() {
        log.info("Confirming pending tier upgrades...");
        // TODO: Implement batch processing
    }

    /**
     * Dọn dẹp spending history cũ (rolling window + 1 tháng buffer).
     */
    @Scheduled(cron = "0 30 2 1 * ?") // Ngày 1 mỗi tháng, 02:30
    public void cleanupOldData() {
        log.info("Cleaning up old spending history...");
        if (loyaltyRepository instanceof LoyaltyRepositoryAdapter adapter) {
            adapter.cleanupOldSpendingHistory();
        }
    }
}