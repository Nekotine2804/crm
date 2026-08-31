package com.sofitech.hoamaimart.transaction.adapter.out.client;

import com.sofitech.hoamaimart.shared.error.BusinessErrorCode;
import com.sofitech.hoamaimart.shared.error.BusinessException;
import com.sofitech.hoamaimart.transaction.domain.port.out.CustomerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

/**
 * HTTP Client gọi sang customer-service.
 * Production: nên dùng OpenFeign + Circuit Breaker.
 *
 * POC: gọi trực tiếp. Có thể tạm thời disable bằng property.
 */
@Component
public class HttpCustomerClient implements CustomerClient {

    private static final Logger log = LoggerFactory.getLogger(HttpCustomerClient.class);

    private final RestTemplate restTemplate;
    private final String customerServiceUrl;
    private final boolean enabled;

    public HttpCustomerClient(
            RestTemplate restTemplate,
            @Value("${app.customer-service.url:http://localhost:8081}") String customerServiceUrl,
            @Value("${app.customer-service.validation-enabled:true}") boolean enabled
    ) {
        this.restTemplate = restTemplate;
        this.customerServiceUrl = customerServiceUrl;
        this.enabled = enabled;
    }

    @Override
    public void validateActiveCustomer(UUID customerId) {
        if (!enabled) {
            log.debug("Customer validation disabled - skipping for customerId={}", customerId);
            return;
        }

        String url = customerServiceUrl + "/api/v1/customers/" + customerId + "/status";

        try {
            CustomerStatusResponse response = restTemplate.getForObject(url, CustomerStatusResponse.class);

            if (response == null) {
                throw BusinessException.of(BusinessErrorCode.CUSTOMER_NOT_FOUND,
                        "Customer không tồn tại: " + customerId);
            }

            if (!"ACTIVE".equals(response.status())) {
                throw BusinessException.of(BusinessErrorCode.CUSTOMER_INACTIVE,
                        "Customer không ACTIVE: " + customerId + " (status=" + response.status() + ")");
            }

        } catch (HttpClientErrorException.NotFound e) {
            throw BusinessException.of(BusinessErrorCode.CUSTOMER_NOT_FOUND,
                    "Customer không tồn tại: " + customerId);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to call customer-service for customerId={}", customerId, e);
            throw BusinessException.of(BusinessErrorCode.COMMON_INTERNAL_ERROR,
                    "Không thể xác thực khách hàng: " + e.getMessage());
        }
    }

    /**
     * Inner response DTO - chỉ cần status.
     */
    public record CustomerStatusResponse(String status) {}
}