package com.sofitech.hoamaimart.transaction.adapter.in.web;

import com.sofitech.hoamaimart.transaction.adapter.in.web.dto.CreateTransactionRequest;
import com.sofitech.hoamaimart.transaction.adapter.in.web.dto.TransactionResponse;
import com.sofitech.hoamaimart.transaction.domain.model.Transaction;
import com.sofitech.hoamaimart.transaction.domain.port.in.CreateTransactionCommandService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller - Adapter IN.
 */
@RestController
@RequestMapping("/api/v1/pos/transactions")
public class TransactionController {

    private final CreateTransactionCommandService createTransactionService;

    public TransactionController(CreateTransactionCommandService createTransactionService) {
        this.createTransactionService = createTransactionService;
    }

    /**
     * POST /api/v1/pos/transactions - Tạo giao dịch mới.
     *
     * Request body:
     * {
     *   "customerId": "550e8400-...",
     *   "storeId": "STORE_001",
     *   "transactionCode": "POS-20240115-001",
     *   "amount": 150000.00
     * }
     *
     * Idempotent theo transactionCode.
     */
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        Transaction transaction = createTransactionService.createTransaction(
                request.customerId(),
                request.storeId(),
                request.transactionCode(),
                request.amount()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(TransactionResponse.from(transaction));
    }
}