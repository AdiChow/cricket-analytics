package com.adi.cricket.cricket_analytics.exception;

public class PlayerNotFoundException extends RuntimeException {

    public PlayerNotFoundException(Long playerId) {
        super("Player not found with id " + playerId);
    }
}
