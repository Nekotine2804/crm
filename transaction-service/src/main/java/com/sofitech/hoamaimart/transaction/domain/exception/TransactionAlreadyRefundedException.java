package com.sofitech.hoamaimart.transaction.domain.exception;

import java.util.UUID;

/**
 * Exception khi transaction đã được refund.
 */
public class TransactionAlreadyRefundedException extends RuntimeException {

    private final UUID transactionId;

    public TransactionAlreadyRefundedException(UUID transactionId) {
        super("Transaction đã được refund: " + transactionId);
        this.transactionId = transactionId;
    }

    public UUID getTransactionId() {
        return transactionId;
    }
}
