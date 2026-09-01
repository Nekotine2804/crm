package com.sofitech.hoamaimart.shared.event;

import java.time.Instant;
import java.util.UUID;

public record PointsRedeemedEvent(
        UUID eventId, Instant occurredAt, String eventType,
        UUID customerId, String redemptionId, int redeemedPoints,
        String reward, int remainingBalance
) implements DomainEvent {

    public PointsRedeemedEvent(UUID customerId, String redemptionId,
                               int redeemedPoints, String reward, int remainingBalance) {
        this(UUID.randomUUID(), Instant.now(), "loyalty.points.redeemed",
                customerId, redemptionId, redeemedPoints, reward, remainingBalance);
    }

    public UUID getCustomerId() { return customerId; }
    public String getRedemptionId() { return redemptionId; }
    public int getRedeemedPoints() { return redeemedPoints; }
    public String getReward() { return reward; }
    public int getRemainingBalance() { return remainingBalance; }
}
