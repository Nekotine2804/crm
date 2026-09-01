package com.sofitech.hoamaimart.shared.event;

import java.time.Instant;
import java.util.UUID;


public interface DomainEvent {

    UUID eventId();

    Instant occurredAt();

    /** vd. "transaction.completed", "loyalty.points.earned" — cũng là routing key gợi ý */
    String eventType();
}