package com.sofitech.hoamaimart.loyalty.domain.port.out;

import com.sofitech.hoamaimart.loyalty.domain.model.LoyaltyAccount;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Port OUT: repository interface.
 */
public interface LoyaltyRepository {

    LoyaltyAccount save(LoyaltyAccount account);

    Optional<LoyaltyAccount> findByCustomerId(UUID customerId);

    /**
     * Ghi nhận chi tiêu cho rolling window.
     */
    void recordSpending(UUID customerId, UUID transactionId, BigDecimal amount, Instant spentAt);

    /**
     * Tính tổng chi tiêu trong rolling window.
     */
    BigDecimal calculateRollingWindowSpending(UUID customerId);

    /**
     * Tìm thời điểm giao dịch cuối cùng.
     */
    Optional<Instant> findLastTransactionAt(UUID customerId);
}