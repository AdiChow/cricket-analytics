package com.adi.cricket.cricket_analytics.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Schema(description = "Grounded player comparison or player-match candidates")
public record AiPlayerComparisonResponse(
        String question,
        AiMatchedPlayersDto matchedPlayers,
        PlayerComparisonDto stats,
        String summary,
        List<String> limitations,
        List<AiPlayerMatchCandidatesDto> candidates
) {
}
