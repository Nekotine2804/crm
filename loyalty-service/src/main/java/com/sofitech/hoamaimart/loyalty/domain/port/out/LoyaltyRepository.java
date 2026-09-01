package com.sofitech.hoamaimart.loyalty.domain.port.out;

import com.sofitech.hoamaimart.loyalty.domain.model.LoyaltyAccount;
import com.sofitech.hoamaimart.loyalty.domain.model.PointTransaction;
import com.sofitech.hoamaimart.loyalty.domain.model.PointTransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
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

    // ===== Point Transaction methods =====

    /**
     * Lưu point transaction.
     */
    PointTransaction savePointTransaction(PointTransaction transaction);

    /**
     * Tìm tất cả point transaction của customer.
     */
    List<PointTransaction> findPointTransactionsByCustomerId(UUID customerId);

    /**
     * Tìm point transaction theo loại.
     */
    List<PointTransaction> findPointTransactionsByCustomerIdAndType(UUID customerId, PointTransactionType type);

    /**
     * Kiểm tra đã có point transaction cho referenceId chưa.
     */
    boolean existsPointTransactionByReferenceIdAndType(String referenceId, PointTransactionType type);
}
