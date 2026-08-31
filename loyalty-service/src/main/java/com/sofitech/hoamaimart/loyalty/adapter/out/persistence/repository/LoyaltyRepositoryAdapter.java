package com.sofitech.hoamaimart.loyalty.adapter.out.persistence.repository;

import com.sofitech.hoamaimart.loyalty.adapter.out.persistence.entity.LoyaltyAccountEntity;
import com.sofitech.hoamaimart.loyalty.adapter.out.persistence.entity.SpendingHistoryEntity;
import com.sofitech.hoamaimart.loyalty.domain.model.LoyaltyAccount;
import com.sofitech.hoamaimart.loyalty.domain.port.out.LoyaltyRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter OUT: implement LoyaltyRepository dùng JPA.
 */
@Repository
public class LoyaltyRepositoryAdapter implements LoyaltyRepository {

    private static final int ROLLING_WINDOW_MONTHS = 12;

    private final LoyaltyJpaRepository jpaRepository;
    private final SpendingHistoryJpaRepository spendingRepository;

    public LoyaltyRepositoryAdapter(
            LoyaltyJpaRepository jpaRepository,
            SpendingHistoryJpaRepository spendingRepository
    ) {
        this.jpaRepository = jpaRepository;
        this.spendingRepository = spendingRepository;
    }

    @Override
    public LoyaltyAccount save(LoyaltyAccount account) {
        LoyaltyAccountEntity entity = LoyaltyAccountEntity.fromDomain(account);
        return jpaRepository.save(entity).toDomain();
    }

    @Override
    public Optional<LoyaltyAccount> findByCustomerId(UUID customerId) {
        return jpaRepository.findByCustomerId(customerId).map(LoyaltyAccountEntity::toDomain);
    }

    /**
     * Lưu lịch sử chi tiêu (gọi khi nhận transaction event).
     */
    @Transactional
    public void recordSpending(UUID customerId, UUID transactionId, BigDecimal amount, Instant spentAt) {
        SpendingHistoryEntity entity = new SpendingHistoryEntity(
                UUID.randomUUID(),
                customerId,
                transactionId,
                amount,
                spentAt,
                Instant.now()
        );
        spendingRepository.save(entity);
    }

    /**
     * Tính tổng chi tiêu trong rolling window (12 tháng).
     */
    public BigDecimal calculateRollingWindowSpending(UUID customerId) {
        Instant fromDate = Instant.now().minus(ROLLING_WINDOW_MONTHS, ChronoUnit.MONTHS);
        return spendingRepository.sumSpendingSince(customerId, fromDate);
    }

    /**
     * Lấy thời điểm giao dịch cuối cùng.
     */
    public Optional<Instant> findLastTransactionAt(UUID customerId) {
        return Optional.ofNullable(spendingRepository.findLastTransactionAt(customerId));
    }

    /**
     * Dọn dẹp data cũ (chạy định kỳ).
     */
    @Transactional
    public void cleanupOldSpendingHistory() {
        Instant cutoffDate = Instant.now().minus(ROLLING_WINDOW_MONTHS + 1, ChronoUnit.MONTHS);
        spendingRepository.deleteBySpentAtBefore(cutoffDate);
    }
}