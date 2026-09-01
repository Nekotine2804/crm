package com.sofitech.hoamaimart.shared.event;

import java.time.Instant;
import java.util.UUID;

/** Event published after a customer has been created successfully. */
public record CustomerCreatedEvent(
        UUID eventId,
        Instant occurredAt,
        String eventType,
        UUID customerId
) implements DomainEvent {

    /** Constructor used by the publisher. The canonical constructor is used by Jackson. */
    public CustomerCreatedEvent(UUID customerId) {
        this(UUID.randomUUID(), Instant.now(), "customer.created", customerId);
    }

    public UUID getCustomerId() { return customerId; }
}
