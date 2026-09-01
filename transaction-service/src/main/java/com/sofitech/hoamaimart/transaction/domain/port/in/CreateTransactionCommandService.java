package com.sofitech.hoamaimart.transaction.domain.port.in;

import com.sofitech.hoamaimart.transaction.domain.model.Transaction;

import java.util.UUID;

/**
 * Port IN: command service interface - use case cho transaction.
 */
public interface CreateTransactionCommandService {

    /**
     * Tạo transaction mới (idempotent).
     * @throws TransactionAlreadyExistsException nếu transactionCode đã tồn tại
     */
    Transaction createTransaction(UUID customerId, String storeId, String transactionCode, java.math.BigDecimal amount);
}
