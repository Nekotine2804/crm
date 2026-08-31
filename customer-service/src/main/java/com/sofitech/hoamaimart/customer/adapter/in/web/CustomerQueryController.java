package com.sofitech.hoamaimart.customer.adapter.in.web;

import com.sofitech.hoamaimart.customer.adapter.in.web.dto.CustomerExistsResponse;
import com.sofitech.hoamaimart.customer.adapter.in.web.dto.CustomerVO;
import com.sofitech.hoamaimart.customer.domain.port.in.CustomerQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST Controller - Query operations.
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerQueryController {

    private final CustomerQueryService customerQueryService;

    public CustomerQueryController(CustomerQueryService customerQueryService) {
        this.customerQueryService = customerQueryService;
    }

    /**
     * GET /api/customers/{id} - Lấy customer theo ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomerVO> getCustomerById(@PathVariable UUID id) {
        return customerQueryService.findById(id)
                .map(CustomerVO::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/customers/phone/{phone} - Lấy customer theo SĐT (full info).
     */
    @GetMapping("/phone/{phone}")
    public ResponseEntity<CustomerVO> getCustomerByPhone(@PathVariable String phone) {
        return customerQueryService.findByPhone(phone)
                .map(CustomerVO::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/customers/check?phone=xxx - Kiểm tra khách đã tồn tại chưa.
     * Dùng cho POS: nhanh, nhẹ, không cần full data.
     *
     * Response 200:
     * { "phone": "0912345678", "exists": true, "customerId": "..." }
     * { "phone": "0912345678", "exists": false, "customerId": null }
     */
    @GetMapping("/check")
    public ResponseEntity<CustomerExistsResponse> checkCustomerExists(
            @RequestParam String phone) {
        if (customerQueryService.existsByPhone(phone)) {
            return customerQueryService.findByPhone(phone)
                    .map(c -> ResponseEntity.ok(
                            CustomerExistsResponse.found(phone, c.getId())))
                    .orElse(ResponseEntity.ok(
                            CustomerExistsResponse.notFound(phone)));
        }
        return ResponseEntity.ok(CustomerExistsResponse.notFound(phone));
    }
}