package com.sofitech.hoamaimart.notification.adapter.out.persistence.repository;

import com.sofitech.hoamaimart.notification.adapter.out.persistence.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationJpaRepository extends JpaRepository<NotificationEntity, UUID> {
    List<NotificationEntity> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);

    List<NotificationEntity> findByCustomerIdAndTypeOrderByCreatedAtDesc(UUID customerId, String type);

    long countByCustomerIdAndStatusNot(UUID customerId, String status);
}
