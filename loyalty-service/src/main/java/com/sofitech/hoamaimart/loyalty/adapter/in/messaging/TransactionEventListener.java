package com.sofitech.hoamaimart.loyalty.adapter.in.messaging;

import com.sofitech.hoamaimart.loyalty.application.service.LoyaltyService;
import com.sofitech.hoamaimart.shared.event.TransactionCompletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Adapter IN: lắng nghe event từ RabbitMQ.
 * Tự động tích điểm loyalty khi có transaction hoàn thành.
 */
@Component
public class TransactionEventListener {

    private static final Logger log = LoggerFactory.getLogger(TransactionEventListener.class);

    private final LoyaltyService loyaltyService;

    public TransactionEventListener(LoyaltyService loyaltyService) {
        this.loyaltyService = loyaltyService;
    }

    /**
     * Lắng nghe event transaction.completed từ RabbitMQ.
     * Tự động cộng điểm loyalty cho khách hàng.
     * Idempotent: nếu transaction đã được tích điểm rồi thì bỏ qua.
     */
    @RabbitListener(queues = "loyalty.transaction.queue")
    public void handleTransactionCompleted(TransactionCompletedEvent event) {
        log.info("Received transaction.completed event: transactionId={}, transactionCode={}, customerId={}, amount={}",
                event.getTransactionId(), event.getTransactionCode(), event.getCustomerId(), event.getAmount());

        try {
            // Sử dụng earnPoints với idempotency check
            var pointTransaction = loyaltyService.earnPoints(
                    event.getCustomerId(),
                    event.getTransactionCode(),  // Dùng transactionCode làm reference
                    event.getAmount()
            );

            if (pointTransaction != null) {
                log.info("Earned {} points for customer {}. New balance: {}",
                        pointTransaction.getPoints(),
                        event.getCustomerId(),
                        pointTransaction.getBalanceAfter());
            } else {
                log.info("Transaction {} already processed (idempotent). Skipping...",
                        event.getTransactionCode());
            }
        } catch (Exception e) {
            log.error("Failed to process transaction event for customer {}: {}",
                    event.getCustomerId(), e.getMessage(), e);
            throw e; // Re-throw để RabbitMQ retry
        }
    }
}
