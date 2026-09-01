package com.sofitech.hoamaimart.shared.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Event published when points are redeemed.
 */
public class PointsRedeemedEvent implements DomainEvent {

    private final UUID eventId;
    private final Instant occurredAt;
    private final String eventType;

    private final UUID customerId;
    private final String redemptionId;
    private final int redeemedPoints;
    private final String reward;
    private final int remainingBalance;

    public PointsRedeemedEvent(
            UUID customerId,
            String redemptionId,
            int redeemedPoints,
            String reward,
            int remainingBalance
    ) {
        this.eventId = UUID.randomUUID();
        this.occurredAt = Instant.now();
        this.eventType = "loyalty.points.redeemed";
        this.customerId = customerId;
        this.redemptionId = redemptionId;
        this.redeemedPoints = redeemedPoints;
        this.reward = reward;
        this.remainingBalance = remainingBalance;
    }

    @Override
    public UUID eventId() { return eventId; }

    @Override
    public Instant occurredAt() { return occurredAt; }

    @Override
    public String eventType() { return eventType; }

    public UUID getCustomerId() { return customerId; }
    public String getRedemptionId() { return redemptionId; }
    public int getRedeemedPoints() { return redeemedPoints; }
    public String getReward() { return reward; }
    public int getRemainingBalance() { return remainingBalance; }
}
