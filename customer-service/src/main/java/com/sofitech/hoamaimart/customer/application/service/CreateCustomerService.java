package com.sofitech.hoamaimart.customer.application.service;

import com.sofitech.hoamaimart.customer.domain.exception.PhoneAlreadyExistsException;
import com.sofitech.hoamaimart.customer.domain.model.Customer;
import com.sofitech.hoamaimart.customer.domain.port.in.CustomerCommandService;
import com.sofitech.hoamaimart.customer.domain.port.out.CustomerRepository;

/**
 * Application service: xử lý use case "tạo khách hàng mới".
 */
public class CreateCustomerService implements CustomerCommandService {

    private final CustomerRepository customerRepository;

    public CreateCustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer createCustomer(String phone, String name) {
        // 1. Validate: kiểm tra SĐT đã tồn tại chưa
        if (customerRepository.existsByPhone(phone)) {
            throw new PhoneAlreadyExistsException(phone);
        }

        // 2. Domain: tạo Customer entity
        Customer customer = Customer.create(phone, name);

        // 3. Persistence: lưu vào DB
        return customerRepository.save(customer);
    }
}