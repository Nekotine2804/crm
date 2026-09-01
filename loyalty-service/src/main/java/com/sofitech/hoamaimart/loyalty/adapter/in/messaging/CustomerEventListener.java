package com.sofitech.hoamaimart.loyalty.adapter.in.messaging;

import com.sofitech.hoamaimart.loyalty.application.service.LoyaltyService;
import com.sofitech.hoamaimart.shared.event.CustomerCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CustomerEventListener {

    private static final Logger log = LoggerFactory.getLogger(CustomerEventListener.class);
    private final LoyaltyService loyaltyService;

    public CustomerEventListener(LoyaltyService loyaltyService) {
        this.loyaltyService = loyaltyService;
    }

    @RabbitListener(queues = "loyalty.customer.queue")
    public void handleCustomerCreated(CustomerCreatedEvent event) {
        loyaltyService.ensureAccount(event.getCustomerId());
        log.info("Ensured loyalty account for customer: {}", event.getCustomerId());
    }
}
