package com.adi.cricket.cricket_analytics.dto;

public record PlayerProfileDto(
        Long playerId,
        String playerName,
        Long matches,
        Long runs,
        Long ballsFaced,
        Double strikeRate
) {
}