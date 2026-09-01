package com.sofitech.hoamaimart.loyalty.adapter.out.persistence.entity;

import com.sofitech.hoamaimart.loyalty.domain.model.PointTransaction;
import com.sofitech.hoamaimart.loyalty.domain.model.PointTransactionType;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity cho bảng point_transactions.
 */
@Entity
@Table(name = "point_transactions", indexes = {
        @Index(name = "idx_point_tx_customer_id", columnList = "customer_id"),
        @Index(name = "idx_point_tx_loyalty_account_id", columnList = "loyalty_account_id"),
        @Index(name = "idx_point_tx_type", columnList = "type"),
        @Index(name = "idx_point_tx_created_at", columnList = "created_at")
})
public class PointTransactionEntity {

    @Id
    private UUID id;

    @Column(name = "loyalty_account_id", nullable = false)
    private UUID loyaltyAccountId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private PointTransactionType type;

    @Column(name = "points", nullable = false)
    private int points;

    @Column(name = "balance_after", nullable = false)
    private int balanceAfter;

    @Column(name = "reference_id")
    private String referenceId;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected PointTransactionEntity() {}

    public PointTransaction toDomain() {
        return new PointTransaction(
                this.id,
                this.loyaltyAccountId,
                this.customerId,
                this.type,
                this.points,
                this.balanceAfter,
                this.referenceId,
                this.description,
                this.createdAt
        );
    }

    public static PointTransactionEntity fromDomain(PointTransaction transaction) {
        PointTransactionEntity entity = new PointTransactionEntity();
        entity.id = transaction.getId();
        entity.loyaltyAccountId = transaction.getLoyaltyAccountId();
        entity.customerId = transaction.getCustomerId();
        entity.type = transaction.getType();
        entity.points = transaction.getPoints();
        entity.balanceAfter = transaction.getBalanceAfter();
        entity.referenceId = transaction.getReferenceId();
        entity.description = transaction.getDescription();
        entity.createdAt = transaction.getCreatedAt();
        return entity;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getLoyaltyAccountId() { return loyaltyAccountId; }
    public UUID getCustomerId() { return customerId; }
    public PointTransactionType getType() { return type; }
    public int getPoints() { return points; }
    public int getBalanceAfter() { return balanceAfter; }
    public String getReferenceId() { return referenceId; }
    public String getDescription() { return description; }
    public Instant getCreatedAt() { return createdAt; }
}
