package com.adi.cricket.cricket_analytics.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Cricket Intelligence Platform API",
                description = "REST API for player search, player profiles, and batting analytics.",
                version = "v1"
        )
)
public class OpenApiConfig {
}
