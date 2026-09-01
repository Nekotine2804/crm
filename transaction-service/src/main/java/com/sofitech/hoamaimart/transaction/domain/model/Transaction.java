package com.sofitech.hoamaimart.transaction.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Transaction aggregate root - thuần Java, không phụ thuộc Spring/JPA.
 */
public class Transaction {

    public enum Status {
        PENDING, COMPLETED, CANCELLED, REFUNDED
    }

    private final UUID id;
    private final UUID customerId;
    private final String storeId;
    private final Amount amount;
    private final String transactionCode;
    private Status status;
    private final Instant createdAt;
    private Instant updatedAt;
    private Instant cancelledAt;
    private String refundReason;

    public Transaction(UUID id, UUID customerId, String storeId, Amount amount,
                       String transactionCode, Status status, Instant createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.storeId = storeId;
        this.amount = amount;
        this.transactionCode = transactionCode;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    // Builder-style constructor for updates
    private Transaction(UUID id, UUID customerId, String storeId, Amount amount,
                        String transactionCode, Status status, Instant createdAt,
                        Instant updatedAt, Instant cancelledAt, String refundReason) {
        this.id = id;
        this.customerId = customerId;
        this.storeId = storeId;
        this.amount = amount;
        this.transactionCode = transactionCode;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.cancelledAt = cancelledAt;
        this.refundReason = refundReason;
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

    /**
     * Kiểm tra transaction có thể refund được không.
     */
    public boolean canRefund() {
        return this.status == Status.COMPLETED;
    }

    /**
     * Kiểm tra transaction có thể cancel được không.
     */
    public boolean canCancel() {
        return this.status == Status.PENDING;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getCustomerId() { return customerId; }
    public String getStoreId() { return storeId; }
    public Amount getAmount() { return amount; }
    public String getTransactionCode() { return transactionCode; }
    public Status getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Instant getCancelledAt() { return cancelledAt; }
    public String getRefundReason() { return refundReason; }
    public BigDecimal getAmountValue() { return amount.value(); }

    // Builder pattern for immutable updates
    public Transaction withStatus(Status newStatus) {
        return new Transaction(
                this.id, this.customerId, this.storeId, this.amount,
                this.transactionCode, newStatus, this.createdAt,
                Instant.now(), this.cancelledAt, this.refundReason
        );
    }

    public Transaction cancel() {
        if (!canCancel()) {
            throw new IllegalStateException("Không thể cancel transaction ở trạng thái: " + status);
        }
        return new Transaction(
                this.id, this.customerId, this.storeId, this.amount,
                this.transactionCode, Status.CANCELLED, this.createdAt,
                Instant.now(), Instant.now(), null
        );
    }

    public Transaction refund(String reason) {
        if (!canRefund()) {
            throw new IllegalStateException("Không thể refund transaction ở trạng thái: " + status);
        }
        return new Transaction(
                this.id, this.customerId, this.storeId, this.amount,
                this.transactionCode, Status.REFUNDED, this.createdAt,
                Instant.now(), Instant.now(), reason
        );
    }
}
