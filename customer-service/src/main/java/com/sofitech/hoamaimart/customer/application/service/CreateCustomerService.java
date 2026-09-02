package com.sofitech.hoamaimart.customer.application.service;

import com.sofitech.hoamaimart.customer.domain.exception.CustomerNotFoundException;
import com.sofitech.hoamaimart.customer.domain.exception.PhoneAlreadyExistsException;
import com.sofitech.hoamaimart.customer.domain.model.Customer;
import com.sofitech.hoamaimart.customer.domain.port.in.CustomerCommandService;
import com.sofitech.hoamaimart.customer.domain.port.out.CustomerRepository;
import com.sofitech.hoamaimart.customer.domain.port.out.EventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class CreateCustomerService implements CustomerCommandService {

    private final CustomerRepository customerRepository;
    private final EventPublisher eventPublisher;

    public CreateCustomerService(CustomerRepository customerRepository, EventPublisher eventPublisher) {
        this.customerRepository = customerRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional
    public Customer createCustomer(String phone, String name, LocalDate dateOfBirth) {

        if (customerRepository.existsByPhone(phone)) {
            throw new PhoneAlreadyExistsException(phone);
        }

        Customer customer = Customer.create(phone, name, dateOfBirth);

        Customer savedCustomer = customerRepository.create(customer);
        eventPublisher.publishCustomerCreated(savedCustomer);
        return savedCustomer;
    }

    @Override
    @Transactional
    public Customer updateCustomer(
            UUID id,
            String phone,
            String name,
            LocalDate dateOfBirth
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

        customer.update(phone, name, dateOfBirth);

        return customerRepository.update(customer);
    }
}
