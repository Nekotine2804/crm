package com.sofitech.hoamaimart.shared.event;

import java.time.Instant;
import java.util.UUID;

public record PointsEarnedEvent(
        UUID eventId, Instant occurredAt, String eventType,
        UUID customerId, String transactionCode, int earnedPoints,
        int newBalance, String tier
) implements DomainEvent {

    public PointsEarnedEvent(UUID customerId, String transactionCode,
                             int earnedPoints, int newBalance, String tier) {
        this(UUID.randomUUID(), Instant.now(), "loyalty.points.earned",
                customerId, transactionCode, earnedPoints, newBalance, tier);
    }

    public UUID getCustomerId() { return customerId; }
    public String getTransactionCode() { return transactionCode; }
    public int getEarnedPoints() { return earnedPoints; }
    public int getNewBalance() { return newBalance; }
    public String getTier() { return tier; }
}
