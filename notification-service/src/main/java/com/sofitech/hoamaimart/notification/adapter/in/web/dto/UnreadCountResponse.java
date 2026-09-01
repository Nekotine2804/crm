package com.sofitech.hoamaimart.notification.adapter.in.web.dto;

import java.util.UUID;

/**
 * Response DTO for unread count.
 */
public record UnreadCountResponse(
        UUID customerId,
        long unreadCount
) {}
