package com.sofitech.hoamaimart.loyalty.domain.port.out;

import com.sofitech.hoamaimart.loyalty.domain.model.PointTransaction;
import com.sofitech.hoamaimart.loyalty.domain.model.PointTransactionType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port OUT: repository cho PointTransaction.
 */
public interface PointTransactionRepository {

    /**
     * Lưu point transaction.
     */
    PointTransaction save(PointTransaction transaction);

    /**
     * Tìm point transaction theo ID.
     */
    Optional<PointTransaction> findById(UUID id);

    /**
     * Tìm tất cả point transaction của customer, sắp xếp theo thời gian giảm dần.
     */
    List<PointTransaction> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);

    /**
     * Tìm point transaction theo customerId và loại.
     */
    List<PointTransaction> findByCustomerIdAndType(UUID customerId, PointTransactionType type);

    /**
     * Kiểm tra đã có point transaction cho referenceId chưa (idempotency).
     */
    boolean existsByReferenceIdAndType(String referenceId, PointTransactionType type);

    /**
     * Tìm point transaction theo referenceId.
     */
    List<PointTransaction> findByReferenceId(String referenceId);
}
