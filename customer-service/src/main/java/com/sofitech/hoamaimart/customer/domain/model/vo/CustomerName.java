package com.sofitech.hoamaimart.customer.domain.model.vo;

/**
 * Value Object: Tên khách hàng.
 * Immutable, so sánh bằng giá trị.
 */
public record CustomerName(String value) {

    public CustomerName {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Tên không được để trống");
        }
        String trimmed = value.trim();
        if (trimmed.length() < 2 || trimmed.length() > 255) {
            throw new IllegalArgumentException("Tên phải từ 2-255 ký tự");
        }
    }

    public static CustomerName of(String value) {
        return new CustomerName(value);
    }

    /**
     * Chuan hoa: viết hoa chữ đầu, không thừa khoảng trắng.
     */
    public String normalize() {
        if (value == null) return null;
        return value.trim()
                .toLowerCase()
                .substring(0, 1).toUpperCase()
                + value.trim().toLowerCase().substring(1);
    }
}