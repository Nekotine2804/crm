package com.sofitech.hoamaimart.loyalty.adapter.in.web.dto;

import com.sofitech.hoamaimart.loyalty.domain.model.PointTransaction;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Response DTO for earning points.
 */
public record EarnPointsResponse(
        UUID customerId,
        int earnedPoints,
        int currentBalance,
        String status,
        String message
) {
    public static EarnPointsResponse from(PointTransaction transaction, UUID customerId) {
        return new EarnPointsResponse(
                customerId,
                transaction.getPoints(),
                transaction.getBalanceAfter(),
                "SUCCESS",
                "Tích điểm thành công"
        );
    }
}
