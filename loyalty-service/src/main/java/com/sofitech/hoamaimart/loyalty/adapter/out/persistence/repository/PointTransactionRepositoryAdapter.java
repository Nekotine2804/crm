package com.sofitech.hoamaimart.loyalty.adapter.out.persistence.repository;

import com.sofitech.hoamaimart.loyalty.adapter.out.persistence.entity.PointTransactionEntity;
import com.sofitech.hoamaimart.loyalty.domain.model.PointTransaction;
import com.sofitech.hoamaimart.loyalty.domain.model.PointTransactionType;
import com.sofitech.hoamaimart.loyalty.domain.port.out.PointTransactionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter OUT: implement PointTransactionRepository dùng JPA.
 */
@Repository
public class PointTransactionRepositoryAdapter implements PointTransactionRepository {

    private final PointTransactionJpaRepository jpaRepository;

    public PointTransactionRepositoryAdapter(PointTransactionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PointTransaction save(PointTransaction transaction) {
        PointTransactionEntity entity = PointTransactionEntity.fromDomain(transaction);
        return jpaRepository.save(entity).toDomain();
    }

    @Override
    public Optional<PointTransaction> findById(UUID id) {
        return jpaRepository.findById(id).map(PointTransactionEntity::toDomain);
    }

    @Override
    public List<PointTransaction> findByCustomerIdOrderByCreatedAtDesc(UUID customerId) {
        return jpaRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(PointTransactionEntity::toDomain)
                .toList();
    }

    @Override
    public List<PointTransaction> findByCustomerIdAndType(UUID customerId, PointTransactionType type) {
        return jpaRepository.findByCustomerIdAndTypeOrderByCreatedAtDesc(customerId, type)
                .stream()
                .map(PointTransactionEntity::toDomain)
                .toList();
    }

    @Override
    public boolean existsByReferenceIdAndType(String referenceId, PointTransactionType type) {
        return jpaRepository.existsByReferenceIdAndType(referenceId, type);
    }

    @Override
    public List<PointTransaction> findByReferenceId(String referenceId) {
        return jpaRepository.findByReferenceId(referenceId)
                .stream()
                .map(PointTransactionEntity::toDomain)
                .toList();
    }
}
