package com.sofitech.hoamaimart.notification.adapter.in.web.dto;

import com.sofitech.hoamaimart.notification.domain.model.Notification;
import com.sofitech.hoamaimart.notification.domain.model.NotificationStatus;
import com.sofitech.hoamaimart.notification.domain.model.NotificationType;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for Notification.
 */
public record NotificationResponse(
        UUID id,
        NotificationType type,
        NotificationStatus status,
        String title,
        String content,
        String channel,
        Instant createdAt,
        Instant sentAt
) {
    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getType(),
                notification.getStatus(),
                notification.getTitle().value(),
                notification.getContent().value(),
                notification.getChannel().value(),
                notification.getCreatedAt(),
                notification.getSentAt()
        );
    }
}
