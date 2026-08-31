package com.sofitech.hoamaimart.shared.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Contract dùng chung cho MỌI event trao đổi qua RabbitMQ giữa các service.
 *
 * shared-kernel là 1 Maven module RIÊNG, build thành .jar, các service
 * khai báo như 1 dependency (KHÔNG copy code, KHÔNG chứa business logic
 * của bất kỳ service nào — chỉ contract/event schema + exception + util
 * thuần Java, không phụ thuộc Spring).
 *
 * Khi đổi version của event (thêm field...), phải giữ backward-compatible
 * hoặc version hoá routing key (vd. "transaction.completed.v2") vì các service
 * deploy độc lập, không cùng lúc.
 */
public interface rèDomainEvent {

    UUID eventId();

    Instant occurredAt();

    /** vd. "transaction.completed", "loyalty.points.earned" — cũng là routing key gợi ý */
    String eventType();
}