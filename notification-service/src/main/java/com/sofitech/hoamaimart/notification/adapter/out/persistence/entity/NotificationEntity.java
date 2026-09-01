package com.sofitech.hoamaimart.notification.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "notifications")
public class NotificationEntity {

    @Id
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "type", nullable = false, length = 30)
    private String type;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", nullable = false, length = 500)
    private String content;

    @Column(name = "channel", nullable = false, length = 20)
    private String channel;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "sent_at")
    private Instant sentAt;

    public NotificationEntity() {}

    public NotificationEntity(UUID id, UUID customerId, String type, String status,
                            String title, String content, String channel,
                            Instant createdAt, Instant sentAt) {
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

    public UUID getId() { return id; }
    public UUID getCustomerId() { return customerId; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getChannel() { return channel; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getSentAt() { return sentAt; }

    public void setId(UUID id) { this.id = id; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public void setType(String type) { this.type = type; }
    public void setStatus(String status) { this.status = status; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setChannel(String channel) { this.channel = channel; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }

    public com.sofitech.hoamaimart.notification.domain.model.Notification toDomain() {
        return new com.sofitech.hoamaimart.notification.domain.model.Notification(
                this.id,
                com.sofitech.hoamaimart.notification.domain.model.vo.CustomerId.of(this.customerId),
                com.sofitech.hoamaimart.notification.domain.model.NotificationType.valueOf(this.type),
                com.sofitech.hoamaimart.notification.domain.model.NotificationStatus.valueOf(this.status),
                com.sofitech.hoamaimart.notification.domain.model.vo.NotificationTitle.of(this.title),
                com.sofitech.hoamaimart.notification.domain.model.vo.NotificationContent.of(this.content),
                com.sofitech.hoamaimart.notification.domain.model.vo.Channel.of(this.channel),
                this.createdAt,
                this.sentAt
        );
    }

    public static NotificationEntity fromDomain(com.sofitech.hoamaimart.notification.domain.model.Notification notification) {
        NotificationEntity entity = new NotificationEntity();
        entity.setId(notification.getId());
        entity.setCustomerId(notification.getCustomerId().value());
        entity.setType(notification.getType().name());
        entity.setStatus(notification.getStatus().name());
        entity.setTitle(notification.getTitle().value());
        entity.setContent(notification.getContent().value());
        entity.setChannel(notification.getChannel().value());
        entity.setCreatedAt(notification.getCreatedAt());
        entity.setSentAt(notification.getSentAt());
        return entity;
    }
}
