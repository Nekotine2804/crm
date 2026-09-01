package com.sofitech.hoamaimart.customer.adapter.out.messaging;

import com.sofitech.hoamaimart.customer.domain.model.Customer;
import com.sofitech.hoamaimart.customer.domain.port.out.EventPublisher;
import com.sofitech.hoamaimart.shared.event.CustomerCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
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
    public void publishCustomerCreated(Customer customer) {
        CustomerCreatedEvent event = new CustomerCreatedEvent(customer.getId());
        rabbitTemplate.convertAndSend(exchange, event.eventType(), event);
        log.info("Published customer.created event: customerId={}", customer.getId());
    }
}
