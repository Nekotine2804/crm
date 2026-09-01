package com.sofitech.hoamaimart.customer.domain.port.out;

import com.sofitech.hoamaimart.customer.domain.model.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

    Customer create(Customer customer);

    Customer update(Customer customer);

    Optional<Customer> findById(UUID id);

    Optional<Customer> findByPhone(String phone);

    boolean existsByPhone(String phone);
}