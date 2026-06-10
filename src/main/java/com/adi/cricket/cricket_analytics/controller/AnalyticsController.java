package com.adi.cricket.cricket_analytics.controller;

import com.adi.cricket.cricket_analytics.dto.BattingLeaderDto;
import com.adi.cricket.cricket_analytics.dto.PlayerProfileDto;
import com.adi.cricket.cricket_analytics.dto.PlayerSearchDto;
import com.adi.cricket.cricket_analytics.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Cricket player and batting analytics endpoints")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/top-batters")
    @Operation(
            summary = "Get top batters",
            description = "Returns the top 20 batters ranked by total runs."
    )
    @ApiResponse(responseCode = "200", description = "Batting leaderboard returned")
    public List<BattingLeaderDto> getTopBatters() {

        return analyticsService.getTopBatters();
    }
    @GetMapping("/players/{playerId}")
    @Operation(
            summary = "Get player profile",
            description = "Returns the batting profile for a player."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Player profile returned"),
            @ApiResponse(responseCode = "400", description = "Invalid player ID"),
            @ApiResponse(responseCode = "404", description = "Player not found"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    public PlayerProfileDto getPlayerProfile(
            @Parameter(description = "Player database ID", example = "42")
            @PathVariable Long playerId
    ) {

        return analyticsService
                .getPlayerProfile(
                        playerId
                );
    }
    @GetMapping("/players/search")
    @Operation(
            summary = "Search players",
            description = "Searches players by a case-insensitive partial name match."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Matching players returned"),
            @ApiResponse(responseCode = "400", description = "Search query is missing or invalid"),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    public List<PlayerSearchDto> searchPlayers(
            @Parameter(description = "Partial player name", example = "kohli")
            @RequestParam String q
    ) {

        return analyticsService
                .searchPlayers(q);
    }
}
