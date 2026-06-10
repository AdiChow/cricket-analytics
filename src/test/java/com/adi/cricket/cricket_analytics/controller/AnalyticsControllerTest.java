package com.adi.cricket.cricket_analytics.controller;

import com.adi.cricket.cricket_analytics.exception.GlobalExceptionHandler;
import com.adi.cricket.cricket_analytics.repository.DeliveryRepository;
import com.adi.cricket.cricket_analytics.service.AnalyticsService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AnalyticsControllerTest {

    @Test
    void returnsPlayerProfile() throws Exception {
        MockMvc mockMvc = createMockMvc(
                Collections.singletonList(
                        new Object[]{42L, "Virat Kohli", 10L, 500L, 400L}
                )
        );

        mockMvc.perform(get("/api/stats/players/42"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.playerId").value(42))
                .andExpect(jsonPath("$.playerName").value("Virat Kohli"))
                .andExpect(jsonPath("$.matches").value(10))
                .andExpect(jsonPath("$.runs").value(500))
                .andExpect(jsonPath("$.ballsFaced").value(400))
                .andExpect(jsonPath("$.strikeRate").value(125.0));
    }

    @Test
    void returnsNotFoundWhenPlayerProfileDoesNotExist() throws Exception {
        MockMvc mockMvc = createMockMvc(List.of());

        mockMvc.perform(get("/api/stats/players/42"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Player not found with id 42"))
                .andExpect(jsonPath("$.path").value("/api/stats/players/42"));
    }

    @Test
    void returnsBadRequestForInvalidPlayerId() throws Exception {
        MockMvc mockMvc = createMockMvc(List.of());

        mockMvc.perform(get("/api/stats/players/not-a-number"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value("Invalid value 'not-a-number' for 'playerId'"))
                .andExpect(jsonPath("$.path")
                        .value("/api/stats/players/not-a-number"));
    }

    @Test
    void returnsConsistentBadRequestWhenSearchQueryIsMissing() throws Exception {
        MockMvc mockMvc = createMockMvc(List.of());

        mockMvc.perform(get("/api/stats/players/search"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/api/stats/players/search"));
    }

    @Test
    void returnsInternalServerErrorForUnexpectedFailures() throws Exception {
        MockMvc mockMvc = createMockMvc(
                new IllegalStateException("Database unavailable")
        );

        mockMvc.perform(get("/api/stats/players/42"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"))
                .andExpect(jsonPath("$.path").value("/api/stats/players/42"));
    }

    private MockMvc createMockMvc(List<Object[]> profileRows) {
        DeliveryRepository deliveryRepository = deliveryRepository((methodName) -> {
            if ("getPlayerProfile".equals(methodName)) {
                return profileRows;
            }

            throw new UnsupportedOperationException("Unexpected repository method: " + methodName);
        });

        return createMockMvc(deliveryRepository);
    }

    private MockMvc createMockMvc(RuntimeException repositoryFailure) {
        DeliveryRepository deliveryRepository = deliveryRepository((methodName) -> {
            throw repositoryFailure;
        });

        return createMockMvc(deliveryRepository);
    }

    private MockMvc createMockMvc(DeliveryRepository deliveryRepository) {
        AnalyticsService analyticsService = new AnalyticsService(
                deliveryRepository,
                null
        );
        AnalyticsController controller = new AnalyticsController(analyticsService);

        return MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private DeliveryRepository deliveryRepository(
            RepositoryMethodHandler methodHandler
    ) {
        return (DeliveryRepository) Proxy.newProxyInstance(
                DeliveryRepository.class.getClassLoader(),
                new Class<?>[]{DeliveryRepository.class},
                (proxy, method, arguments) -> methodHandler.invoke(method.getName())
        );
    }

    @FunctionalInterface
    private interface RepositoryMethodHandler {

        Object invoke(String methodName);
    }
}
