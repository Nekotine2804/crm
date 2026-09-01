package com.sofitech.hoamaimart.transaction.domain.port.out;

import com.sofitech.hoamaimart.transaction.domain.model.Transaction;

import java.util.Optional;
import java.util.UUID;

/**
 * Port OUT: repository interface - được implement bởi adapter persistence.
 */
public interface TransactionRepository {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(UUID id);

    Optional<Transaction> findByTransactionCode(String transactionCode);

    boolean existsByTransactionCode(String transactionCode);
}
