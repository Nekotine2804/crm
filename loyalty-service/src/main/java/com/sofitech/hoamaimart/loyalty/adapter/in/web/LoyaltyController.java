package com.sofitech.hoamaimart.loyalty.adapter.in.web;

import com.sofitech.hoamaimart.loyalty.adapter.in.web.dto.EarnPointsRequest;
import com.sofitech.hoamaimart.loyalty.adapter.in.web.dto.EarnPointsResponse;
import com.sofitech.hoamaimart.loyalty.adapter.in.web.dto.LoyaltyResponse;
import com.sofitech.hoamaimart.loyalty.adapter.in.web.dto.PointHistoryResponse;
import com.sofitech.hoamaimart.loyalty.adapter.in.web.dto.RedeemRequest;
import com.sofitech.hoamaimart.loyalty.adapter.in.web.dto.RedeemResponse;
import com.sofitech.hoamaimart.loyalty.application.service.LoyaltyService;
import com.sofitech.hoamaimart.loyalty.domain.model.LoyaltyAccount;
import com.sofitech.hoamaimart.loyalty.domain.model.PointTransaction;
import com.sofitech.hoamaimart.loyalty.domain.port.out.LoyaltyRepository;
import com.sofitech.hoamaimart.shared.error.BusinessErrorCode;
import com.sofitech.hoamaimart.shared.error.BusinessException;
import com.sofitech.hoamaimart.shared.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller - Adapter IN.
 */
@RestController
@RequestMapping("/api/v1/customers/{customerId}/loyalty")
@Tag(name = "Loyalty", description = "Loyalty points management endpoints")
public class LoyaltyController {

    private final LoyaltyService loyaltyService;
    private final LoyaltyRepository loyaltyRepository;

    public LoyaltyController(
            LoyaltyService loyaltyService,
            LoyaltyRepository loyaltyRepository
    ) {
        this.loyaltyService = loyaltyService;
        this.loyaltyRepository = loyaltyRepository;
    }

    /**
     * GET /api/v1/customers/{customerId}/loyalty
     * Lấy thông tin tài khoản loyalty của khách.
     */
    @Operation(summary = "Get loyalty account", description = "Retrieves loyalty account information for a customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loyalty account found"),
            @ApiResponse(responseCode = "404", description = "Loyalty account not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<?> getLoyaltyAccount(
            @Parameter(description = "Customer UUID") @PathVariable UUID customerId) {
        return loyaltyRepository.findByCustomerId(customerId)
                .map(LoyaltyResponse::from)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> BusinessException.of(
                    BusinessErrorCode.LOYALTY_ACCOUNT_NOT_FOUND,
                    "Không tìm thấy tài khoản loyalty cho khách: " + customerId
                ));
    }

    /**
     * POST /api/v1/customers/{customerId}/loyalty/earn
     * Tích điểm loyalty (Earn Point).
     */
    @Operation(summary = "Earn loyalty points", description = "Awards loyalty points for a purchase transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Points earned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "409", description = "Transaction already processed (idempotent)")
    })
    @PostMapping("/earn")
    public ResponseEntity<?> earnPoints(
            @Parameter(description = "Customer UUID") @PathVariable UUID customerId,
            @Valid @RequestBody EarnPointsRequest request
    ) {
        var pointTransaction = loyaltyService.earnPoints(
                customerId,
                request.transactionCode(),
                request.amount()
        );

        if (pointTransaction == null) {
            // Idempotent: đã xử lý rồi
            return ResponseEntity.ok(new EarnPointsResponse(
                    customerId,
                    0,
                    loyaltyRepository.findByCustomerId(customerId)
                            .map(acc -> acc.getPoints().value())
                            .orElse(0),
                    "ALREADY_PROCESSED",
                    "Transaction đã được xử lý trước đó"
            ));
        }

        return ResponseEntity.ok(EarnPointsResponse.from(pointTransaction, customerId));
    }

    /**
     * POST /api/v1/customers/{customerId}/loyalty/redeem
     * Quy đổi điểm loyalty.
     */
    @Operation(summary = "Redeem loyalty points", description = "Redeem loyalty points for a customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Points redeemed successfully"),
            @ApiResponse(responseCode = "400", description = "Insufficient points or invalid request")
    })
    @PostMapping("/redeem")
    public ResponseEntity<?> redeem(
            @Parameter(description = "Customer UUID") @PathVariable UUID customerId,
            @Valid @RequestBody RedeemRequest request
    ) {
        LoyaltyAccount redeemed = loyaltyService.redeem(customerId, request.points());
        
        // Record point transaction
        String redemptionId = UUID.randomUUID().toString();
        loyaltyService.recordRedeem(customerId, request.points(), redemptionId);

        return ResponseEntity.ok(RedeemResponse.from(redeemed, redemptionId));
    }

    /**
     * GET /api/v1/customers/{customerId}/loyalty/points/history
     * Lấy lịch sử point transactions.
     */
    @Operation(summary = "Get point transaction history", description = "Retrieves the loyalty point transaction history for a customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Point history retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping("/points/history")
    public ResponseEntity<?> getPointHistory(
            @Parameter(description = "Customer UUID") @PathVariable UUID customerId
    ) {
        // Verify customer exists
        loyaltyRepository.findByCustomerId(customerId)
                .orElseThrow(() -> BusinessException.of(
                    BusinessErrorCode.LOYALTY_ACCOUNT_NOT_FOUND,
                    "Không tìm thấy tài khoản loyalty cho khách: " + customerId
                ));

        List<PointTransaction> transactions = loyaltyRepository.findPointTransactionsByCustomerId(customerId);
        List<PointHistoryResponse> history = transactions.stream()
                .map(PointHistoryResponse::from)
                .toList();

        return ResponseEntity.ok(history);
    }
}
