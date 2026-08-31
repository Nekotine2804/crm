package com.sofitech.hoamaimart.loyalty.adapter.out.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Entity ghi nhận chi tiêu để tính rolling window.
 */
@Entity
@Table(name = "spending_history")
public class SpendingHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "transaction_id", nullable = false)
    private UUID transactionId;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "spent_at", nullable = false)
    private Instant spentAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected SpendingHistoryEntity() {}

    public SpendingHistoryEntity(UUID id, UUID customerId, UUID transactionId,
                                 BigDecimal amount, Instant spentAt, Instant createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.transactionId = transactionId;
        this.amount = amount;
        this.spentAt = spentAt;
        this.createdAt = createdAt;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getCustomerId() { return customerId; }
    public UUID getTransactionId() { return transactionId; }
    public BigDecimal getAmount() { return amount; }
    public Instant getSpentAt() { return spentAt; }
    public Instant getCreatedAt() { return createdAt; }
}