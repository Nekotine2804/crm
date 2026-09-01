package com.sofitech.hoamaimart.customer.adapter.in.web;

import com.sofitech.hoamaimart.customer.adapter.in.web.dto.CreateCustomerRequest;
import com.sofitech.hoamaimart.customer.adapter.in.web.dto.CreateCustomerResponse;
import com.sofitech.hoamaimart.customer.adapter.in.web.dto.ErrorResponse;
import com.sofitech.hoamaimart.customer.domain.exception.PhoneAlreadyExistsException;
import com.sofitech.hoamaimart.customer.domain.model.Customer;
import com.sofitech.hoamaimart.customer.domain.port.in.CustomerCommandService;
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
 * REST Controller - Adapter IN: nhận request từ client qua API Gateway.
 */
@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customers", description = "Customer management endpoints")
public class CustomerController {

    private final CustomerCommandService customerCommandService;

    public CustomerController(CustomerCommandService customerCommandService) {
        this.customerCommandService = customerCommandService;
    }

    /**
     * POST /api/customers - Tạo khách hàng mới.
     */
    @Operation(summary = "Create a new customer", description = "Creates a new customer with phone and name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer created successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Phone number already exists",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<?> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        try {
            Customer customer = customerCommandService.createCustomer(request.phone(), request.name());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CreateCustomerResponse.from(customer));
        } catch (IllegalArgumentException e) {
            // Validation error từ Value Object
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("VALIDATION_ERROR", e.getMessage(), 400));
        }
    }

    /**
     * Exception handler: PhoneAlreadyExistsException → 409 Conflict.
     */
    @ExceptionHandler(PhoneAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handlePhoneExists(PhoneAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("PHONE_EXISTS", ex.getMessage(), 409));
    }
}
