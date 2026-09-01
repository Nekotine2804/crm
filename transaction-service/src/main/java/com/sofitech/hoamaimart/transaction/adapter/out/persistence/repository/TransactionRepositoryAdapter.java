package com.sofitech.hoamaimart.transaction.adapter.out.persistence.repository;

import com.sofitech.hoamaimart.transaction.adapter.out.persistence.entity.TransactionEntity;
import com.sofitech.hoamaimart.transaction.domain.model.Transaction;
import com.sofitech.hoamaimart.transaction.domain.port.out.TransactionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Adapter OUT: implement TransactionRepository dùng JPA.
 */
@Repository
public class TransactionRepositoryAdapter implements TransactionRepository {

    private final TransactionJpaRepository jpaRepository;

    public TransactionRepositoryAdapter(TransactionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity entity = TransactionEntity.fromDomain(transaction);
        return jpaRepository.save(entity).toDomain();
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        return jpaRepository.findById(id).map(TransactionEntity::toDomain);
    }

    @Override
    public Optional<Transaction> findByTransactionCode(String transactionCode) {
        return jpaRepository.findByTransactionCode(transactionCode).map(TransactionEntity::toDomain);
    }

    @Override
    public boolean existsByTransactionCode(String transactionCode) {
        return jpaRepository.existsByTransactionCode(transactionCode);
    }
}
