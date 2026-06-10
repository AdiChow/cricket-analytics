package com.adi.cricket.cricket_analytics.dto;

public record BattingLeaderDto(
        Long playerId,
        String playerName,
        Long runs,
        Long ballsFaced,
        Double strikeRate
) {}