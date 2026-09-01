package com.sofitech.hoamaimart.customer.application.service;

import com.sofitech.hoamaimart.customer.domain.exception.CustomerNotFoundException;
import com.sofitech.hoamaimart.customer.domain.exception.PhoneAlreadyExistsException;
import com.sofitech.hoamaimart.customer.domain.model.Customer;
import com.sofitech.hoamaimart.customer.domain.port.in.CustomerCommandService;
import com.sofitech.hoamaimart.customer.domain.port.out.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CreateCustomerService implements CustomerCommandService {

    private final CustomerRepository customerRepository;

    public CreateCustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public Customer createCustomer(String phone, String name) {

        if (customerRepository.existsByPhone(phone)) {
            throw new PhoneAlreadyExistsException(phone);
        }

        Customer customer = Customer.create(phone, name);

        return customerRepository.create(customer);
    }

    @Override
    @Transactional
    public Customer updateCustomer(
            UUID id,
            String phone,
            String name
    ) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new CustomerNotFoundException(id)
                );

        if (phone != null
                && !phone.isBlank()
                && !phone.equals(customer.getPhoneValue())
                && customerRepository.existsByPhone(phone)) {

            throw new PhoneAlreadyExistsException(phone);
        }

        customer.update(phone, name);

        return customerRepository.update(customer);
    }
}