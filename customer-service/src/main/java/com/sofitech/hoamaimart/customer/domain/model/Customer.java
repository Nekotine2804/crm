package com.sofitech.hoamaimart.customer.domain.model;

import com.sofitech.hoamaimart.customer.domain.model.vo.CustomerName;
import com.sofitech.hoamaimart.customer.domain.model.vo.PhoneNumber;

import java.time.Instant;
import java.util.UUID;

/**
 * Customer aggregate root - thuần Java, không phụ thuộc Spring/JPA.
 */
public class Customer {

    private final UUID id;
    private PhoneNumber phone;
    private CustomerName name;
    private Instant createdAt;
    private Instant updatedAt;

    public Customer(UUID id, PhoneNumber phone, CustomerName name, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.phone = phone;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Factory method: tạo customer mới.
     */
    public static Customer create(String phone, String name) {
        Instant now = Instant.now();
        return new Customer(
                UUID.randomUUID(),
                PhoneNumber.of(phone),
                CustomerName.of(name),
                now,
                now
        );
    }

    /**
     * Update thông tin customer.
     */
    public void update(String phone, String name) {
        if (phone != null && !phone.isBlank()) {
            this.phone = PhoneNumber.of(phone);
        }
        if (name != null && !name.isBlank()) {
            this.name = CustomerName.of(name);
        }
        this.updatedAt = Instant.now();
    }

    /**
     * Update chỉ name.
     */
    public void updateName(String name) {
        if (name != null && !name.isBlank()) {
            this.name = CustomerName.of(name);
        }
        this.updatedAt = Instant.now();
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public PhoneNumber getPhone() {
        return phone;
    }

    public CustomerName getName() {
        return name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    // Convenience methods - trả về String
    public String getPhoneValue() {
        return phone.value();
    }

    public String getNameValue() {
        return name.value();
    }
}
