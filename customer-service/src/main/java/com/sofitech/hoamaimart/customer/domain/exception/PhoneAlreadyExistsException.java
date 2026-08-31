package com.sofitech.hoamaimart.customer.domain.exception;

/**
 * Exception khi SĐT khách hàng đã tồn tại trong hệ thống.
 */
public class PhoneAlreadyExistsException extends RuntimeException {

    private final String phone;

    public PhoneAlreadyExistsException(String phone) {
        super("SĐT đã được đăng ký: " + phone);
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }
}