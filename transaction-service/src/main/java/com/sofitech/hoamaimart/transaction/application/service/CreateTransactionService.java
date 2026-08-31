package com.sofitech.hoamaimart.transaction.application.service;

import com.sofitech.hoamaimart.transaction.domain.model.Transaction;
import com.sofitech.hoamaimart.transaction.domain.port.in.CreateTransactionCommandService;
import com.sofitech.hoamaimart.transaction.domain.port.out.EventPublisher;
import com.sofitech.hoamaimart.transaction.domain.port.out.TransactionRepository;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Application service: xử lý use case "tạo giao dịch".
 */
public class CreateTransactionService implements CreateTransactionCommandService {

    private final TransactionRepository transactionRepository;
    private final EventPublisher eventPublisher;

    public CreateTransactionService(TransactionRepository transactionRepository, EventPublisher eventPublisher) {
        this.transactionRepository = transactionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Transaction createTransaction(UUID customerId, String storeId, BigDecimal amount) {
        // 1. Domain: tạo Transaction
        Transaction transaction = Transaction.create(customerId, storeId, amount);

        // 2. Persistence: lưu vào DB
        Transaction saved = transactionRepository.save(transaction);

        // 3. Publish event: thông báo cho các service khác
        eventPublisher.publishTransactionCompleted(saved);

        return saved;
    }
}