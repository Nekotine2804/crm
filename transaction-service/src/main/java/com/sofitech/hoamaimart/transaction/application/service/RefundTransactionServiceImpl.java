package com.sofitech.hoamaimart.transaction.application.service;

import com.sofitech.hoamaimart.transaction.domain.exception.InvalidTransactionStateException;
import com.sofitech.hoamaimart.transaction.domain.exception.TransactionAlreadyRefundedException;
import com.sofitech.hoamaimart.transaction.domain.exception.TransactionNotFoundException;
import com.sofitech.hoamaimart.transaction.domain.model.Transaction;
import com.sofitech.hoamaimart.transaction.domain.port.in.RefundTransactionService;
import com.sofitech.hoamaimart.transaction.domain.port.out.EventPublisher;
import com.sofitech.hoamaimart.transaction.domain.port.out.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application service: xử lý use case refund/cancel transaction.
 */
public class RefundTransactionServiceImpl implements RefundTransactionService {

    private static final Logger log = LoggerFactory.getLogger(RefundTransactionServiceImpl.class);

    private final TransactionRepository transactionRepository;
    private final EventPublisher eventPublisher;

    public RefundTransactionServiceImpl(
            TransactionRepository transactionRepository,
            EventPublisher eventPublisher
    ) {
        this.transactionRepository = transactionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Transaction refundTransaction(java.util.UUID transactionId, String reason) {
        log.info("Processing refund for transaction: {}", transactionId);

        // 1. Tìm transaction
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));

        // 2. Kiểm tra trạng thái
        if (transaction.getStatus() == Transaction.Status.REFUNDED) {
            throw new TransactionAlreadyRefundedException(transactionId);
        }
        if (transaction.getStatus() == Transaction.Status.CANCELLED) {
            throw new InvalidTransactionStateException("Transaction đã bị cancel, không thể refund");
        }

        // 3. Refund
        Transaction refunded = transaction.refund(reason);
        Transaction saved = transactionRepository.save(refunded);

        // 4. Publish event để loyalty-service xử lý reverse points
        eventPublisher.publishTransactionRefunded(saved, reason);

        log.info("Transaction {} refunded successfully. Reason: {}", transactionId, reason);
        return saved;
    }

    @Override
    public Transaction cancelTransaction(java.util.UUID transactionId) {
        log.info("Processing cancel for transaction: {}", transactionId);

        // 1. Tìm transaction
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException(transactionId));

        // 2. Kiểm tra trạng thái
        if (!transaction.canCancel()) {
            throw new InvalidTransactionStateException(
                    "Không thể cancel transaction ở trạng thái: " + transaction.getStatus());
        }

        // 3. Cancel
        Transaction cancelled = transaction.cancel();
        Transaction saved = transactionRepository.save(cancelled);

        log.info("Transaction {} cancelled successfully", transactionId);
        return saved;
    }
}
