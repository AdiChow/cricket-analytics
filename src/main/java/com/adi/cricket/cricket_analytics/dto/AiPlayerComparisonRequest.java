package com.adi.cricket.cricket_analytics.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AiPlayerComparisonRequest(
        @Schema(
                description = "Natural-language request naming two players",
                example = "Compare Kohli and Smith"
        )
        String question
) {
}
