package com.sofitech.hoamaimart.transaction.domain.exception;

import java.util.UUID;

/**
 * Exception khi không tìm thấy transaction.
 */
public class TransactionNotFoundException extends RuntimeException {

    private final UUID transactionId;

    public TransactionNotFoundException(UUID transactionId) {
        super("Không tìm thấy transaction: " + transactionId);
        this.transactionId = transactionId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }
}
