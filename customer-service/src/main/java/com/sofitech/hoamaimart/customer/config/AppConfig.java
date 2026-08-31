package com.sofitech.hoamaimart.customer.config;

import com.sofitech.hoamaimart.customer.adapter.out.persistence.mapper.CustomerMapper;
import com.sofitech.hoamaimart.customer.adapter.out.persistence.repository.CustomerJpaRepository;
import com.sofitech.hoamaimart.customer.adapter.out.persistence.repository.CustomerRepositoryAdapter;
import com.sofitech.hoamaimart.customer.application.service.CreateCustomerService;
import com.sofitech.hoamaimart.customer.application.service.GetCustomerService;
import com.sofitech.hoamaimart.customer.domain.port.in.CustomerCommandService;
import com.sofitech.hoamaimart.customer.domain.port.in.CustomerQueryService;
import com.sofitech.hoamaimart.customer.domain.port.out.CustomerRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration: wire các bean theo Hexagonal architecture.
 */
@Configuration
public class AppConfig {

    @Bean
    public CustomerRepository customerRepository(
            CustomerJpaRepository jpaRepository,
            CustomerMapper mapper
    ) {
        return new CustomerRepositoryAdapter(jpaRepository, mapper);
    }

    @Bean
    public CustomerCommandService customerCommandService(CustomerRepository repository) {
        return new CreateCustomerService(repository);
    }

    @Bean
    public CustomerQueryService customerQueryService(CustomerRepository repository) {
        return new GetCustomerService(repository);
    }
}