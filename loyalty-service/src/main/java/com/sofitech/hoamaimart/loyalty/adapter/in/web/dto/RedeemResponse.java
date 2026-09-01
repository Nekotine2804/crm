package com.sofitech.hoamaimart.loyalty.adapter.in.web.dto;

import com.sofitech.hoamaimart.loyalty.domain.model.LoyaltyAccount;

import java.util.UUID;

/**
 * Response DTO for redeeming points.
 */
public record RedeemResponse(
        UUID customerId,
        int redeemedPoints,
        int currentBalance,
        String tier,
        String redemptionId,
        String status
) {
    public static RedeemResponse from(LoyaltyAccount account, String redemptionId) {
        return new RedeemResponse(
                account.getCustomerId(),
                0, // redeemed points - could be tracked separately
                account.getPoints().value(),
                account.getTier().name(),
                redemptionId,
                "SUCCESS"
        );
    }
}
