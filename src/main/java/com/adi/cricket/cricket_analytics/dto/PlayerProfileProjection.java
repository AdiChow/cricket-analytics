package com.adi.cricket.cricket_analytics.dto;

public interface PlayerProfileProjection {

    Long getPlayerId();

    String getPlayerName();

    Long getMatches();

    Long getRuns();

    Long getBallsFaced();
}
