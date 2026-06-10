package com.adi.cricket.cricket_analytics.controller;

import com.adi.cricket.cricket_analytics.dto.BattingLeaderProjection;
import com.adi.cricket.cricket_analytics.dto.PlayerProfileProjection;
import com.adi.cricket.cricket_analytics.dto.PlayerSearchProjection;
import com.adi.cricket.cricket_analytics.exception.GlobalExceptionHandler;
import com.adi.cricket.cricket_analytics.repository.DeliveryRepository;
import com.adi.cricket.cricket_analytics.repository.PlayerRepository;
import com.adi.cricket.cricket_analytics.service.AnalyticsService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AnalyticsControllerTest {

    @Test
    void returnsPlayerProfile() throws Exception {
        MockMvc mockMvc = createMockMvc(
                Optional.of(playerProfile(
                        42L,
                        "Virat Kohli",
                        10L,
                        500L,
                        400L
                )),
                List.of()
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
    void returnsPlayerComparison() throws Exception {
        MockMvc mockMvc = createMockMvc(Map.of(
                53L, Optional.of(playerProfile(
                        53L,
                        "V Kohli",
                        121L,
                        9230L,
                        16655L
                )),
                49L, Optional.of(playerProfile(
                        49L,
                        "SPD Smith",
                        118L,
                        10763L,
                        20031L
                ))
        ));

        mockMvc.perform(get("/api/stats/players/compare")
                        .param("player1Id", "53")
                        .param("player2Id", "49"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.player1.playerId").value(53))
                .andExpect(jsonPath("$.player1.playerName").value("V Kohli"))
                .andExpect(jsonPath("$.player1.matches").value(121))
                .andExpect(jsonPath("$.player1.runs").value(9230))
                .andExpect(jsonPath("$.player1.ballsFaced").value(16655))
                .andExpect(jsonPath("$.player1.strikeRate").value(55.42))
                .andExpect(jsonPath("$.player2.playerId").value(49))
                .andExpect(jsonPath("$.player2.playerName").value("SPD Smith"))
                .andExpect(jsonPath("$.player2.matches").value(118))
                .andExpect(jsonPath("$.player2.runs").value(10763))
                .andExpect(jsonPath("$.player2.ballsFaced").value(20031))
                .andExpect(jsonPath("$.player2.strikeRate").value(53.73));
    }

    @Test
    void returnsBadRequestWhenFirstComparisonIdIsMissing() throws Exception {
        MockMvc mockMvc = createMockMvc(Map.of());

        mockMvc.perform(get("/api/stats/players/compare")
                        .param("player2Id", "49"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/api/stats/players/compare"));
    }

    @Test
    void returnsBadRequestWhenSecondComparisonIdIsMissing() throws Exception {
        MockMvc mockMvc = createMockMvc(Map.of());

        mockMvc.perform(get("/api/stats/players/compare")
                        .param("player1Id", "53"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.path").value("/api/stats/players/compare"));
    }

    @Test
    void returnsBadRequestWhenComparisonIdsAreTheSame() throws Exception {
        MockMvc mockMvc = createMockMvc(Map.of());

        mockMvc.perform(get("/api/stats/players/compare")
                        .param("player1Id", "53")
                        .param("player2Id", "53"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message")
                        .value("player1Id and player2Id must be different"))
                .andExpect(jsonPath("$.path").value("/api/stats/players/compare"));
    }

    @Test
    void returnsNotFoundWhenFirstComparedPlayerDoesNotExist() throws Exception {
        MockMvc mockMvc = createMockMvc(Map.of(
                49L, Optional.of(playerProfile(
                        49L,
                        "SPD Smith",
                        118L,
                        10763L,
                        20031L
                ))
        ));

        mockMvc.perform(get("/api/stats/players/compare")
                        .param("player1Id", "53")
                        .param("player2Id", "49"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").value("Player not found with id 53"))
                .andExpect(jsonPath("$.path").value("/api/stats/players/compare"));
    }

    @Test
    void returnsNotFoundWhenSecondComparedPlayerDoesNotExist() throws Exception {
        MockMvc mockMvc = createMockMvc(Map.of(
                53L, Optional.of(playerProfile(
                        53L,
                        "V Kohli",
                        121L,
                        9230L,
                        16655L
                ))
        ));

        mockMvc.perform(get("/api/stats/players/compare")
                        .param("player1Id", "53")
                        .param("player2Id", "49"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message").value("Player not found with id 49"))
                .andExpect(jsonPath("$.path").value("/api/stats/players/compare"));
    }

    @Test
    void returnsBattingLeaderboard() throws Exception {
        MockMvc mockMvc = createMockMvc(
                Optional.empty(),
                List.of(
                        battingLeader(42L, "Virat Kohli", 500L, 400L),
                        battingLeader(18L, "Smriti Mandhana", 450L, 375L)
                )
        );

        mockMvc.perform(get("/api/stats/top-batters"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].playerId").value(42))
                .andExpect(jsonPath("$[0].playerName").value("Virat Kohli"))
                .andExpect(jsonPath("$[0].runs").value(500))
                .andExpect(jsonPath("$[0].ballsFaced").value(400))
                .andExpect(jsonPath("$[0].strikeRate").value(125.0))
                .andExpect(jsonPath("$[1].playerId").value(18))
                .andExpect(jsonPath("$[1].playerName").value("Smriti Mandhana"))
                .andExpect(jsonPath("$[1].runs").value(450))
                .andExpect(jsonPath("$[1].ballsFaced").value(375))
                .andExpect(jsonPath("$[1].strikeRate").value(120.0));
    }

    @Test
    void returnsPlayerSearchResults() throws Exception {
        MockMvc mockMvc = createMockMvc(
                Optional.empty(),
                List.of(),
                List.of(playerSearchResult(42L, "Virat Kohli"))
        );

        mockMvc.perform(get("/api/stats/players/search").param("q", "virat"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0].playerId").value(42))
                .andExpect(jsonPath("$[0].playerName").value("Virat Kohli"));
    }

    @Test
    void returnsNotFoundWhenPlayerProfileDoesNotExist() throws Exception {
        MockMvc mockMvc = createMockMvc(Optional.empty(), List.of());

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
        MockMvc mockMvc = createMockMvc(Optional.empty(), List.of());

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
        MockMvc mockMvc = createMockMvc(Optional.empty(), List.of());

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

    private MockMvc createMockMvc(
            Optional<PlayerProfileProjection> profileRow,
            List<BattingLeaderProjection> battingLeaders
    ) {
        return createMockMvc(profileRow, battingLeaders, List.of());
    }

    private MockMvc createMockMvc(
            Optional<PlayerProfileProjection> profileRow,
            List<BattingLeaderProjection> battingLeaders,
            List<PlayerSearchProjection> searchResults
    ) {
        DeliveryRepository deliveryRepository = deliveryRepository((methodName, arguments) -> {
            if ("getPlayerProfile".equals(methodName)) {
                return profileRow;
            }
            if ("getTopBatters".equals(methodName)) {
                return battingLeaders;
            }

            throw new UnsupportedOperationException("Unexpected repository method: " + methodName);
        });
        PlayerRepository playerRepository = playerRepository(searchResults);

        return createMockMvc(deliveryRepository, playerRepository);
    }

    private MockMvc createMockMvc(
            Map<Long, Optional<PlayerProfileProjection>> profileRows
    ) {
        DeliveryRepository deliveryRepository = deliveryRepository((methodName, arguments) -> {
            if ("getPlayerProfile".equals(methodName)) {
                return profileRows.getOrDefault(
                        (Long) arguments[0],
                        Optional.empty()
                );
            }

            throw new UnsupportedOperationException("Unexpected repository method: " + methodName);
        });

        return createMockMvc(deliveryRepository, null);
    }

    private PlayerProfileProjection playerProfile(
            Long playerId,
            String playerName,
            Long matches,
            Long runs,
            Long ballsFaced
    ) {
        return new PlayerProfileProjection() {
            @Override
            public Long getPlayerId() {
                return playerId;
            }

            @Override
            public String getPlayerName() {
                return playerName;
            }

            @Override
            public Long getMatches() {
                return matches;
            }

            @Override
            public Long getRuns() {
                return runs;
            }

            @Override
            public Long getBallsFaced() {
                return ballsFaced;
            }
        };
    }

    private BattingLeaderProjection battingLeader(
            Long playerId,
            String playerName,
            Long runs,
            Long ballsFaced
    ) {
        return new BattingLeaderProjection() {
            @Override
            public Long getPlayerId() {
                return playerId;
            }

            @Override
            public String getPlayerName() {
                return playerName;
            }

            @Override
            public Long getRuns() {
                return runs;
            }

            @Override
            public Long getBallsFaced() {
                return ballsFaced;
            }
        };
    }

    private PlayerSearchProjection playerSearchResult(
            Long playerId,
            String playerName
    ) {
        return new PlayerSearchProjection() {
            @Override
            public Long getPlayerId() {
                return playerId;
            }

            @Override
            public String getPlayerName() {
                return playerName;
            }
        };
    }

    private MockMvc createMockMvc(RuntimeException repositoryFailure) {
        DeliveryRepository deliveryRepository = deliveryRepository((methodName, arguments) -> {
            throw repositoryFailure;
        });

        return createMockMvc(deliveryRepository, null);
    }

    private MockMvc createMockMvc(
            DeliveryRepository deliveryRepository,
            PlayerRepository playerRepository
    ) {
        AnalyticsService analyticsService = new AnalyticsService(
                deliveryRepository,
                playerRepository
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
                (proxy, method, arguments) -> methodHandler.invoke(
                        method.getName(),
                        arguments
                )
        );
    }

    private PlayerRepository playerRepository(
            List<PlayerSearchProjection> searchResults
    ) {
        return (PlayerRepository) Proxy.newProxyInstance(
                PlayerRepository.class.getClassLoader(),
                new Class<?>[]{PlayerRepository.class},
                (proxy, method, arguments) -> {
                    if ("searchPlayers".equals(method.getName())) {
                        return searchResults;
                    }

                    throw new UnsupportedOperationException(
                            "Unexpected repository method: " + method.getName()
                    );
                }
        );
    }

    @FunctionalInterface
    private interface RepositoryMethodHandler {

        Object invoke(String methodName, Object[] arguments);
    }
}
