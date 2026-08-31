package com.sofitech.hoamaimart.customer.domain.model.vo;

import java.util.regex.Pattern;

/**
 * Value Object: Số điện thoại.
 * Immutable, so sánh bằng giá trị.
 */
public record PhoneNumber(String value) {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^0[0-9]{9,10}$");

    public PhoneNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("SĐT không được để trống");
        }
        String trimmed = value.trim();
        if (!PHONE_PATTERN.matcher(trimmed).matches()) {
            throw new IllegalArgumentException("SĐT không hợp lệ: " + trimmed);
        }
    }

    /**
     * Factory method để tạo từ String.
     */
    public static PhoneNumber of(String value) {
        return new PhoneNumber(value);
    }

    /**
     * Format hiển thị: 0xx.xxx.xxxx
     */
    public String format() {
        String v = value.replaceAll("\\s+", "");
        if (v.length() == 10) {
            return v.substring(0, 3) + "." + v.substring(3, 6) + "." + v.substring(6);
        } else {
            return v.substring(0, 3) + "." + v.substring(3, 7) + "." + v.substring(7);
        }
    }
}