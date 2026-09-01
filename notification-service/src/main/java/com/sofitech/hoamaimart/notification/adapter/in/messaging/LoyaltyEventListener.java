package com.sofitech.hoamaimart.notification.adapter.in.messaging;

import com.sofitech.hoamaimart.notification.application.service.NotificationService;
import com.sofitech.hoamaimart.shared.event.PointsEarnedEvent;
import com.sofitech.hoamaimart.shared.event.PointsRedeemedEvent;
import com.sofitech.hoamaimart.shared.event.TierUpgradedEvent;
import com.sofitech.hoamaimart.shared.event.TransactionRefundedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Adapter IN: lắng nghe events từ RabbitMQ.
 */
@Component
public class LoyaltyEventListener {

    private static final Logger log = LoggerFactory.getLogger(LoyaltyEventListener.class);

    private final NotificationService notificationService;

    public LoyaltyEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = "notification.points.earned.queue")
    public void handlePointsEarned(PointsEarnedEvent event) {
        log.info("Received points.earned event for customer {}: {} points",
                event.getCustomerId(), event.getEarnedPoints());

        notificationService.sendPointsEarnedNotification(
                event.getCustomerId(),
                event.getEarnedPoints(),
                event.getNewBalance()
        );
    }

    @RabbitListener(queues = "notification.tier.upgraded.queue")
    public void handleTierUpgraded(TierUpgradedEvent event) {
        log.info("Received tier.upgraded event for customer {}: {} -> {}",
                event.getCustomerId(), event.getOldTier(), event.getNewTier());

        notificationService.sendTierUpgradeNotification(
                event.getCustomerId(),
                event.getNewTier()
        );
    }

    @RabbitListener(queues = "notification.points.redeemed.queue")
    public void handlePointsRedeemed(PointsRedeemedEvent event) {
        log.info("Received points.redeemed event for customer {}: {} points for {}",
                event.getCustomerId(), event.getRedeemedPoints(), event.getReward());

        notificationService.sendRedeemSuccessNotification(
                event.getCustomerId(),
                event.getRedeemedPoints(),
                event.getReward(),
                event.getRemainingBalance()
        );
    }

    @RabbitListener(queues = "notification.refund.queue")
    public void handleRefund(TransactionRefundedEvent event) {
        log.info("Received refund event for customer {}: transaction {}",
                event.getCustomerId(), event.getTransactionCode());

        notificationService.sendRefundNotification(
                event.getCustomerId(),
                event.getTransactionCode(),
                event.getRefundReason()
        );
    }
}
