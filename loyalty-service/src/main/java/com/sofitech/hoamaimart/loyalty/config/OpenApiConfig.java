package com.sofitech.hoamaimart.loyalty.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Loyalty Service API")
                        .version("1.0.0")
                        .description("API for managing loyalty points in Hoa Mai Mart CRM")
                        .contact(new Contact()
                                .name("SoFiTech")
                                .email("support@sofitech.vn")))
                .servers(List.of(
                        new Server().url("http://localhost:8082").description("Local Development")
                ));
    }
}
