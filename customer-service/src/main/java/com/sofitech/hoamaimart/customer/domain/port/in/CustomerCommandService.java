package com.sofitech.hoamaimart.customer.domain.port.in;

import com.sofitech.hoamaimart.customer.domain.model.Customer;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Port IN: command service interface - use case cho customer.
 * Implement bởi application layer.
 */
public interface CustomerCommandService {

    /**
     * Tạo khách hàng mới.
     * @param phone SĐT khách hàng
     * @param name Tên khách hàng
     * @param dateOfBirth Ngày sinh (nullable)
     * @return Customer đã được tạo
     * @throws PhoneAlreadyExistsException nếu SĐT đã tồn tại
     */
    Customer createCustomer(String phone, String name, LocalDate dateOfBirth);

    /**
     * Cập nhật thông tin khách hàng.
     * @param id Customer ID
     * @param phone SĐT mới (nullable)
     * @param name Tên mới (nullable)
     * @param dateOfBirth Ngày sinh mới (nullable)
     * @return Customer đã được cập nhật
     * @throws CustomerNotFoundException nếu không tìm thấy
     * @throws PhoneAlreadyExistsException nếu SĐT đã tồn tại (khi đổi SĐT)
     */
    Customer updateCustomer(UUID id, String phone, String name, LocalDate dateOfBirth);
}
