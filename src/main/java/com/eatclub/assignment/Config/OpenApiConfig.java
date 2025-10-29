package com.eatclub.assignment.Config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI orderServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Order Service API Documentation")
                        .description("API documentation for Order Management Service (Redis Stream + REST)")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("EatClub Backend Team")
                                .email("mudit12131@gmail.com"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("Eatclub Order Management System Assignment Documentation")
                        .url("https://github.com/mu-droid/Eatclub_OMS"));
    }
}