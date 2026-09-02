package com.sofitech.hoamaimart.loyalty.adapter.out.messaging;

import com.sofitech.hoamaimart.shared.event.PointsEarnedEvent;
import com.sofitech.hoamaimart.shared.event.PointsRedeemedEvent;
import com.sofitech.hoamaimart.shared.event.TierUpgradedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LoyaltyEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(LoyaltyEventPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    public LoyaltyEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishPointsEarned(UUID customerId, String transactionCode, int earnedPoints, int newBalance, String tier) {
        PointsEarnedEvent event = new PointsEarnedEvent(
                customerId, transactionCode, earnedPoints, newBalance, tier
        );
        rabbitTemplate.convertAndSend("hoamai.exchange", "loyalty.points.earned", event);
        log.info("Published points.earned event for customer {}", customerId);
    }

    public void publishTierUpgraded(UUID customerId, String oldTier, String newTier) {
        TierUpgradedEvent event = new TierUpgradedEvent(customerId, oldTier, newTier);
        rabbitTemplate.convertAndSend("hoamai.exchange", "loyalty.tier.upgraded", event);
        log.info("Published tier.upgraded event for customer {}: {} -> {}", customerId, oldTier, newTier);
    }

    public void publishPointsRedeemed(UUID customerId, String redemptionId, int redeemedPoints, String reward, int remainingBalance) {
        PointsRedeemedEvent event = new PointsRedeemedEvent(
                customerId, redemptionId, redeemedPoints, reward, remainingBalance
        );
        rabbitTemplate.convertAndSend("hoamai.exchange", "loyalty.points.redeemed", event);
        log.info("Published points.redeemed event for customer {}", customerId);
    }
}
