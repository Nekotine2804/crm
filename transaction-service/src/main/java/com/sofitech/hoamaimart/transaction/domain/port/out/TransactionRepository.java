package com.sofitech.hoamaimart.transaction.domain.port.out;

import com.sofitech.hoamaimart.transaction.domain.model.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port OUT: repository interface.
 */
public interface TransactionRepository {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(UUID id);

    List<Transaction> findByCustomerId(UUID customerId);
}