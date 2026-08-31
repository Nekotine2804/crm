package com.sofitech.hoamaimart.customer.domain.port.in;

import com.sofitech.hoamaimart.customer.domain.model.Customer;

import java.util.Optional;
import java.util.UUID;

/**
 * Port IN: query service interface - truy vấn customer.
 */
public interface CustomerQueryService {

    Optional<Customer> findById(UUID id);

    Optional<Customer> findByPhone(String phone);
}