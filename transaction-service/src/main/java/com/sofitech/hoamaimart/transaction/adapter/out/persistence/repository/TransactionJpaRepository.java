package com.sofitech.hoamaimart.transaction.adapter.out.persistence.repository;

import com.sofitech.hoamaimart.transaction.adapter.out.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * JPA Repository.
 */
@Repository
public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, UUID> {

    List<TransactionEntity> findByCustomerId(UUID customerId);
}