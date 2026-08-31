package com.sofitech.hoamaimart.customer.adapter.out.persistence.repository;

import com.sofitech.hoamaimart.customer.adapter.out.persistence.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository - adapter OUT.
 */
@Repository
public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, UUID> {

    Optional<CustomerEntity> findByPhone(String phone);

    boolean existsByPhone(String phone);
}