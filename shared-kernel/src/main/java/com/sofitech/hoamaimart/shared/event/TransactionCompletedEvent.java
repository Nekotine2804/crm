package com.sofitech.hoamaimart.shared.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Event published when a transaction (receipt/invoice) is completed at POS.
 * Consumed by loyalty-service to calculate and award loyalty points.
 */
public class TransactionCompletedEvent implements DomainEvent {

    private final UUID eventId;
    private final Instant occurredAt;
    private final String eventType;

    private final UUID transactionId;
    private final UUID customerId;
    private final String storeId;
    private final BigDecimal amount;

    public TransactionCompletedEvent(
            UUID transactionId,
            UUID customerId,
            String storeId,
            BigDecimal amount
    ) {
        this.eventId = UUID.randomUUID();
        this.occurredAt = Instant.now();
        this.eventType = "transaction.completed";
        this.transactionId = transactionId;
        this.customerId = customerId;
        this.storeId = storeId;
        this.amount = amount;
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

    public UUID getTransactionId() {
        return transactionId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public String getStoreId() {
        return storeId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}