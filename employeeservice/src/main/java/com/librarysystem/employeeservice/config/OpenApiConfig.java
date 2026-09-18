package com.librarysystem.employeeservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Order API",
                version = "1.0.0",
                description = "REST API for managing customer orders"
        )
)
class OpenApiConfig {
}