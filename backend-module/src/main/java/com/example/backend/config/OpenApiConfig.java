package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI customOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("API Desafio Fullstack Integrado")
                                                .version("1.0.0")
                                                .description("API para gerenciamento de beneficiários com transferências seguras")
                                                .contact(new Contact()
                                                                .name("Time Development")
                                                                .email("dev@example.com"))
                                                .license(new License()
                                                                .name("MIT")
                                                                .url("https://opensource.org/licenses/MIT")));
        }
}
