package com.sofitech.hoamaimart.customer.adapter.out.persistence.mapper;

import com.sofitech.hoamaimart.customer.adapter.out.persistence.entity.CustomerEntity;
import com.sofitech.hoamaimart.customer.domain.model.Customer;
import org.springframework.stereotype.Component;

/**
 * Mapper chuyển đổi giữa Domain entity và JPA entity.
 */
@Component
public class CustomerMapper {

    /**
     * Domain → Entity
     */
    public CustomerEntity toEntity(Customer domain) {
        return CustomerEntity.fromDomain(domain);
    }

    /**
     * Entity → Domain
     */
    public Customer toDomain(CustomerEntity entity) {
        return entity.toDomain();
    }
}