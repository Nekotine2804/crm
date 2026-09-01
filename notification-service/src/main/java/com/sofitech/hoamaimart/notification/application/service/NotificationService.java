package com.sofitech.hoamaimart.notification.application.service;

import com.sofitech.hoamaimart.notification.domain.model.Notification;
import com.sofitech.hoamaimart.notification.domain.model.NotificationType;
import com.sofitech.hoamaimart.notification.domain.model.vo.Channel;
import com.sofitech.hoamaimart.notification.domain.model.vo.CustomerId;
import com.sofitech.hoamaimart.notification.domain.port.out.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Notification save(Notification notification) {
        return repository.save(notification);
    }

    public void sendPointsEarnedNotification(UUID customerId, int points, int newBalance) {
        Notification notification = Notification.forPointsEarned(CustomerId.of(customerId), points, newBalance);
        repository.save(notification);
        log.info("Created POINT_EARNED notification for customer {}", customerId);
    }

    public void sendTierUpgradeNotification(UUID customerId, String newTier) {
        Notification notification = Notification.forTierUpgrade(CustomerId.of(customerId), newTier);
        repository.save(notification);
        log.info("Created TIER_UPGRADE notification for customer {}", customerId);
    }

    public void sendRedeemSuccessNotification(UUID customerId, int points, String reward, int remainingBalance) {
        Notification notification = Notification.forRedeemSuccess(CustomerId.of(customerId), points, reward, remainingBalance);
        repository.save(notification);
        log.info("Created REDEEM_SUCCESS notification for customer {}", customerId);
    }

    public void sendRefundNotification(UUID customerId, String transactionCode, String reason) {
        Notification notification = Notification.forRefund(CustomerId.of(customerId), transactionCode, reason);
        repository.save(notification);
        log.info("Created REFUND notification for customer {}", customerId);
    }

    public List<Notification> getNotificationsByCustomerId(UUID customerId) {
        return repository.findByCustomerId(customerId);
    }

    public List<Notification> getNotificationsByCustomerIdAndType(UUID customerId, NotificationType type) {
        return repository.findByCustomerIdAndType(customerId, type);
    }

    public long countUnreadNotifications(UUID customerId) {
        return repository.countUnreadByCustomerId(customerId);
    }
}
