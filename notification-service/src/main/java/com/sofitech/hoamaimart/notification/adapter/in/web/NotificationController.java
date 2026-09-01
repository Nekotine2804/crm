package com.sofitech.hoamaimart.notification.adapter.in.web;

import com.sofitech.hoamaimart.notification.adapter.in.web.dto.NotificationResponse;
import com.sofitech.hoamaimart.notification.adapter.in.web.dto.UnreadCountResponse;
import com.sofitech.hoamaimart.notification.application.service.NotificationService;
import com.sofitech.hoamaimart.notification.domain.model.Notification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller - Notification API.
 */
@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Notification management endpoints")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * GET /api/notifications/customers/{customerId}
     * Lấy tất cả notifications của customer.
     */
    @Operation(summary = "Get notifications by customer", description = "Retrieves all notifications for a customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notifications retrieved successfully")
    })
    @GetMapping("/customers/{customerId}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByCustomer(
            @Parameter(description = "Customer UUID") @PathVariable UUID customerId
    ) {
        List<Notification> notifications = notificationService.getNotificationsByCustomerId(customerId);
        List<NotificationResponse> response = notifications.stream()
                .map(NotificationResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/notifications/customers/{customerId}/unread-count
     * Đếm số notification chưa đọc.
     */
    @Operation(summary = "Get unread notification count", description = "Returns the count of unread notifications for a customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Count retrieved successfully")
    })
    @GetMapping("/customers/{customerId}/unread-count")
    public ResponseEntity<UnreadCountResponse> getUnreadCount(
            @Parameter(description = "Customer UUID") @PathVariable UUID customerId
    ) {
        long count = notificationService.countUnreadNotifications(customerId);
        return ResponseEntity.ok(new UnreadCountResponse(customerId, count));
    }

    /**
     * GET /api/notifications/customers/{customerId}/health
     * Kiểm tra notification service healthy.
     */
    @GetMapping("/customers/{customerId}/health")
    public ResponseEntity<Map<String, Object>> healthCheck(@PathVariable UUID customerId) {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "customerId", customerId.toString(),
                "timestamp", java.time.Instant.now().toString()
        ));
    }
}
