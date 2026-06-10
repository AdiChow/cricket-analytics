package com.adi.cricket.cricket_analytics.repository;

import com.adi.cricket.cricket_analytics.entity.MatchPlayer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatchPlayerRepository
        extends JpaRepository<MatchPlayer, Long> {
    Optional<MatchPlayer>
    findByMatchIdAndPlayerId(
            Long matchId,
            Long playerId
    );
}
