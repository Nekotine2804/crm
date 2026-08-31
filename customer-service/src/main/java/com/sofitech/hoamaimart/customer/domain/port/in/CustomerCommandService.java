package com.sofitech.hoamaimart.customer.domain.port.in;

import com.sofitech.hoamaimart.customer.domain.model.Customer;

/**
 * Port IN: command service interface - use case cho customer.
 * Implement bởi application layer.
 */
public interface CustomerCommandService {

    /**
     * Tạo khách hàng mới.
     * @param phone SĐT khách hàng
     * @param name Tên khách hàng
     * @return Customer đã được tạo
     * @throws PhoneAlreadyExistsException nếu SĐT đã tồn tại
     */
    Customer createCustomer(String phone, String name);
}