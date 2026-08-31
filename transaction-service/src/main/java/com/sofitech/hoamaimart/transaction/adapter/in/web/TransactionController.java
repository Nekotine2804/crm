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
@RequestMapping("/api/transactions")
public class TransactionController {

    private final CreateTransactionCommandService createTransactionService;

    public TransactionController(CreateTransactionCommandService createTransactionService) {
        this.createTransactionService = createTransactionService;
    }

    /**
     * POST /api/transactions - Tạo giao dịch mới.
     *
     * Request body:
     * {
     *   "customerId": "550e8400-...",
     *   "storeId": "STORE_001",
     *   "amount": 150000.00
     * }
     *
     * Response (201 Created):
     * {
     *   "transactionId": "...",
     *   "customerId": "...",
     *   "storeId": "STORE_001",
     *   "amount": 150000.00,
     *   "status": "COMPLETED",
     *   "createdAt": "2024-01-15T10:30:00Z"
     * }
     */
    @PostMapping
    public ResponseEntity<?> createTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        try {
            Transaction transaction = createTransactionService.createTransaction(
                    request.customerId(),
                    request.storeId(),
                    request.amount()
            );
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(TransactionResponse.from(transaction));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("VALIDATION_ERROR", e.getMessage(), 400));
        }
    }
}