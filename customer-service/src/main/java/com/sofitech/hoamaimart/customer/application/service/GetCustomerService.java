package com.sofitech.hoamaimart.customer.application.service;

import com.sofitech.hoamaimart.customer.domain.model.Customer;
import com.sofitech.hoamaimart.customer.domain.port.in.CustomerQueryService;
import com.sofitech.hoamaimart.customer.domain.port.out.CustomerRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Application service: truy vấn customer.
 */
public class GetCustomerService implements CustomerQueryService {

    private final CustomerRepository customerRepository;

    public GetCustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return customerRepository.findById(id);
    }

    @Override
    public Optional<Customer> findByPhone(String phone) {
        return customerRepository.findByPhone(phone);
    }
}