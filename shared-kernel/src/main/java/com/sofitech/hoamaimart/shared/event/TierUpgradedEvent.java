package com.sofitech.hoamaimart.shared.event;

import java.time.Instant;
import java.util.UUID;

public record TierUpgradedEvent(
        UUID eventId, Instant occurredAt, String eventType,
        UUID customerId, String oldTier, String newTier
) implements DomainEvent {

    public TierUpgradedEvent(UUID customerId, String oldTier, String newTier) {
        this(UUID.randomUUID(), Instant.now(), "loyalty.tier.upgraded",
                customerId, oldTier, newTier);
    }

    public UUID getCustomerId() { return customerId; }
    public String getOldTier() { return oldTier; }
    public String getNewTier() { return newTier; }
}
