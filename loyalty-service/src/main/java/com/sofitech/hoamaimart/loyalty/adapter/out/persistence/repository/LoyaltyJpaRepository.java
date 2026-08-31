package com.sofitech.hoamaimart.loyalty.adapter.out.persistence.repository;

import com.sofitech.hoamaimart.loyalty.adapter.out.persistence.entity.LoyaltyAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository.
 */
@Repository
public interface LoyaltyJpaRepository extends JpaRepository<LoyaltyAccountEntity, UUID> {

    Optional<LoyaltyAccountEntity> findByCustomerId(UUID customerId);
}