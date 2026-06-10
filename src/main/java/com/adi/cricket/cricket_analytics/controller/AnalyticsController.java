package com.adi.cricket.cricket_analytics.controller;

import com.adi.cricket.cricket_analytics.dto.BattingLeaderDto;
import com.adi.cricket.cricket_analytics.dto.PlayerProfileDto;
import com.adi.cricket.cricket_analytics.dto.PlayerSearchDto;
import com.adi.cricket.cricket_analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/top-batters")
    public List<BattingLeaderDto> getTopBatters() {

        return analyticsService.getTopBatters();
    }
    @GetMapping("/players/{playerId}")
    public PlayerProfileDto getPlayerProfile(
            @PathVariable Long playerId
    ) {

        return analyticsService
                .getPlayerProfile(
                        playerId
                );
    }
    @GetMapping("/players/search")
    public List<PlayerSearchDto> searchPlayers(
            @RequestParam String q
    ) {

        return analyticsService
                .searchPlayers(q);
    }
}
