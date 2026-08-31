package com.sofitech.hoamaimart.loyalty.adapter.in.web.dto;

import com.sofitech.hoamaimart.loyalty.domain.model.LoyaltyAccount;
import com.sofitech.hoamaimart.loyalty.domain.model.Tier;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO cho thông tin loyalty account.
 */
public record LoyaltyResponse(
        UUID customerId,
        int points,
        Tier tier,
        Instant updatedAt
) {
    public static LoyaltyResponse from(LoyaltyAccount account) {
        return new LoyaltyResponse(
                account.getCustomerId(),
                account.getPoints().value(),
                account.getTier(),
                account.getUpdatedAt()
        );
    }
}