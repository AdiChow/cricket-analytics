package com.adi.cricket.cricket_analytics.controller;

import com.adi.cricket.cricket_analytics.dto.AiMatchedPlayersDto;
import com.adi.cricket.cricket_analytics.dto.AiPlayerComparisonResponse;
import com.adi.cricket.cricket_analytics.dto.PlayerComparisonDto;
import com.adi.cricket.cricket_analytics.dto.PlayerProfileDto;
import com.adi.cricket.cricket_analytics.dto.PlayerSearchDto;
import com.adi.cricket.cricket_analytics.exception.GlobalExceptionHandler;
import com.adi.cricket.cricket_analytics.exception.InvalidAiComparisonException;
import com.adi.cricket.cricket_analytics.service.AiPlayerComparisonService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AiComparisonControllerTest {

    @Test
    void returnsAiPlayerComparison() throws Exception {
        MockMvc mockMvc = createMockMvc(
                new StubAiPlayerComparisonService(successResponse(), null)
        );

        mockMvc.perform(post("/api/ai/compare-players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"question":"Compare Kohli and Smith"}
                                """))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.question")
                        .value("Compare Kohli and Smith"))
                .andExpect(jsonPath("$.matchedPlayers.player1.playerId")
                        .value(53))
                .andExpect(jsonPath("$.matchedPlayers.player2.playerId")
                        .value(49))
                .andExpect(jsonPath("$.stats.player1.runs")
                        .value(9230))
                .andExpect(jsonPath("$.stats.player2.runs")
                        .value(10763))
                .andExpect(jsonPath("$.summary")
                        .value("Grounded comparison summary"))
                .andExpect(jsonPath("$.limitations[0]").exists());
    }

    @Test
    void returnsBadRequestForInvalidQuestion() throws Exception {
        InvalidAiComparisonException failure =
                new InvalidAiComparisonException(
                        "Question must name two players using 'and', 'vs', or 'versus'"
                );
        MockMvc mockMvc = createMockMvc(
                new StubAiPlayerComparisonService(null, failure)
        );

        mockMvc.perform(post("/api/ai/compare-players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"question":"Compare Kohli"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value(
                        "Question must name two players using 'and', 'vs', or 'versus'"
                ))
                .andExpect(jsonPath("$.path")
                        .value("/api/ai/compare-players"));
    }

    private MockMvc createMockMvc(AiPlayerComparisonService service) {
        return MockMvcBuilders
                .standaloneSetup(new AiComparisonController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private AiPlayerComparisonResponse successResponse() {
        PlayerComparisonDto stats = new PlayerComparisonDto(
                new PlayerProfileDto(
                        53L,
                        "V Kohli",
                        121L,
                        9230L,
                        16655L,
                        55.42
                ),
                new PlayerProfileDto(
                        49L,
                        "SPD Smith",
                        118L,
                        10763L,
                        20031L,
                        53.73
                )
        );

        return new AiPlayerComparisonResponse(
                "Compare Kohli and Smith",
                new AiMatchedPlayersDto(
                        new PlayerSearchDto(53L, "V Kohli"),
                        new PlayerSearchDto(49L, "SPD Smith")
                ),
                stats,
                "Grounded comparison summary",
                List.of(
                        "ballsFaced currently includes wides due to known schema limitation"
                ),
                List.of()
        );
    }

    private static class StubAiPlayerComparisonService
            extends AiPlayerComparisonService {

        private final AiPlayerComparisonResponse response;
        private final RuntimeException failure;

        StubAiPlayerComparisonService(
                AiPlayerComparisonResponse response,
                RuntimeException failure
        ) {
            super(null, null, null);
            this.response = response;
            this.failure = failure;
        }

        @Override
        public AiPlayerComparisonResponse comparePlayers(String question) {
            if (failure != null) {
                throw failure;
            }
            return response;
        }
    }
}
