package com.sofitech.hoamaimart.transaction.adapter.in.web;

import com.sofitech.hoamaimart.transaction.adapter.in.web.dto.CreateTransactionRequest;
import com.sofitech.hoamaimart.transaction.adapter.in.web.dto.RefundRequest;
import com.sofitech.hoamaimart.transaction.adapter.in.web.dto.RefundResponse;
import com.sofitech.hoamaimart.transaction.adapter.in.web.dto.TransactionResponse;
import com.sofitech.hoamaimart.transaction.domain.exception.InvalidTransactionStateException;
import com.sofitech.hoamaimart.transaction.domain.exception.TransactionAlreadyRefundedException;
import com.sofitech.hoamaimart.transaction.domain.exception.TransactionNotFoundException;
import com.sofitech.hoamaimart.transaction.domain.model.Transaction;
import com.sofitech.hoamaimart.transaction.domain.port.in.CreateTransactionCommandService;
import com.sofitech.hoamaimart.transaction.domain.port.in.RefundTransactionService;
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

import java.util.Map;
import java.util.UUID;

/**
 * REST Controller - Adapter IN.
 */
@RestController
@RequestMapping("/api/v1/pos/transactions")
@Tag(name = "Transactions", description = "Point of Sale transaction endpoints")
public class TransactionController {

    private final CreateTransactionCommandService createTransactionService;
    private final RefundTransactionService refundTransactionService;

    public TransactionController(
            CreateTransactionCommandService createTransactionService,
            RefundTransactionService refundTransactionService
    ) {
        this.createTransactionService = createTransactionService;
        this.refundTransactionService = refundTransactionService;
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

    /**
     * POST /api/v1/pos/transactions/{id}/refund - Refund một giao dịch.
     */
    @Operation(summary = "Refund a transaction", description = "Refunds a completed transaction and reverses loyalty points")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction refunded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Transaction not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Transaction already refunded",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{id}/refund")
    public ResponseEntity<?> refundTransaction(
            @PathVariable UUID id,
            @Valid @RequestBody RefundRequest request
    ) {
        try {
            Transaction refunded = refundTransactionService.refundTransaction(id, request.reason());
            return ResponseEntity.ok(RefundResponse.from(refunded));
        } catch (TransactionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "code", "TRANSACTION_NOT_FOUND",
                            "message", e.getMessage(),
                            "status", 404
                    ));
        } catch (TransactionAlreadyRefundedException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "code", "ALREADY_REFUNDED",
                            "message", e.getMessage(),
                            "status", 409
                    ));
        } catch (InvalidTransactionStateException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "code", "INVALID_STATE",
                            "message", e.getMessage(),
                            "status", 400
                    ));
        }
    }

    /**
     * POST /api/v1/pos/transactions/{id}/cancel - Cancel một giao dịch (chỉ PENDING).
     */
    @Operation(summary = "Cancel a transaction", description = "Cancels a pending transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction cancelled successfully"),
            @ApiResponse(responseCode = "400", description = "Cannot cancel non-pending transaction"),
            @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelTransaction(@PathVariable UUID id) {
        try {
            Transaction cancelled = refundTransactionService.cancelTransaction(id);
            return ResponseEntity.ok(TransactionResponse.from(cancelled));
        } catch (TransactionNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "code", "TRANSACTION_NOT_FOUND",
                            "message", e.getMessage(),
                            "status", 404
                    ));
        } catch (InvalidTransactionStateException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "code", "INVALID_STATE",
                            "message", e.getMessage(),
                            "status", 400
                    ));
        }
    }
}
