package com.sofitech.hoamaimart.loyalty.adapter.out.persistence.repository;

import com.sofitech.hoamaimart.loyalty.adapter.out.persistence.entity.PointTransactionEntity;
import com.sofitech.hoamaimart.loyalty.domain.model.PointTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * JPA repository cho PointTransaction.
 */
@Repository
public interface PointTransactionJpaRepository extends JpaRepository<PointTransactionEntity, UUID> {

    /**
     * Tìm tất cả giao dịch điểm theo customerId, sắp xếp theo thời gian giảm dần.
     */
    List<PointTransactionEntity> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);

    /**
     * Tìm giao dịch điểm theo customerId với phân trang.
     */
    Page<PointTransactionEntity> findByCustomerId(UUID customerId, Pageable pageable);

    /**
     * Tìm giao dịch điểm theo customerId và loại.
     */
    List<PointTransactionEntity> findByCustomerIdAndTypeOrderByCreatedAtDesc(UUID customerId, PointTransactionType type);

    /**
     * Tìm giao dịch điểm theo referenceId (ví dụ: transaction ID).
     */
    List<PointTransactionEntity> findByReferenceId(String referenceId);

    /**
     * Kiểm tra xem đã có giao dịch EARN cho transactionId chưa (idempotency).
     */
    boolean existsByReferenceIdAndType(String referenceId, PointTransactionType type);

    /**
     * Đếm tổng điểm đã tích (EARN) của customer.
     */
    @Query("SELECT COALESCE(SUM(p.points), 0) FROM PointTransactionEntity p WHERE p.customerId = :customerId AND p.type = 'EARN'")
    int sumEarnedPointsByCustomerId(@Param("customerId") UUID customerId);

    /**
     * Đếm tổng điểm đã đổi (REDEEM) của customer.
     */
    @Query("SELECT COALESCE(SUM(ABS(p.points)), 0) FROM PointTransactionEntity p WHERE p.customerId = :customerId AND p.type = 'REDEEM'")
    int sumRedeemedPointsByCustomerId(@Param("customerId") UUID customerId);
}
