package com.sofitech.hoamaimart.notification.application.service;

import com.sofitech.hoamaimart.notification.domain.model.Notification;
import com.sofitech.hoamaimart.notification.domain.model.NotificationType;
import com.sofitech.hoamaimart.notification.domain.model.vo.CustomerId;
import com.sofitech.hoamaimart.notification.domain.port.out.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

/**
 * Application service: xử lý notification.
 */
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    /**
     * Gửi notification tích điểm.
     */
    public Notification sendPointsEarnedNotification(UUID customerId, int points, int newBalance) {
        Notification notification = Notification.forPointsEarned(
                CustomerId.of(customerId),
                points,
                newBalance
        );
        Notification saved = notificationRepository.save(notification);
        log.info("Created points earned notification for customer {}: {} points", customerId, points);
        return saved;
    }

    /**
     * Gửi notification lên tier.
     */
    public Notification sendTierUpgradeNotification(UUID customerId, String newTier) {
        Notification notification = Notification.forTierUpgrade(
                CustomerId.of(customerId),
                newTier
        );
        Notification saved = notificationRepository.save(notification);
        log.info("Created tier upgrade notification for customer {}: new tier {}", customerId, newTier);
        return saved;
    }

    /**
     * Gửi notification redeem thành công.
     */
    public Notification sendRedeemSuccessNotification(UUID customerId, int points, String reward, int remainingBalance) {
        Notification notification = Notification.forRedeemSuccess(
                CustomerId.of(customerId),
                points,
                reward,
                remainingBalance
        );
        Notification saved = notificationRepository.save(notification);
        log.info("Created redeem success notification for customer {}: {} points for {}", customerId, points, reward);
        return saved;
    }

    /**
     * Gửi notification refund.
     */
    public Notification sendRefundNotification(UUID customerId, String transactionCode, String reason) {
        Notification notification = Notification.forRefund(
                CustomerId.of(customerId),
                transactionCode,
                reason
        );
        Notification saved = notificationRepository.save(notification);
        log.info("Created refund notification for customer {}: transaction {}", customerId, transactionCode);
        return saved;
    }

    /**
     * Lấy notification history của customer.
     */
    public List<Notification> getNotificationsByCustomerId(UUID customerId) {
        return notificationRepository.findByCustomerId(customerId);
    }

    /**
     * Lấy notification theo loại.
     */
    public List<Notification> getNotificationsByCustomerIdAndType(UUID customerId, NotificationType type) {
        return notificationRepository.findByCustomerIdAndType(customerId, type);
    }

    /**
     * Đếm notification chưa đọc.
     */
    public long countUnreadNotifications(UUID customerId) {
        return notificationRepository.countUnreadByCustomerId(customerId);
    }
}
