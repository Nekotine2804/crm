package com.sofitech.hoamaimart.shared.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Event published when a transaction is refunded.
 * Consumed by loyalty-service to reverse loyalty points.
 */
public class TransactionRefundedEvent implements DomainEvent {

    private final UUID eventId;
    private final Instant occurredAt;
    private final String eventType;

    private final UUID transactionId;
    private final String transactionCode;
    private final UUID customerId;
    private final String storeId;
    private final BigDecimal originalAmount;
    private final String refundReason;
    private final Instant refundedAt;

    public TransactionRefundedEvent(
            UUID transactionId,
            String transactionCode,
            UUID customerId,
            String storeId,
            BigDecimal originalAmount,
            String refundReason
    ) {
        this.eventId = UUID.randomUUID();
        this.occurredAt = Instant.now();
        this.eventType = "transaction.refunded";
        this.transactionId = transactionId;
        this.transactionCode = transactionCode;
        this.customerId = customerId;
        this.storeId = storeId;
        this.originalAmount = originalAmount;
        this.refundReason = refundReason;
        this.refundedAt = Instant.now();
    }

    @Override
    public UUID eventId() {
        return eventId;
    }

    @Override
    public Instant occurredAt() {
        return occurredAt;
    }

    @Override
    public String eventType() {
        return eventType;
    }

    public UUID getTransactionId() { return transactionId; }
    public String getTransactionCode() { return transactionCode; }
    public UUID getCustomerId() { return customerId; }
    public String getStoreId() { return storeId; }
    public BigDecimal getOriginalAmount() { return originalAmount; }
    public String getRefundReason() { return refundReason; }
    public Instant getRefundedAt() { return refundedAt; }
}
