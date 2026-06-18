package com.adi.cricket.cricket_analytics.service;

import com.adi.cricket.cricket_analytics.ai.AiComparisonProvider;
import com.adi.cricket.cricket_analytics.dto.AiPlayerComparisonResponse;
import com.adi.cricket.cricket_analytics.dto.PlayerComparisonDto;
import com.adi.cricket.cricket_analytics.dto.PlayerProfileDto;
import com.adi.cricket.cricket_analytics.dto.PlayerSearchDto;
import com.adi.cricket.cricket_analytics.exception.AiProviderException;
import com.adi.cricket.cricket_analytics.exception.InvalidAiComparisonException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AiPlayerComparisonServiceTest {

    private StubAnalyticsService analyticsService;
    private StubAiComparisonProvider aiComparisonProvider;
    private AiPlayerComparisonService service;

    @BeforeEach
    void setUp() {
        analyticsService = new StubAnalyticsService();
        aiComparisonProvider = new StubAiComparisonProvider();
        service = new AiPlayerComparisonService(
                analyticsService,
                new PlayerNameExtractor(),
                aiComparisonProvider
        );
    }

    @Test
    void returnsCandidatesWhenPlayerMatchIsAmbiguous() {
        analyticsService.addSearchResults(
                "Smith",
                List.of(
                        new PlayerSearchDto(49L, "SPD Smith"),
                        new PlayerSearchDto(71L, "JA Smith")
                )
        );
        analyticsService.addSearchResults(
                "Kohli",
                List.of(new PlayerSearchDto(53L, "V Kohli"))
        );

        AiPlayerComparisonResponse response = service.comparePlayers(
                "Compare Smith and Kohli"
        );

        assertThat(response.matchedPlayers()).isNull();
        assertThat(response.stats()).isNull();
        assertThat(response.candidates()).singleElement().satisfies(match -> {
            assertThat(match.query()).isEqualTo("Smith");
            assertThat(match.candidates()).extracting(PlayerSearchDto::playerName)
                    .containsExactly("SPD Smith", "JA Smith");
        });
        assertThat(analyticsService.comparisonCalls).isZero();
        assertThat(aiComparisonProvider.calls).isZero();
    }

    @Test
    void rejectsComparisonWhenBothNamesResolveToSamePlayer() {
        PlayerSearchDto kohli = new PlayerSearchDto(53L, "V Kohli");
        analyticsService.addSearchResults("Kohli", List.of(kohli));
        analyticsService.addSearchResults("V Kohli", List.of(kohli));

        assertThatThrownBy(() ->
                service.comparePlayers("Compare Kohli and V Kohli")
        )
                .isInstanceOf(InvalidAiComparisonException.class)
                .hasMessage("Question must refer to two different players");

        assertThat(aiComparisonProvider.calls).isZero();
    }

    @Test
    void rejectsComparisonWhenAPlayerCannotBeFound() {
        analyticsService.addSearchResults("Unknown", List.of());
        analyticsService.addSearchResults(
                "Smith",
                List.of(new PlayerSearchDto(49L, "SPD Smith"))
        );

        assertThatThrownBy(() ->
                service.comparePlayers("Compare Unknown and Smith")
        )
                .isInstanceOf(InvalidAiComparisonException.class)
                .hasMessage("Could not find a player matching 'Unknown'");
    }

    @Test
    void returnsDeterministicSummaryWhenAiProviderFails() {
        PlayerComparisonDto stats = comparisonStats();
        stubUniquePlayersAndStats(stats);
        aiComparisonProvider.failure =
                new AiProviderException("provider timeout");

        AiPlayerComparisonResponse response = service.comparePlayers(
                "Compare Kohli and Smith"
        );

        assertThat(response.stats()).isEqualTo(stats);
        assertThat(response.summary())
                .contains("V Kohli has 9230 runs from 121 matches")
                .contains("SPD Smith has the higher run total")
                .contains("V Kohli has the higher strike rate");
        assertThat(response.limitations())
                .containsExactly(
                        "ballsFaced currently includes wides due to known schema limitation"
                );
        assertThat(aiComparisonProvider.calls).isEqualTo(1);
    }

    @Test
    void returnsGroundedAiSummaryOnSuccess() {
        PlayerComparisonDto stats = comparisonStats();
        stubUniquePlayersAndStats(stats);
        aiComparisonProvider.summary =
                "Smith has more runs, while Kohli has the higher strike rate.";

        AiPlayerComparisonResponse response = service.comparePlayers(
                "Compare Kohli and Smith"
        );

        assertThat(response.matchedPlayers().player1().playerId()).isEqualTo(53L);
        assertThat(response.matchedPlayers().player2().playerId()).isEqualTo(49L);
        assertThat(response.stats()).isEqualTo(stats);
        assertThat(response.summary())
                .isEqualTo("Smith has more runs, while Kohli has the higher strike rate.");
        assertThat(aiComparisonProvider.question)
                .isEqualTo("Compare Kohli and Smith");
        assertThat(aiComparisonProvider.stats).isEqualTo(stats);
    }

    private void stubUniquePlayersAndStats(PlayerComparisonDto stats) {
        analyticsService.addSearchResults(
                "Kohli",
                List.of(new PlayerSearchDto(53L, "V Kohli"))
        );
        analyticsService.addSearchResults(
                "Smith",
                List.of(new PlayerSearchDto(49L, "SPD Smith"))
        );
        analyticsService.comparison = stats;
    }

    private PlayerComparisonDto comparisonStats() {
        return new PlayerComparisonDto(
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
    }

    private static class StubAnalyticsService extends AnalyticsService {

        private final Map<String, List<PlayerSearchDto>> searchResults =
                new HashMap<>();
        private PlayerComparisonDto comparison;
        private int comparisonCalls;

        StubAnalyticsService() {
            super(null, null);
        }

        void addSearchResults(
                String query,
                List<PlayerSearchDto> results
        ) {
            searchResults.put(query, results);
        }

        @Override
        public List<PlayerSearchDto> searchPlayers(String searchTerm) {
            return searchResults.getOrDefault(searchTerm, List.of());
        }

        @Override
        public PlayerComparisonDto comparePlayers(
                Long player1Id,
                Long player2Id
        ) {
            comparisonCalls++;
            return comparison;
        }
    }

    private static class StubAiComparisonProvider
            implements AiComparisonProvider {

        private String summary;
        private AiProviderException failure;
        private String question;
        private PlayerComparisonDto stats;
        private int calls;

        @Override
        public String generateComparisonSummary(
                String question,
                PlayerComparisonDto stats
        ) {
            calls++;
            this.question = question;
            this.stats = stats;

            if (failure != null) {
                throw failure;
            }

            return summary;
        }
    }
}
