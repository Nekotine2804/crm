package com.sofitech.hoamaimart.shared.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Event published when a customer tier is upgraded.
 */
public class TierUpgradedEvent implements DomainEvent {

    private final UUID eventId;
    private final Instant occurredAt;
    private final String eventType;

    private final UUID customerId;
    private final String oldTier;
    private final String newTier;

    public TierUpgradedEvent(UUID customerId, String oldTier, String newTier) {
        this.eventId = UUID.randomUUID();
        this.occurredAt = Instant.now();
        this.eventType = "loyalty.tier.upgraded";
        this.customerId = customerId;
        this.oldTier = oldTier;
        this.newTier = newTier;
    }

    @Override
    public UUID eventId() { return eventId; }

    @Override
    public Instant occurredAt() { return occurredAt; }

    @Override
    public String eventType() { return eventType; }

    public UUID getCustomerId() { return customerId; }
    public String getOldTier() { return oldTier; }
    public String getNewTier() { return newTier; }
}
