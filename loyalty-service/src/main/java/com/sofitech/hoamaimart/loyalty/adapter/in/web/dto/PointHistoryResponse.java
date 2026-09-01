package com.sofitech.hoamaimart.loyalty.adapter.in.web.dto;

import com.sofitech.hoamaimart.loyalty.domain.model.PointTransaction;
import com.sofitech.hoamaimart.loyalty.domain.model.PointTransactionType;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for point transaction history.
 */
public record PointHistoryResponse(
        UUID id,
        PointTransactionType type,
        int points,
        int balanceAfter,
        String referenceId,
        String description,
        Instant createdAt
) {
    public static PointHistoryResponse from(PointTransaction transaction) {
        return new PointHistoryResponse(
                transaction.getId(),
                transaction.getType(),
                transaction.getPoints(),
                transaction.getBalanceAfter(),
                transaction.getReferenceId(),
                transaction.getDescription(),
                transaction.getCreatedAt()
        );
    }
}
