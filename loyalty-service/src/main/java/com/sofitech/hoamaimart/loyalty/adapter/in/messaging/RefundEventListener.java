package com.sofitech.hoamaimart.loyalty.adapter.in.messaging;

import com.sofitech.hoamaimart.loyalty.application.service.LoyaltyService;
import com.sofitech.hoamaimart.shared.event.TransactionRefundedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Adapter IN: lắng nghe event refund từ RabbitMQ.
 * Tự động hoàn điểm loyalty khi có transaction được refund.
 */
@Component
public class RefundEventListener {

    private static final Logger log = LoggerFactory.getLogger(RefundEventListener.class);

    private final LoyaltyService loyaltyService;

    public RefundEventListener(LoyaltyService loyaltyService) {
        this.loyaltyService = loyaltyService;
    }

    /**
     * Lắng nghe event transaction.refunded từ RabbitMQ.
     * Hoàn điểm loyalty cho khách hàng.
     */
    @RabbitListener(queues = "loyalty.refund.queue")
    public void handleTransactionRefunded(TransactionRefundedEvent event) {
        log.info("Received transaction.refunded event: transactionId={}, transactionCode={}, customerId={}, amount={}",
                event.getTransactionId(), event.getTransactionCode(), event.getCustomerId(), event.getOriginalAmount());

        try {
            loyaltyService.refundPoints(
                    event.getCustomerId(),
                    event.getTransactionCode(),
                    event.getOriginalAmount()
            );
            log.info("Refunded loyalty points for customer: {}. Transaction: {}",
                    event.getCustomerId(), event.getTransactionCode());
        } catch (Exception e) {
            log.error("Failed to process refund event for customer {}: {}",
                    event.getCustomerId(), e.getMessage(), e);
            throw e; // Re-throw để RabbitMQ retry
        }
    }
}
