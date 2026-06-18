package com.adi.cricket.cricket_analytics.controller;

import com.adi.cricket.cricket_analytics.dto.AiPlayerComparisonRequest;
import com.adi.cricket.cricket_analytics.dto.AiPlayerComparisonResponse;
import com.adi.cricket.cricket_analytics.service.AiPlayerComparisonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI Insights", description = "Grounded AI explanations over verified analytics data")
public class AiComparisonController {

    private final AiPlayerComparisonService aiPlayerComparisonService;

    @PostMapping("/compare-players")
    @Operation(
            summary = "Explain a player comparison",
            description = "Matches two players from a natural-language que    stion, loads verified profile statistics, and returns a grounded AI explanation or deterministic fallback."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Comparison returned, or ambiguous player candidates returned",
                    content = @Content(schema = @Schema(implementation = AiPlayerComparisonResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Question is invalid or fewer than two distinct players were found"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                    schema = @Schema(implementation = AiPlayerComparisonRequest.class),
                    examples = @ExampleObject(value = "{\"question\":\"Compare Kohli and Smith\"}")
            )
    )
    public AiPlayerComparisonResponse comparePlayers(
            @RequestBody AiPlayerComparisonRequest request
    ) {
        return aiPlayerComparisonService.comparePlayers(
                request == null ? null : request.question()
        );
    }
}
