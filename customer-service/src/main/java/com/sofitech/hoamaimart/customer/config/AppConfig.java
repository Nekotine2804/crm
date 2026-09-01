package com.sofitech.hoamaimart.customer.config;

import com.sofitech.hoamaimart.customer.adapter.out.persistence.repository.CustomerJpaRepository;
import com.sofitech.hoamaimart.customer.adapter.out.persistence.repository.CustomerRepositoryAdapter;
import com.sofitech.hoamaimart.customer.application.service.CreateCustomerService;
import com.sofitech.hoamaimart.customer.application.service.GetCustomerService;
import com.sofitech.hoamaimart.customer.domain.port.in.CustomerCommandService;
import com.sofitech.hoamaimart.customer.domain.port.in.CustomerQueryService;
import com.sofitech.hoamaimart.customer.domain.port.out.CustomerRepository;
import com.sofitech.hoamaimart.customer.domain.port.out.EventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public CustomerRepository customerRepository(CustomerJpaRepository jpaRepository) {
        return new CustomerRepositoryAdapter(jpaRepository);
    }

    @Bean
    public CustomerCommandService customerCommandService(CustomerRepository repository, EventPublisher eventPublisher) {
        return new CreateCustomerService(repository, eventPublisher);
    }

    @Bean
    public CustomerQueryService customerQueryService(CustomerRepository repository) {
        return new GetCustomerService(repository);
    }
}
