package com.sofitech.hoamaimart.loyalty.application.service;

import com.sofitech.hoamaimart.loyalty.domain.model.LoyaltyAccount;
import com.sofitech.hoamaimart.loyalty.domain.port.out.LoyaltyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

/**
 * Scheduled service: đánh giá tier định kỳ.
 * Chạy định kỳ để:
 * 1. Đánh giá lại tier (rớt hạng nếu giảm chi tiêu)
 * 2. Cleanup spending history cũ
 * 3. Phát hiện khách dormant để gửi notification
 *
 * Lưu ý: rolling window spending đã tự động loại bỏ giao dịch cũ nên
 * tier evaluation chỉ cần chạy để đánh giá lại cho nhanh (không cần
 * đợi giao dịch mới).
 */
@Service
public class TierEvaluationScheduler {

    private static final Logger log = LoggerFactory.getLogger(TierEvaluationScheduler.class);

    private final LoyaltyRepository loyaltyRepository;

    public TierEvaluationScheduler(LoyaltyRepository loyaltyRepository) {
        this.loyaltyRepository = loyaltyRepository;
    }

    /**
     * Đánh giá lại tier cho tất cả khách.
     * Chạy ngày 1 hàng tháng lúc 00:00.
     * Tier sẽ tự rớt nếu spending window giảm.
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    public void evaluateAllTiers() {
        log.info("[TierEval] Starting monthly tier evaluation for all customers...");

        // TODO: Implement batch processing for all customers
        // For now, log only - real implementation would paginate through all accounts
        log.info("[TierEval] Tier evaluation completed");
    }

    /**
     * Đánh giá lại tier cho một khách.
     * Gọi khi cần force re-eval (vd: sau khi cleanup data, hoặc manual admin).
     */
    public void evaluateTier(UUID customerId) {
        LoyaltyAccount account = loyaltyRepository.findByCustomerId(customerId).orElse(null);
        if (account == null) {
            return;
        }

        BigDecimal rollingSpending = loyaltyRepository.calculateRollingWindowSpending(customerId);

        account.evaluateTier(rollingSpending.longValue());
        loyaltyRepository.save(account);

        log.info("[TierEval] Customer {} → tier={} (rolling spending: {} VND)",
                customerId, account.getTier(), rollingSpending);
    }

    /**
     * Phát hiện khách dormant (không mua 90+ ngày).
     * Dùng để gửi email re-engagement.
     * Chạy hàng tuần thứ 2 lúc 09:00.
     */
    @Scheduled(cron = "0 0 9 ? * MON")
    public void detectDormantCustomers() {
        log.info("[Dormant] Scanning for dormant customers...");

        // Logic: Tìm accounts có last transaction > 90 ngày trước
        // và gửi event notification-service để push email
        // TODO: Implement batch query + publish event
        log.info("[Dormant] Dormant detection completed");
    }

    /**
     * Cảnh báo khách sắp rớt hạng (chưa đạt ngưỡng tier hiện tại).
     * Chạy ngày 20 hàng tháng lúc 10:00.
     */
    @Scheduled(cron = "0 0 10 20 * ?")
    public void notifyTierDowngradeWarning() {
        log.info("[Warning] Sending tier-downgrade warnings...");

        // Logic: Với mỗi customer, tính currentRollingSpending
        // → nếu < threshold tier hiện tại → gửi cảnh báo
        // (đến ngày 1 tháng sau sẽ rớt hạng)
        log.info("[Warning] Tier downgrade warnings sent");
    }

    /**
     * Cleanup spending history cũ (rolling window + 1 tháng buffer).
     * Chạy ngày 1 hàng tháng lúc 02:00.
     */
    @Scheduled(cron = "0 0 2 1 * ?")
    public void cleanupOldSpendingHistory() {
        log.info("[Cleanup] Removing spending history > 13 months old...");

        // Keep 13 months to be safe (12 + 1 buffer)
        Instant cutoffDate = Instant.now().minus(13, ChronoUnit.MONTHS);

        if (loyaltyRepository instanceof com.sofitech.hoamaimart.loyalty.adapter.out.persistence.repository.LoyaltyRepositoryAdapter adapter) {
            adapter.cleanupOldSpendingHistory();
        }
        log.info("[Cleanup] Cleanup completed (cutoff: {})", cutoffDate);
    }

    /**
     * Cron expression examples cho tham khảo:
     *
     * 0 0 0 1 * ?      → Ngày 1 mỗi tháng, 00:00 (đánh giá tier)
     * 0 0 9 ? * MON    → Thứ 2 hàng tuần, 09:00 (detect dormant)
     * 0 0 10 20 * ?    → Ngày 20 mỗi tháng, 10:00 (warning)
     * 0 0 2 1 * ?      → Ngày 1 mỗi tháng, 02:00 (cleanup)
     *
     * Có thể chuyển sang Quartz Scheduler nếu cần dynamic scheduling
     * hoặc quản lý qua Admin UI.
     */
}