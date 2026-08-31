package com.sofitech.hoamaimart.transaction.application.service;

import com.sofitech.hoamaimart.shared.error.BusinessErrorCode;
import com.sofitech.hoamaimart.shared.error.BusinessException;
import com.sofitech.hoamaimart.transaction.domain.model.Transaction;
import com.sofitech.hoamaimart.transaction.domain.port.in.CreateTransactionCommandService;
import com.sofitech.hoamaimart.transaction.domain.port.out.CustomerClient;
import com.sofitech.hoamaimart.transaction.domain.port.out.EventPublisher;
import com.sofitech.hoamaimart.transaction.domain.port.out.TransactionRepository;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Application service: xử lý use case "tạo giao dịch".
 *
 * Flow:
 * 1. Idempotency: check transactionCode đã tồn tại chưa
 * 2. Validate customer (tồn tại + ACTIVE)
 * 3. Validate amount > 0
 * 4. Tạo transaction
 * 5. Lưu DB
 * 6. Publish event
 */
public class CreateTransactionService implements CreateTransactionCommandService {

    private final TransactionRepository transactionRepository;
    private final EventPublisher eventPublisher;
    private final CustomerClient customerClient;

    public CreateTransactionService(
            TransactionRepository transactionRepository,
            EventPublisher eventPublisher,
            CustomerClient customerClient
    ) {
        this.transactionRepository = transactionRepository;
        this.eventPublisher = eventPublisher;
        this.customerClient = customerClient;
    }

    @Override
    public Transaction createTransaction(UUID customerId, String storeId, String transactionCode, BigDecimal amount) {
        // 1. Validate amount > 0
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw BusinessException.of(BusinessErrorCode.POS_INVALID_AMOUNT,
                    "Số tiền phải > 0, nhận: " + amount);
        }

        // 2. Idempotency: nếu transactionCode đã tồn tại → trả về transaction cũ
        var existing = transactionRepository.findByTransactionCode(transactionCode);
        if (existing.isPresent()) {
            return existing.get();
        }

        // 3. Validate customer (cross-service call)
        customerClient.validateActiveCustomer(customerId);

        // 4. Tạo domain transaction
        Transaction transaction;
        try {
            transaction = Transaction.create(customerId, storeId, transactionCode, amount);
        } catch (IllegalArgumentException e) {
            throw BusinessException.of(BusinessErrorCode.POS_INVALID_AMOUNT, e.getMessage());
        }

        // 5. Lưu DB
        Transaction saved = transactionRepository.save(transaction);

        // 6. Publish event
        eventPublisher.publishTransactionCompleted(saved);

        return saved;
    }
}