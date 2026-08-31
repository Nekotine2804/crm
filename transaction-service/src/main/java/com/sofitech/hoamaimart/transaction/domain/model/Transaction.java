package com.sofitech.hoamaimart.transaction.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Transaction aggregate root - thuần Java, không phụ thuộc Spring/JPA.
 */
public class Transaction {

    public enum Status {
        PENDING, COMPLETED, CANCELLED
    }

    private final UUID id;
    private final UUID customerId;
    private final String storeId;
    private final Amount amount;
    private final String transactionCode;
    private final Status status;
    private final Instant createdAt;

    public Transaction(UUID id, UUID customerId, String storeId, Amount amount,
                       String transactionCode, Status status, Instant createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.storeId = storeId;
        this.amount = amount;
        this.transactionCode = transactionCode;
        this.status = status;
        this.createdAt = createdAt;
    }

    /**
     * Factory method: tạo transaction mới (đã completed).
     */
    public static Transaction create(UUID customerId, String storeId, String transactionCode, BigDecimal amountValue) {
        if (transactionCode == null || transactionCode.isBlank()) {
            throw new IllegalArgumentException("transactionCode không được để trống");
        }
        return new Transaction(
                UUID.randomUUID(),
                customerId,
                storeId,
                Amount.of(amountValue),
                transactionCode,
                Status.COMPLETED,
                Instant.now()
        );
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getCustomerId() { return customerId; }
    public String getStoreId() { return storeId; }
    public Amount getAmount() { return amount; }
    public String getTransactionCode() { return transactionCode; }
    public Status getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public BigDecimal getAmountValue() { return amount.value(); }
}