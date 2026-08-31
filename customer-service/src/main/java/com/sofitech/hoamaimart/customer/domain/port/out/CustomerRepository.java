package com.sofitech.hoamaimart.customer.domain.port.out;

import com.sofitech.hoamaimart.customer.domain.model.Customer;

import java.util.Optional;
import java.util.UUID;

/**
 * Port OUT: repository interface - được implement bởi adapter persistence.
 * Domain không biết gì về JPA/Postgres.
 */
public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findById(UUID id);

    Optional<Customer> findByPhone(String phone);

    boolean existsByPhone(String phone);
}