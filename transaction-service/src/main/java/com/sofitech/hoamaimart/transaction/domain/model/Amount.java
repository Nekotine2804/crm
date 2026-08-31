package com.sofitech.hoamaimart.transaction.domain.model;

import java.math.BigDecimal;

/**
 * Value Object: số tiền giao dịch.
 * Immutable, luôn positive.
 */
public record Amount(BigDecimal value) {

    public Amount {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền phải lớn hơn 0");
        }
    }

    public static Amount of(BigDecimal value) {
        return new Amount(value);
    }

    /**
     * Tính điểm loyalty: 1 điểm / 1000 VND.
     */
    public int toLoyaltyPoints() {
        return value.divide(BigDecimal.valueOf(1000)).intValue();
    }
}