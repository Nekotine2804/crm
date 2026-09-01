package com.sofitech.hoamaimart.notification.adapter.out.persistence.repository;

import com.sofitech.hoamaimart.notification.adapter.out.persistence.entity.NotificationEntity;
import com.sofitech.hoamaimart.notification.domain.model.NotificationStatus;
import com.sofitech.hoamaimart.notification.domain.model.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * JPA Repository cho Notification.
 */
@Repository
public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, UUID> {

    List<NotificationEntity> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);

    List<NotificationEntity> findByCustomerIdAndTypeOrderByCreatedAtDesc(UUID customerId, NotificationType type);

    @Query("SELECT COUNT(n) FROM NotificationEntity n WHERE n.customerId = :customerId AND n.status != :status")
    long countByCustomerIdAndStatusNot(@Param("customerId") UUID customerId, @Param("status") NotificationStatus status);
}
