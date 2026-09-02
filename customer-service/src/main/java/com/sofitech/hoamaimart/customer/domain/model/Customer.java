package com.sofitech.hoamaimart.customer.domain.model;

import com.sofitech.hoamaimart.customer.domain.model.vo.CustomerName;
import com.sofitech.hoamaimart.customer.domain.model.vo.PhoneNumber;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Customer aggregate root - thuần Java, không phụ thuộc Spring/JPA.
 */
public class Customer {

    private final UUID id;
    private PhoneNumber phone;
    private CustomerName name;
    private LocalDate dateOfBirth;
    private Instant createdAt;
    private Instant updatedAt;

    public Customer(UUID id, PhoneNumber phone, CustomerName name, LocalDate dateOfBirth, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.phone = phone;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Factory method: tạo customer mới.
     */
    public static Customer create(String phone, String name, LocalDate dateOfBirth) {
        Instant now = Instant.now();
        return new Customer(
                UUID.randomUUID(),
                PhoneNumber.of(phone),
                CustomerName.of(name),
                dateOfBirth,
                now,
                now
        );
    }

    /**
     * Update thông tin customer.
     */
    public void update(String phone, String name, LocalDate dateOfBirth) {
        if (phone != null && !phone.isBlank()) {
            this.phone = PhoneNumber.of(phone);
        }
        if (name != null && !name.isBlank()) {
            this.name = CustomerName.of(name);
        }
        if (dateOfBirth != null) {
            this.dateOfBirth = dateOfBirth;
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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
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
