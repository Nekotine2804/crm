package com.sofitech.hoamaimart.notification.adapter.in.web;

import com.sofitech.hoamaimart.notification.application.service.NotificationService;
import com.sofitech.hoamaimart.notification.adapter.in.web.dto.NotificationResponse;
import com.sofitech.hoamaimart.notification.adapter.in.web.dto.UnreadCountResponse;
import com.sofitech.hoamaimart.notification.domain.model.NotificationType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(summary = "Get notifications by customer")
    @GetMapping("/customers/{customerId}")
    public ResponseEntity<List<NotificationResponse>> getNotifications(@PathVariable UUID customerId) {
        List<NotificationResponse> response = notificationService.getNotificationsByCustomerId(customerId)
                .stream()
                .map(NotificationResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get notifications by customer and type")
    @GetMapping("/customers/{customerId}/type/{type}")
    public ResponseEntity<List<NotificationResponse>> getNotificationsByType(
            @PathVariable UUID customerId,
            @PathVariable NotificationType type) {
        List<NotificationResponse> response = notificationService
                .getNotificationsByCustomerIdAndType(customerId, type)
                .stream()
                .map(NotificationResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Count unread notifications")
    @GetMapping("/customers/{customerId}/unread/count")
    public ResponseEntity<UnreadCountResponse> countUnread(@PathVariable UUID customerId) {
        return ResponseEntity.ok(new UnreadCountResponse(
                customerId,
                notificationService.countUnreadNotifications(customerId)
        ));
    }

    @Operation(summary = "Health check")
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }
}
