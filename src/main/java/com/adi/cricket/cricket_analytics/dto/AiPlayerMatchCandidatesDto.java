package com.adi.cricket.cricket_analytics.dto;

import java.util.List;

public record AiPlayerMatchCandidatesDto(
        String query,
        List<PlayerSearchDto> candidates
) {
}
