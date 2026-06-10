package com.adi.cricket.cricket_analytics.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.stream.Stream;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String[] allowedOrigins;

    public WebConfig(
            @Value("${app.cors.allowed-origins:http://localhost:5173}") String localOrigins,
            @Value("${app.cors.deployed-origins:}") String deployedOrigins
    ) {
        this.allowedOrigins = Stream.concat(
                        splitOrigins(localOrigins),
                        splitOrigins(deployedOrigins)
                )
                .distinct()
                .toArray(String[]::new);

        if (Arrays.asList(this.allowedOrigins).contains("*")) {
            throw new IllegalArgumentException("Wildcard CORS origins are not allowed");
        }
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET")
                .allowedHeaders("*")
                .maxAge(3600);
    }

    private Stream<String> splitOrigins(String origins) {
        return Arrays.stream(origins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank());
    }
}
