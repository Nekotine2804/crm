package com.sofitech.hoamaimart.transaction.domain.port.in;

import com.sofitech.hoamaimart.transaction.domain.model.Transaction;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Port IN: command service interface.
 */
public interface CreateTransactionCommandService {

    /**
     * Tạo giao dịch mới và publish event.
     */
    Transaction createTransaction(UUID customerId, String storeId, BigDecimal amount);
}