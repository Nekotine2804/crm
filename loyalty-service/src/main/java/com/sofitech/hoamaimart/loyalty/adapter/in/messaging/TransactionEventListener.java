package com.sofitech.hoamaimart.loyalty.adapter.in.messaging;

import com.sofitech.hoamaimart.loyalty.domain.port.in.LoyaltyCommandService;
import com.sofitech.hoamaimart.shared.event.TransactionCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Adapter IN: lắng nghe event từ RabbitMQ.
 */
@Component
public class TransactionEventListener {

    private static final Logger log = LoggerFactory.getLogger(TransactionEventListener.class);

    private final LoyaltyCommandService loyaltyCommandService;

    public TransactionEventListener(LoyaltyCommandService loyaltyCommandService) {
        this.loyaltyCommandService = loyaltyCommandService;
    }

    /**
     * Lắng nghe event transaction.completed từ RabbitMQ.
     * Tự động cộng điểm loyalty cho khách hàng.
     */
    @RabbitListener(queues = "loyalty.transaction.queue")
    public void handleTransactionCompleted(TransactionCompletedEvent event) {
        log.info("Received transaction.completed event: transactionId={}, customerId={}, amount={}",
                event.getTransactionId(), event.getCustomerId(), event.getAmount());

        try {
            loyaltyCommandService.addPoints(event.getCustomerId(), event.getAmount());
            log.info("Added loyalty points for customer: {}", event.getCustomerId());
        } catch (Exception e) {
            log.error("Failed to process transaction event for customer {}: {}",
                    event.getCustomerId(), e.getMessage(), e);
            throw e; // Re-throw để RabbitMQ retry
        }
    }
}