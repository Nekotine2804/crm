package com.sofitech.hoamaimart.customer.adapter.out.persistence.repository;

import com.sofitech.hoamaimart.customer.adapter.out.persistence.entity.CustomerEntity;
import com.sofitech.hoamaimart.customer.domain.exception.CustomerNotFoundException;
import com.sofitech.hoamaimart.customer.domain.model.Customer;
import com.sofitech.hoamaimart.customer.domain.port.out.CustomerRepository;

import java.util.Optional;
import java.util.UUID;

public class CustomerRepositoryAdapter implements CustomerRepository {

    private final CustomerJpaRepository jpaRepository;

    public CustomerRepositoryAdapter(CustomerJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Customer create(Customer customer) {

        CustomerEntity entity = CustomerEntity.fromDomain(customer);

        CustomerEntity saved = jpaRepository.save(entity);

        return saved.toDomain();
    }

    @Override
    public Customer update(Customer customer) {

        CustomerEntity entity = jpaRepository.findById(customer.getId())
                .orElseThrow(() ->
                        new CustomerNotFoundException(customer.getId())
                );

        entity.setPhone(customer.getPhoneValue());
        entity.setName(customer.getNameValue());
        entity.setUpdatedAt(customer.getUpdatedAt());

        /*
         * Không gọi jpaRepository.save(entity).
         *
         * Entity đang managed trong transaction.
         * Hibernate dirty checking sẽ tự UPDATE khi transaction commit.
         */
        return entity.toDomain();
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(CustomerEntity::toDomain);
    }

    @Override
    public Optional<Customer> findByPhone(String phone) {
        return jpaRepository.findByPhone(phone)
                .map(CustomerEntity::toDomain);
    }

    @Override
    public boolean existsByPhone(String phone) {
        return jpaRepository.existsByPhone(phone);
    }
}