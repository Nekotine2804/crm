package com.sofitech.hoamaimart.transaction.adapter.in.web.dto;

import com.sofitech.hoamaimart.transaction.domain.model.Transaction;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for refund operation.
 */
public record RefundResponse(
        UUID transactionId,
        String transactionCode,
        UUID customerId,
        String storeId,
        String originalStatus,
        String newStatus,
        String refundReason,
        Instant refundedAt,
        String status
) {
    public static RefundResponse from(Transaction transaction) {
        return new RefundResponse(
                transaction.getId(),
                transaction.getTransactionCode(),
                transaction.getCustomerId(),
                transaction.getStoreId(),
                "COMPLETED",
                transaction.getStatus().name(),
                transaction.getRefundReason(),
                transaction.getUpdatedAt(),
                "SUCCESS"
        );
    }
}
