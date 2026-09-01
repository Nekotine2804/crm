package com.sofitech.hoamaimart.notification.domain.port.out;

import com.sofitech.hoamaimart.notification.domain.model.Notification;
import com.sofitech.hoamaimart.notification.domain.model.NotificationType;

import java.util.List;
import java.util.UUID;

/**
 * Port OUT: repository interface cho Notification.
 */
public interface NotificationRepository {

    Notification save(Notification notification);

    List<Notification> findByCustomerId(UUID customerId);

    List<Notification> findByCustomerIdAndType(UUID customerId, NotificationType type);

    long countUnreadByCustomerId(UUID customerId);
}
