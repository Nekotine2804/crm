package com.sofitech.hoamaimart.transaction.application.service;

import com.sofitech.hoamaimart.shared.error.BusinessErrorCode;
import com.sofitech.hoamaimart.shared.error.BusinessException;
import com.sofitech.hoamaimart.transaction.domain.model.Transaction;
import com.sofitech.hoamaimart.transaction.domain.port.in.CreateTransactionCommandService;
import com.sofitech.hoamaimart.transaction.domain.port.out.CustomerClient;
import com.sofitech.hoamaimart.transaction.domain.port.out.EventPublisher;
import com.sofitech.hoamaimart.transaction.domain.port.out.TransactionRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Application service: xử lý use case "tạo giao dịch".
 *
 * Flow:
 * 1. Auto-generate transactionCode nếu không được cung cấp
 * 2. Idempotency: check transactionCode đã tồn tại chưa
 * 3. Validate customer (tồn tại + ACTIVE)
 * 4. Validate amount > 0
 * 5. Tạo transaction
 * 6. Lưu DB
 * 7. Publish event
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

        // 2. Auto-generate transactionCode nếu không được cung cấp
        if (transactionCode == null || transactionCode.isBlank()) {
            transactionCode = generateTransactionCode();
        }

        // 3. Idempotency: nếu transactionCode đã tồn tại → trả về transaction cũ
        var existing = transactionRepository.findByTransactionCode(transactionCode);
        if (existing.isPresent()) {
            return existing.get();
        }

        // 4. Validate customer (cross-service call)
        customerClient.validateActiveCustomer(customerId);

        // 5. Tạo domain transaction
        Transaction transaction;
        try {
            transaction = Transaction.create(customerId, storeId, transactionCode, amount);
        } catch (IllegalArgumentException e) {
            throw BusinessException.of(BusinessErrorCode.POS_INVALID_AMOUNT, e.getMessage());
        }

        // 6. Lưu DB
        Transaction saved = transactionRepository.save(transaction);

        // 7. Publish event
        eventPublisher.publishTransactionCompleted(saved);

        return saved;
    }

    /**
     * Tạo transaction code tự động theo format: TXN-{timestamp}-{uuid-first-8chars}
     * Ví dụ: TXN-20260902-abc12345
     */
    private String generateTransactionCode() {
        String timestamp = java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuidPart = UUID.randomUUID().toString().substring(0, 8);
        return "TXN-" + timestamp + "-" + uuidPart;
    }
}