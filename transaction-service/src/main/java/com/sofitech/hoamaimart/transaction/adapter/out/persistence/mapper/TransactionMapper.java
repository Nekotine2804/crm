package com.sofitech.hoamaimart.transaction.adapter.out.persistence.mapper;

import com.sofitech.hoamaimart.transaction.adapter.out.persistence.entity.TransactionEntity;
import com.sofitech.hoamaimart.transaction.domain.model.Transaction;
import org.springframework.stereotype.Component;

/**
 * Mapper giữa Domain và JPA entity.
 * Dùng method thuần Java thay vì MapStruct.
 */
@Component
public class TransactionMapper {

    public TransactionEntity toEntity(Transaction domain) {
        return TransactionEntity.fromDomain(domain);
    }

    public Transaction toDomain(TransactionEntity entity) {
        return entity.toDomain();
    }
}