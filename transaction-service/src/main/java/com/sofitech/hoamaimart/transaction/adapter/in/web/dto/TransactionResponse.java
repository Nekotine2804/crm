package com.sofitech.hoamaimart.transaction.adapter.in.web.dto;

import com.sofitech.hoamaimart.transaction.domain.model.Transaction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO sau khi tạo giao dịch.
 */
public record TransactionResponse(
        UUID transactionId,
        UUID customerId,
        String storeId,
        String transactionCode,
        BigDecimal amount,
        String status,
        Instant createdAt
) {

    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getCustomerId(),
                transaction.getStoreId(),
                transaction.getTransactionCode(),
                transaction.getAmountValue(),
                transaction.getStatus().name(),
                transaction.getCreatedAt()
        );
    }
}