package com.sofitech.hoamaimart.notification.domain.model;

import com.sofitech.hoamaimart.notification.domain.model.vo.Channel;
import com.sofitech.hoamaimart.notification.domain.model.vo.CustomerId;
import com.sofitech.hoamaimart.notification.domain.model.vo.NotificationContent;
import com.sofitech.hoamaimart.notification.domain.model.vo.NotificationTitle;

import java.time.Instant;
import java.util.UUID;

/**
 * Domain model: Notification.
 * Sử dụng Value Objects để đảm bảo tính nhất quán của dữ liệu.
 */
public class Notification {

    private final UUID id;
    private final CustomerId customerId;
    private final NotificationType type;
    private NotificationStatus status;
    private final NotificationTitle title;
    private final NotificationContent content;
    private final Channel channel;
    private final Instant createdAt;
    private Instant sentAt;

    public Notification(
            UUID id,
            CustomerId customerId,
            NotificationType type,
            NotificationStatus status,
            NotificationTitle title,
            NotificationContent content,
            Channel channel,
            Instant createdAt,
            Instant sentAt
    ) {
        this.id = id;
        this.customerId = customerId;
        this.type = type;
        this.status = status;
        this.title = title;
        this.content = content;
        this.channel = channel;
        this.createdAt = createdAt;
        this.sentAt = sentAt;
    }

    /**
     * Factory: tạo notification tích điểm.
     */
    public static Notification forPointsEarned(CustomerId customerId, int points, int newBalance) {
        return new Notification(
                UUID.randomUUID(),
                customerId,
                NotificationType.POINT_EARNED,
                NotificationStatus.PENDING,
                NotificationTitle.of("Tích điểm thành công! 🎉"),
                NotificationContent.of(String.format("Bạn đã được cộng %d điểm. Số dư hiện tại: %d điểm", points, newBalance)),
                Channel.SMS(),
                Instant.now(),
                null
        );
    }

    /**
     * Factory: tạo notification lên tier.
     */
    public static Notification forTierUpgrade(CustomerId customerId, String newTier) {
        return new Notification(
                UUID.randomUUID(),
                customerId,
                NotificationType.TIER_UPGRADE,
                NotificationStatus.PENDING,
                NotificationTitle.of("Chúc mừng bạn lên hạng " + newTier + "! ⭐"),
                NotificationContent.of(String.format("Xin chúc mừng! Bạn đã được nâng hạng lên %s. Cảm ơn bạn đã đồng hành cùng Hoa Mai Mart!", newTier)),
                Channel.SMS(),
                Instant.now(),
                null
        );
    }

    /**
     * Factory: tạo notification redeem thành công.
     */
    public static Notification forRedeemSuccess(CustomerId customerId, int points, String reward, int remainingBalance) {
        return new Notification(
                UUID.randomUUID(),
                customerId,
                NotificationType.REDEEM_SUCCESS,
                NotificationStatus.PENDING,
                NotificationTitle.of("Đổi điểm thành công! 🎁"),
                NotificationContent.of(String.format("Bạn đã đổi %d điểm lấy %s. Số dư còn lại: %d điểm", points, reward, remainingBalance)),
                Channel.SMS(),
                Instant.now(),
                null
        );
    }

    /**
     * Factory: tạo notification refund.
     */
    public static Notification forRefund(CustomerId customerId, String transactionCode, String reason) {
        return new Notification(
                UUID.randomUUID(),
                customerId,
                NotificationType.REFUND,
                NotificationStatus.PENDING,
                NotificationTitle.of("Thông báo hoàn tiền 💰"),
                NotificationContent.of(String.format("Giao dịch %s đã được hoàn tiền. Lý do: %s", transactionCode, reason)),
                Channel.SMS(),
                Instant.now(),
                null
        );
    }

    /**
     * Đánh dấu đã gửi.
     */
    public void markAsSent() {
        this.status = NotificationStatus.SENT;
        this.sentAt = Instant.now();
    }

    /**
     * Đánh dấu đã đọc.
     */
    public void markAsRead() {
        this.status = NotificationStatus.READ;
    }

    // Getters
    public UUID getId() { return id; }
    public CustomerId getCustomerId() { return customerId; }
    public NotificationType getType() { return type; }
    public NotificationStatus getStatus() { return status; }
    public NotificationTitle getTitle() { return title; }
    public NotificationContent getContent() { return content; }
    public Channel getChannel() { return channel; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getSentAt() { return sentAt; }
}
