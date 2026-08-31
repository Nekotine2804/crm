package com.sofitech.hoamaimart.loyalty.domain.model;

import java.math.BigDecimal;


public record Points(int value) {

    public Points {
        if (value < 0) {
            throw new IllegalArgumentException("Điểm không thể âm");
        }
    }

    public static Points of(int value) {
        return new Points(value);
    }

    /**
     * Tạo Points từ số tiền: 10.000 VND = 1 điểm
     */
    public static Points fromAmount(BigDecimal amount) {
        int points = amount.divide(BigDecimal.valueOf(10_000)).intValue();
        return new Points(Math.max(0, points));
    }

    /**
     * Cộng điểm
     */
    public Points add(Points other) {
        return new Points(this.value + other.value);
    }

    public boolean isZero() {
        return value == 0;
    }

    /**
     * Kiểm tra đủ điểm cho tier
     */
    public boolean meetsThreshold(int threshold) {
        return value >= threshold;
    }
}