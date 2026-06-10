package com.adi.cricket.cricket_analytics.exception;

public class InvalidPlayerComparisonException extends RuntimeException {

    public InvalidPlayerComparisonException() {
        super("player1Id and player2Id must be different");
    }
}
