package com.sofitech.hoamaimart.loyalty.adapter.in.web;

import com.sofitech.hoamaimart.loyalty.adapter.in.web.dto.LoyaltyResponse;
import com.sofitech.hoamaimart.loyalty.adapter.in.web.dto.RedeemRequest;
import com.sofitech.hoamaimart.loyalty.domain.model.LoyaltyAccount;
import com.sofitech.hoamaimart.loyalty.domain.port.in.LoyaltyCommandService;
import com.sofitech.hoamaimart.loyalty.domain.port.out.LoyaltyRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller - Adapter IN.
 */
@RestController
@RequestMapping("/api/v1/customers/{customerId}/loyalty")
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
    @GetMapping
    public ResponseEntity<?> getLoyaltyAccount(@PathVariable UUID customerId) {
        return loyaltyRepository.findByCustomerId(customerId)
                .map(LoyaltyResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/v1/customers/{customerId}/loyalty/redeem
     * Quy đổi điểm loyalty.
     *
     * Request body:
     * {
     *   "points": 100
     * }
     *
     * Response (200 OK):
     * {
     *   "customerId": "...",
     *   "points": 400,
     *   "tier": "GOLD",
     *   "updatedAt": "2024-01-15T10:30:00Z"
     * }
     */
    @PostMapping("/redeem")
    public ResponseEntity<?> redeem(
            @PathVariable UUID customerId,
            @Valid @RequestBody RedeemRequest request
    ) {
        LoyaltyAccount redeemed = loyaltyCommandService.redeem(customerId, request.points());
        return ResponseEntity.ok(LoyaltyResponse.from(redeemed));
    }
}