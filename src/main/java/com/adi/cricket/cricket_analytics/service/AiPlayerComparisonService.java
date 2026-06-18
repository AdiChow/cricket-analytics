package com.adi.cricket.cricket_analytics.service;

import com.adi.cricket.cricket_analytics.ai.AiComparisonProvider;
import com.adi.cricket.cricket_analytics.dto.AiMatchedPlayersDto;
import com.adi.cricket.cricket_analytics.dto.AiPlayerComparisonResponse;
import com.adi.cricket.cricket_analytics.dto.AiPlayerMatchCandidatesDto;
import com.adi.cricket.cricket_analytics.dto.PlayerComparisonDto;
import com.adi.cricket.cricket_analytics.dto.PlayerProfileDto;
import com.adi.cricket.cricket_analytics.dto.PlayerSearchDto;
import com.adi.cricket.cricket_analytics.exception.AiProviderException;
import com.adi.cricket.cricket_analytics.exception.InvalidAiComparisonException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiPlayerComparisonService {

    private static final List<String> LIMITATIONS = List.of(
            "ballsFaced currently includes wides due to known schema limitation"
    );

    private final AnalyticsService analyticsService;
    private final PlayerNameExtractor playerNameExtractor;
    private final AiComparisonProvider aiComparisonProvider;

    public AiPlayerComparisonResponse comparePlayers(String question) {
        List<String> playerNames =
                playerNameExtractor.extractPlayerNames(question);

        String player1Query = playerNames.get(0);
        String player2Query = playerNames.get(1);
        List<PlayerSearchDto> player1Candidates =
                analyticsService.searchPlayers(player1Query);
        List<PlayerSearchDto> player2Candidates =
                analyticsService.searchPlayers(player2Query);

        requireCandidates(player1Query, player1Candidates);
        requireCandidates(player2Query, player2Candidates);

        Optional<PlayerSearchDto> player1 =
                selectUniqueMatch(player1Query, player1Candidates);
        Optional<PlayerSearchDto> player2 =
                selectUniqueMatch(player2Query, player2Candidates);

        List<AiPlayerMatchCandidatesDto> ambiguities = new ArrayList<>();
        if (player1.isEmpty()) {
            ambiguities.add(new AiPlayerMatchCandidatesDto(
                    player1Query,
                    player1Candidates
            ));
        }
        if (player2.isEmpty()) {
            ambiguities.add(new AiPlayerMatchCandidatesDto(
                    player2Query,
                    player2Candidates
            ));
        }

        if (!ambiguities.isEmpty()) {
            return new AiPlayerComparisonResponse(
                    question,
                    null,
                    null,
                    "Player matching is ambiguous. Use more specific player names.",
                    List.of(),
                    ambiguities
            );
        }

        PlayerSearchDto matchedPlayer1 = player1.orElseThrow();
        PlayerSearchDto matchedPlayer2 = player2.orElseThrow();
        if (matchedPlayer1.playerId().equals(matchedPlayer2.playerId())) {
            throw new InvalidAiComparisonException(
                    "Question must refer to two different players"
            );
        }

        PlayerComparisonDto stats = analyticsService.comparePlayers(
                matchedPlayer1.playerId(),
                matchedPlayer2.playerId()
        );
        String summary = generateSummary(question, stats);

        return new AiPlayerComparisonResponse(
                question,
                new AiMatchedPlayersDto(
                        new PlayerSearchDto(
                                stats.player1().playerId(),
                                stats.player1().playerName()
                        ),
                        new PlayerSearchDto(
                                stats.player2().playerId(),
                                stats.player2().playerName()
                        )
                ),
                stats,
                summary,
                LIMITATIONS,
                List.of()
        );
    }

    private Optional<PlayerSearchDto> selectUniqueMatch(
            String query,
            List<PlayerSearchDto> candidates
    ) {
        List<PlayerSearchDto> exactMatches = candidates
                .stream()
                .filter(candidate ->
                        candidate.playerName().equalsIgnoreCase(query)
                )
                .toList();

        if (exactMatches.size() == 1) {
            return Optional.of(exactMatches.get(0));
        }
        if (candidates.size() == 1) {
            return Optional.of(candidates.get(0));
        }

        return Optional.empty();
    }

    private void requireCandidates(
            String query,
            List<PlayerSearchDto> candidates
    ) {
        if (candidates.isEmpty()) {
            throw new InvalidAiComparisonException(
                    "Could not find a player matching '" + query + "'"
            );
        }
    }

    private String generateSummary(
            String question,
            PlayerComparisonDto stats
    ) {
        try {
            String summary = aiComparisonProvider.generateComparisonSummary(
                    question,
                    stats
            );
            if (summary != null && !summary.isBlank()) {
                return summary.trim();
            }
        } catch (AiProviderException exception) {
            log.warn(
                    "AI comparison provider unavailable; using fallback summary: {}",
                    exception.getMessage()
            );
        }

        return buildFallbackSummary(stats);
    }

    private String buildFallbackSummary(PlayerComparisonDto stats) {
        PlayerProfileDto player1 = stats.player1();
        PlayerProfileDto player2 = stats.player2();

        return String.format(
                Locale.ROOT,
                "%s has %d runs from %d matches, with %d balls faced and a strike rate of %.2f. ",
                player1.playerName(),
                player1.runs(),
                player1.matches(),
                player1.ballsFaced(),
                player1.strikeRate()
        )
                + String.format(
                        Locale.ROOT,
                        "%s has %d runs from %d matches, with %d balls faced and a strike rate of %.2f. ",
                        player2.playerName(),
                        player2.runs(),
                        player2.matches(),
                        player2.ballsFaced(),
                        player2.strikeRate()
                )
                + compareRuns(player1, player2)
                + " "
                + compareStrikeRates(player1, player2);
    }

    private String compareRuns(
            PlayerProfileDto player1,
            PlayerProfileDto player2
    ) {
        int comparison = player1.runs().compareTo(player2.runs());
        if (comparison == 0) {
            return "Both players have the same run total.";
        }

        return "%s has the higher run total."
                .formatted(
                        comparison > 0
                                ? player1.playerName()
                                : player2.playerName()
                );
    }

    private String compareStrikeRates(
            PlayerProfileDto player1,
            PlayerProfileDto player2
    ) {
        int comparison = player1.strikeRate().compareTo(player2.strikeRate());
        if (comparison == 0) {
            return "Both players have the same strike rate.";
        }

        return "%s has the higher strike rate."
                .formatted(
                        comparison > 0
                                ? player1.playerName()
                                : player2.playerName()
                );
    }
}
