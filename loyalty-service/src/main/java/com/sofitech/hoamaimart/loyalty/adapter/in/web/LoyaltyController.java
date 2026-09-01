package com.sofitech.hoamaimart.loyalty.adapter.in.web;

import com.sofitech.hoamaimart.loyalty.adapter.in.web.dto.LoyaltyResponse;
import com.sofitech.hoamaimart.loyalty.adapter.in.web.dto.RedeemRequest;
import com.sofitech.hoamaimart.loyalty.domain.model.LoyaltyAccount;
import com.sofitech.hoamaimart.loyalty.domain.port.in.LoyaltyCommandService;
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

import java.util.UUID;

/**
 * REST Controller - Adapter IN.
 */
@RestController
@RequestMapping("/api/v1/customers/{customerId}/loyalty")
@Tag(name = "Loyalty", description = "Loyalty points management endpoints")
public class LoyaltyController {

    private final LoyaltyCommandService loyaltyCommandService;
    private final LoyaltyRepository loyaltyRepository;

    public LoyaltyController(
            LoyaltyCommandService loyaltyCommandService,
            LoyaltyRepository loyaltyRepository
    ) {
        this.loyaltyCommandService = loyaltyCommandService;
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
        LoyaltyAccount redeemed = loyaltyCommandService.redeem(customerId, request.points());
        return ResponseEntity.ok(LoyaltyResponse.from(redeemed));
    }
}
