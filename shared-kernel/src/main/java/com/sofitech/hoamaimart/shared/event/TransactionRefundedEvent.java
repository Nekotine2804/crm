package com.sofitech.hoamaimart.shared.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Event emitted when a completed transaction is refunded. */
public record TransactionRefundedEvent(
        UUID eventId, Instant occurredAt, String eventType,
        UUID transactionId, String transactionCode, UUID customerId,
        String storeId, BigDecimal originalAmount, String refundReason,
        Instant refundedAt
) implements DomainEvent {

    public TransactionRefundedEvent(UUID transactionId, String transactionCode,
                                    UUID customerId, String storeId,
                                    BigDecimal originalAmount, String refundReason) {
        this(UUID.randomUUID(), Instant.now(), "transaction.refunded",
                transactionId, transactionCode, customerId, storeId,
                originalAmount, refundReason, Instant.now());
    }

    public UUID getTransactionId() { return transactionId; }
    public String getTransactionCode() { return transactionCode; }
    public UUID getCustomerId() { return customerId; }
    public String getStoreId() { return storeId; }
    public BigDecimal getOriginalAmount() { return originalAmount; }
    public String getRefundReason() { return refundReason; }
    public Instant getRefundedAt() { return refundedAt; }
}
