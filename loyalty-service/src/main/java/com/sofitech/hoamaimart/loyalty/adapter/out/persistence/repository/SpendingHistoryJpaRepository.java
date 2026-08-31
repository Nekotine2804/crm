package com.sofitech.hoamaimart.loyalty.adapter.out.persistence.repository;

import com.sofitech.hoamaimart.loyalty.adapter.out.persistence.entity.SpendingHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Repository để tính rolling window spending.
 */
@Repository
public interface SpendingHistoryJpaRepository extends JpaRepository<SpendingHistoryEntity, UUID> {

    /**
     * Tính tổng chi tiêu trong khoảng thời gian rolling window (12 tháng).
     */
    @Query("SELECT COALESCE(SUM(s.amount), 0) FROM SpendingHistoryEntity s " +
           "WHERE s.customerId = :customerId AND s.spentAt >= :fromDate")
    BigDecimal sumSpendingSince(
            @Param("customerId") UUID customerId,
            @Param("fromDate") Instant fromDate
    );

    /**
     * Tìm thời điểm giao dịch cuối cùng của khách.
     */
    @Query("SELECT MAX(s.spentAt) FROM SpendingHistoryEntity s WHERE s.customerId = :customerId")
    Instant findLastTransactionAt(@Param("customerId") UUID customerId);

    /**
     * Xóa các record cũ hơn rolling window (để giữ DB nhẹ).
     */
    void deleteBySpentAtBefore(Instant cutoffDate);
}