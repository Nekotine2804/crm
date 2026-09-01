package com.sofitech.hoamaimart.notification.domain.model.vo;

import java.util.UUID;

/**
 * Value Object: Customer ID.
 */
public final class CustomerId {
    private final UUID value;

    private CustomerId(UUID value) {
        this.value = value;
    }

    public static CustomerId of(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        return new CustomerId(value);
    }

    public static CustomerId of(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Customer ID cannot be blank");
        }
        try {
            return new CustomerId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID format: " + value);
        }
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerId that = (CustomerId) o;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
