package com.sofitech.hoamaimart.customer.adapter.in.web;

import com.sofitech.hoamaimart.customer.adapter.in.web.dto.CustomerExistsResponse;
import com.sofitech.hoamaimart.customer.adapter.in.web.dto.CustomerVO;
import com.sofitech.hoamaimart.customer.domain.port.in.CustomerQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

/**
 * REST Controller - Query operations.
 */
@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customers", description = "Customer query endpoints")
public class CustomerQueryController {

    private final CustomerQueryService customerQueryService;
    
    @Value("${services.loyalty.url:http://localhost:8082}")
    private String loyaltyServiceUrl;

    public CustomerQueryController(CustomerQueryService customerQueryService) {
        this.customerQueryService = customerQueryService;
    }

    /**
     * GET /api/customers/{id} - Lấy customer theo ID.
     */
    @Operation(summary = "Get customer by ID", description = "Retrieves a customer by their unique ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomerVO> getCustomerById(
            @Parameter(description = "Customer UUID") @PathVariable UUID id) {
        return customerQueryService.findById(id)
                .map(CustomerVO::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/customers/phone/{phone} - Lấy customer theo SĐT (full info).
     */
    @Operation(summary = "Get customer by phone", description = "Retrieves a customer by their phone number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    @GetMapping("/phone/{phone}")
    public ResponseEntity<CustomerVO> getCustomerByPhone(
            @Parameter(description = "Phone number") @PathVariable String phone) {
        return customerQueryService.findByPhone(phone)
                .map(CustomerVO::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/customers/check?phone=xxx - Kiểm tra khách đã tồn tại chưa.
     */
    @Operation(summary = "Check if customer exists", description = "Quick check if a customer exists by phone number (for POS)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Check completed")
    })
    @GetMapping("/check")
    public ResponseEntity<CustomerExistsResponse> checkCustomerExists(
            @Parameter(description = "Phone number to check") @RequestParam String phone) {
        if (customerQueryService.existsByPhone(phone)) {
            return customerQueryService.findByPhone(phone)
                    .map(c -> ResponseEntity.ok(
                            CustomerExistsResponse.found(phone, c.getId())))
                    .orElse(ResponseEntity.ok(
                            CustomerExistsResponse.notFound(phone)));
        }
        return ResponseEntity.ok(CustomerExistsResponse.notFound(phone));
    }

    /**
     * GET /api/customers/{id}/points/history - Lấy lịch sử điểm loyalty.
     * Proxy request sang loyalty-service.
     */
    @Operation(summary = "Get point transaction history", description = "Retrieves loyalty point transaction history for a customer (proxies to loyalty-service)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Point history retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Customer or loyalty account not found"),
            @ApiResponse(responseCode = "502", description = "Loyalty service unavailable")
    })
    @GetMapping("/{id}/points/history")
    public ResponseEntity<?> getPointHistory(
            @Parameter(description = "Customer UUID") @PathVariable UUID id) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = loyaltyServiceUrl + "/api/v1/customers/" + id + "/loyalty/points/history";
            var response = restTemplate.getForEntity(url, Object.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(502)
                    .body(Map.of(
                            "code", "LOYALTY_SERVICE_ERROR",
                            "message", "Không thể lấy lịch sử điểm: " + e.getMessage(),
                            "status", 502
                    ));
        }
    }
}
