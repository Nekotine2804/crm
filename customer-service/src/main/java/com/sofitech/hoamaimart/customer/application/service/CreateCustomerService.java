package com.sofitech.hoamaimart.customer.application.service;

import com.sofitech.hoamaimart.customer.domain.exception.CustomerNotFoundException;
import com.sofitech.hoamaimart.customer.domain.exception.PhoneAlreadyExistsException;
import com.sofitech.hoamaimart.customer.domain.model.Customer;
import com.sofitech.hoamaimart.customer.domain.port.in.CustomerCommandService;
import com.sofitech.hoamaimart.customer.domain.port.out.CustomerRepository;

import java.util.UUID;

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

    @Override
    public Customer updateCustomer(UUID id, String phone, String name) {
        // 1. Tìm customer hiện tại
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        // 2. Nếu đổi SĐT, kiểm tra SĐT mới đã tồn tại chưa
        if (phone != null && !phone.isBlank() && !phone.equals(customer.getPhoneValue())) {
            if (customerRepository.existsByPhone(phone)) {
                throw new PhoneAlreadyExistsException(phone);
            }
        }

        // 3. Update customer
        customer.update(phone, name);

        // 4. Lưu vào DB
        return customerRepository.save(customer);
    }
}
