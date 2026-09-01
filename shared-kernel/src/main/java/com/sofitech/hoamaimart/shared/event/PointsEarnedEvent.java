package com.sofitech.hoamaimart.shared.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Event published when points are earned.
 */
public class PointsEarnedEvent implements DomainEvent {

    private final UUID eventId;
    private final Instant occurredAt;
    private final String eventType;

    private final UUID customerId;
    private final String transactionCode;
    private final int earnedPoints;
    private final int newBalance;
    private final String tier;

    public PointsEarnedEvent(
            UUID customerId,
            String transactionCode,
            int earnedPoints,
            int newBalance,
            String tier
    ) {
        this.eventId = UUID.randomUUID();
        this.occurredAt = Instant.now();
        this.eventType = "loyalty.points.earned";
        this.customerId = customerId;
        this.transactionCode = transactionCode;
        this.earnedPoints = earnedPoints;
        this.newBalance = newBalance;
        this.tier = tier;
    }

    @Override
    public UUID eventId() { return eventId; }

    @Override
    public Instant occurredAt() { return occurredAt; }

    @Override
    public String eventType() { return eventType; }

    public UUID getCustomerId() { return customerId; }
    public String getTransactionCode() { return transactionCode; }
    public int getEarnedPoints() { return earnedPoints; }
    public int getNewBalance() { return newBalance; }
    public String getTier() { return tier; }
}
