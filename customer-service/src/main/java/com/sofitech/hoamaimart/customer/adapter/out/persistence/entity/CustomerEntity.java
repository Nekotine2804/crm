package com.sofitech.hoamaimart.customer.adapter.out.persistence.entity;

import com.sofitech.hoamaimart.customer.domain.model.Customer;
import com.sofitech.hoamaimart.customer.domain.model.vo.CustomerName;
import com.sofitech.hoamaimart.customer.domain.model.vo.PhoneNumber;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA entity cho bảng customers.
 */
@Entity
@Table(name = "customers")
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "phone", nullable = false, unique = true, length = 20)
    private String phone;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected CustomerEntity() {}

    public CustomerEntity(UUID id, String phone, String name, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.phone = phone;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Convert to Domain
    public Customer toDomain() {
        return new Customer(
                this.id,
                PhoneNumber.of(this.phone),
                CustomerName.of(this.name),
                this.createdAt,
                this.updatedAt
        );
    }

    // Create from Domain
    public static CustomerEntity fromDomain(Customer customer) {
        return new CustomerEntity(
                customer.getId(),
                customer.getPhoneValue(),
                customer.getNameValue(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}