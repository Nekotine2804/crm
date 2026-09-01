package com.sofitech.hoamaimart.shared.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Event emitted after a POS transaction completes. */
public record TransactionCompletedEvent(
        UUID eventId, Instant occurredAt, String eventType,
        UUID transactionId, String transactionCode, UUID customerId,
        String storeId, BigDecimal amount, Instant transactionTime
) implements DomainEvent {

    public TransactionCompletedEvent(UUID transactionId, String transactionCode,
                                     UUID customerId, String storeId,
                                     BigDecimal amount, Instant transactionTime) {
        this(UUID.randomUUID(), Instant.now(), "transaction.completed",
                transactionId, transactionCode, customerId, storeId, amount, transactionTime);
    }

    public UUID getTransactionId() { return transactionId; }
    public String getTransactionCode() { return transactionCode; }
    public UUID getCustomerId() { return customerId; }
    public String getStoreId() { return storeId; }
    public BigDecimal getAmount() { return amount; }
    public Instant getTransactionTime() { return transactionTime; }
}
