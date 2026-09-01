package com.sofitech.hoamaimart.transaction.domain.port.in;

import com.sofitech.hoamaimart.transaction.domain.model.Transaction;

import java.util.UUID;

/**
 * Port IN: command service interface - use case refund transaction.
 */
public interface RefundTransactionService {

    /**
     * Refund một transaction.
     * @param transactionId ID của transaction cần refund
     * @param reason Lý do refund
     * @return Transaction đã được refund
     * @throws TransactionNotFoundException nếu không tìm thấy
     * @throws TransactionAlreadyRefundedException nếu đã refund rồi
     */
    Transaction refundTransaction(UUID transactionId, String reason);

    /**
     * Cancel một transaction (chỉ transaction PENDING mới cancel được).
     * @param transactionId ID của transaction cần cancel
     * @return Transaction đã được cancel
     */
    Transaction cancelTransaction(UUID transactionId);
}
