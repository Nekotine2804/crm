package com.sofitech.hoamaimart.notification.adapter.out.persistence.repository;

import com.sofitech.hoamaimart.notification.adapter.out.persistence.entity.NotificationEntity;
import com.sofitech.hoamaimart.notification.domain.model.Notification;
import com.sofitech.hoamaimart.notification.domain.model.NotificationType;
import com.sofitech.hoamaimart.notification.domain.port.out.NotificationRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class NotificationRepositoryAdapter implements NotificationRepository {

    private final NotificationJpaRepository jpaRepository;

    public NotificationRepositoryAdapter(NotificationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Notification save(Notification notification) {
        NotificationEntity entity = NotificationEntity.fromDomain(notification);
        return jpaRepository.save(entity).toDomain();
    }

    @Override
    public List<Notification> findByCustomerId(UUID customerId) {
        return jpaRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(NotificationEntity::toDomain)
                .toList();
    }

    @Override
    public List<Notification> findByCustomerIdAndType(UUID customerId, NotificationType type) {
        return jpaRepository.findByCustomerIdAndTypeOrderByCreatedAtDesc(customerId, type.name())
                .stream()
                .map(NotificationEntity::toDomain)
                .toList();
    }

    @Override
    public long countUnreadByCustomerId(UUID customerId) {
        return jpaRepository.countByCustomerIdAndStatusNot(customerId, "READ");
    }
}
