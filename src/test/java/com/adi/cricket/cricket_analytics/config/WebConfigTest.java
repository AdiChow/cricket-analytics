package com.adi.cricket.cricket_analytics.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class WebConfigTest {

    @Test
    void allowsConfiguredFrontendOriginForAnalyticsReads() {
        WebConfig webConfig = new WebConfig(
                "http://localhost:5173",
                ""
        );
        TestCorsRegistry registry = new TestCorsRegistry();

        webConfig.addCorsMappings(registry);

        CorsConfiguration configuration = registry.configurations().get("/api/**");
        assertThat(configuration).isNotNull();
        assertThat(configuration.getAllowedOrigins())
                .containsExactly("http://localhost:5173");
        assertThat(configuration.getAllowedMethods())
                .containsExactly("GET");
    }

    @Test
    void addsConfiguredDeployedOriginsWithoutRemovingLocalhost() {
        WebConfig webConfig = new WebConfig(
                "http://localhost:5173",
                "https://cricket.example.com, https://preview.example.com"
        );
        TestCorsRegistry registry = new TestCorsRegistry();

        webConfig.addCorsMappings(registry);

        CorsConfiguration configuration = registry.configurations().get("/api/**");
        assertThat(configuration.getAllowedOrigins())
                .containsExactly(
                        "http://localhost:5173",
                        "https://cricket.example.com",
                        "https://preview.example.com"
                );
    }

    @Test
    void rejectsWildcardOrigins() {
        assertThatThrownBy(() -> new WebConfig(
                "http://localhost:5173",
                "*"
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Wildcard CORS origins are not allowed");
    }

    private static class TestCorsRegistry extends CorsRegistry {

        Map<String, CorsConfiguration> configurations() {
            return getCorsConfigurations();
        }
    }
}
