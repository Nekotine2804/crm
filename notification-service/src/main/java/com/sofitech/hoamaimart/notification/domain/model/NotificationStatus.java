package com.sofitech.hoamaimart.notification.domain.model;

/**
 * Trạng thái notification.
 */
public enum NotificationStatus {
    PENDING,    // Chờ gửi
    SENT,       // Đã gửi
    FAILED,     // Gửi thất bại
    READ        // Đã đọc
}
