package com.sofitech.hoamaimart.transaction.adapter.out.persistence.entity;

import com.sofitech.hoamaimart.transaction.domain.model.Amount;
import com.sofitech.hoamaimart.transaction.domain.model.Transaction;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "store_id", nullable = false, length = 50)
    private String storeId;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "transaction_code", nullable = false, unique = true, length = 100)
    private String transactionCode;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    @Column(name = "refund_reason", length = 255)
    private String refundReason;

    @Version
    @Column(name = "version")
    private Long version;

    protected TransactionEntity() {}

    public TransactionEntity(UUID id, UUID customerId, String storeId, BigDecimal amount,
                             String transactionCode, String status, Instant createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.storeId = storeId;
        this.amount = amount;
        this.transactionCode = transactionCode;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public Transaction toDomain() {
        return new Transaction(
                this.id,
                this.customerId,
                this.storeId,
                Amount.of(this.amount),
                this.transactionCode,
                Transaction.Status.valueOf(this.status),
                this.createdAt
        );
    }

    public static TransactionEntity fromDomain(Transaction transaction) {
        TransactionEntity entity = new TransactionEntity(
                transaction.getId(),
                transaction.getCustomerId(),
                transaction.getStoreId(),
                transaction.getAmountValue(),
                transaction.getTransactionCode(),
                transaction.getStatus().name(),
                transaction.getCreatedAt()
        );
        entity.updatedAt = transaction.getUpdatedAt();
        entity.cancelledAt = transaction.getCancelledAt();
        entity.refundReason = transaction.getRefundReason();
        return entity;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getCustomerId() { return customerId; }
    public String getStoreId() { return storeId; }
    public BigDecimal getAmount() { return amount; }
    public String getTransactionCode() { return transactionCode; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public Instant getCancelledAt() { return cancelledAt; }
    public String getRefundReason() { return refundReason; }
    public Long getVersion() { return version; }
}
