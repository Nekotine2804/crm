package com.sofitech.hoamaimart.loyalty.adapter.out.persistence.entity;

import com.sofitech.hoamaimart.loyalty.domain.model.LoyaltyAccount;
import com.sofitech.hoamaimart.loyalty.domain.model.Points;
import com.sofitech.hoamaimart.loyalty.domain.model.Tier;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity cho bảng loyalty_accounts.
 */
@Entity
@Table(name = "loyalty_accounts")
public class LoyaltyAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "customer_id", nullable = false, unique = true)
    private UUID customerId;

    @Column(name = "points", nullable = false)
    private int points;

    @Enumerated(EnumType.STRING)
    @Column(name = "tier", nullable = false, length = 20)
    private Tier tier;

    @Enumerated(EnumType.STRING)
    @Column(name = "pending_tier", length = 20)
    private Tier pendingTier;

    @Column(name = "last_tier_evaluation")
    private Instant lastTierEvaluation;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected LoyaltyAccountEntity() {}

    public LoyaltyAccount toDomain() {
        return new LoyaltyAccount(
                this.id,
                this.customerId,
                Points.of(this.points),
                this.tier,
                this.pendingTier,
                this.lastTierEvaluation,
                this.createdAt,
                this.updatedAt
        );
    }

    public static LoyaltyAccountEntity fromDomain(LoyaltyAccount account) {
        LoyaltyAccountEntity entity = new LoyaltyAccountEntity();
        entity.id = account.getId();
        entity.customerId = account.getCustomerId();
        entity.points = account.getPoints().value();
        entity.tier = account.getTier();
        entity.pendingTier = account.getPendingTier();
        entity.lastTierEvaluation = account.getLastTierEvaluation();
        entity.createdAt = account.getCreatedAt();
        entity.updatedAt = account.getUpdatedAt();
        return entity;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getCustomerId() { return customerId; }
    public int getPoints() { return points; }
    public Tier getTier() { return tier; }
    public Tier getPendingTier() { return pendingTier; }
    public Instant getLastTierEvaluation() { return lastTierEvaluation; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}