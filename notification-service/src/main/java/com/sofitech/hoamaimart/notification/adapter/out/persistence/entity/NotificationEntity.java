package com.sofitech.hoamaimart.notification.adapter.out.persistence.entity;

import com.sofitech.hoamaimart.notification.domain.model.Notification;
import com.sofitech.hoamaimart.notification.domain.model.NotificationStatus;
import com.sofitech.hoamaimart.notification.domain.model.NotificationType;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA Entity cho bảng notifications.
 */
@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notification_customer_id", columnList = "customer_id"),
        @Index(name = "idx_notification_type", columnList = "type"),
        @Index(name = "idx_notification_status", columnList = "status"),
        @Index(name = "idx_notification_created_at", columnList = "created_at")
})
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private NotificationStatus status;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", nullable = false, length = 500)
    private String content;

    @Column(name = "channel", nullable = false, length = 20)
    private String channel;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "sent_at")
    private Instant sentAt;

    protected NotificationEntity() {}

    public Notification toDomain() {
        return new Notification(
                this.id,
                com.sofitech.hoamaimart.notification.domain.model.vo.CustomerId.of(this.customerId),
                this.type,
                this.status,
                com.sofitech.hoamaimart.notification.domain.model.vo.NotificationTitle.of(this.title),
                com.sofitech.hoamaimart.notification.domain.model.vo.NotificationContent.of(this.content),
                com.sofitech.hoamaimart.notification.domain.model.vo.Channel.of(this.channel),
                this.createdAt,
                this.sentAt
        );
    }

    public static NotificationEntity fromDomain(Notification notification) {
        NotificationEntity entity = new NotificationEntity();
        entity.id = notification.getId();
        entity.customerId = notification.getCustomerId().value();
        entity.type = notification.getType();
        entity.status = notification.getStatus();
        entity.title = notification.getTitle().value();
        entity.content = notification.getContent().value();
        entity.channel = notification.getChannel().value();
        entity.createdAt = notification.getCreatedAt();
        entity.sentAt = notification.getSentAt();
        return entity;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getCustomerId() { return customerId; }
    public NotificationType getType() { return type; }
    public NotificationStatus getStatus() { return status; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getChannel() { return channel; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getSentAt() { return sentAt; }
}
