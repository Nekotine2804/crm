package com.sofitech.hoamaimart.transaction.adapter.in.web;

import com.sofitech.hoamaimart.transaction.adapter.in.web.dto.CreateTransactionRequest;
import com.sofitech.hoamaimart.transaction.adapter.in.web.dto.TransactionResponse;
import com.sofitech.hoamaimart.transaction.domain.model.Transaction;
import com.sofitech.hoamaimart.transaction.domain.port.in.CreateTransactionCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller - Adapter IN.
 */
@RestController
@RequestMapping("/api/v1/pos/transactions")
@Tag(name = "Transactions", description = "Point of Sale transaction endpoints")
public class TransactionController {

    private final CreateTransactionCommandService createTransactionService;

    public TransactionController(CreateTransactionCommandService createTransactionService) {
        this.createTransactionService = createTransactionService;
    }

    /**
     * POST /api/v1/pos/transactions - Tạo giao dịch mới.
     */
    @Operation(summary = "Create a new transaction", description = "Creates a new POS transaction. Idempotent based on transactionCode.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transaction created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Transaction already exists (idempotent)")
    })
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Transaction details",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateTransactionRequest.class)))
            @Valid @RequestBody CreateTransactionRequest request) {
        Transaction transaction = createTransactionService.createTransaction(
                request.customerId(),
                request.storeId(),
                request.transactionCode(),
                request.amount()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(TransactionResponse.from(transaction));
    }
}
