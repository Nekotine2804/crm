package com.sofitech.hoamaimart.transaction.adapter.out.persistence.repository;

import com.sofitech.hoamaimart.transaction.adapter.out.persistence.mapper.TransactionMapper;
import com.sofitech.hoamaimart.transaction.domain.model.Transaction;
import com.sofitech.hoamaimart.transaction.domain.port.out.TransactionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter OUT: implement TransactionRepository dùng JPA.
 */
@Component
public class TransactionRepositoryAdapter implements TransactionRepository {

    private final TransactionJpaRepository jpaRepository;
    private final TransactionMapper mapper;

    public TransactionRepositoryAdapter(TransactionJpaRepository jpaRepository, TransactionMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Transaction save(Transaction transaction) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(transaction)));
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Transaction> findByCustomerId(UUID customerId) {
        return jpaRepository.findByCustomerId(customerId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}