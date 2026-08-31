package com.sofitech.hoamaimart.transaction.adapter.out.messaging;

import com.sofitech.hoamaimart.shared.event.TransactionCompletedEvent;
import com.sofitech.hoamaimart.transaction.domain.model.Transaction;
import com.sofitech.hoamaimart.transaction.domain.port.out.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;

/**
 * Adapter OUT: publish events qua RabbitMQ.
 */
public class RabbitMQEventPublisher implements EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;

    public RabbitMQEventPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${app.rabbitmq.exchange:hoamai.exchange}") String exchange
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
    }

    @Override
    public void publishTransactionCompleted(Transaction transaction) {
        TransactionCompletedEvent event = new TransactionCompletedEvent(
                transaction.getId(),
                transaction.getCustomerId(),
                transaction.getStoreId(),
                transaction.getAmountValue()
        );

        // Routing key = event type
        rabbitTemplate.convertAndSend(exchange, event.eventType(), event);

        log.info("Published transaction.completed event: transactionId={}, customerId={}, amount={}",
                transaction.getId(), transaction.getCustomerId(), transaction.getAmountValue());
    }
}