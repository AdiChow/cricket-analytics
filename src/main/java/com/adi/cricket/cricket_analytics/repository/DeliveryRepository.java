package com.adi.cricket.cricket_analytics.repository;

import com.adi.cricket.cricket_analytics.dto.BattingLeaderProjection;
import com.adi.cricket.cricket_analytics.dto.PlayerProfileProjection;
import com.adi.cricket.cricket_analytics.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DeliveryRepository
        extends JpaRepository<Delivery, Long> {

    boolean existsByMatchId(Long matchId);

    @Query(value = """
                            SELECT
                              p.id AS "playerId",
                              p.name AS "playerName",
                              SUM(d.batter_runs) AS runs,
                              COUNT(*) AS "ballsFaced"
                            FROM delivery d
                            JOIN player p
                                ON p.id = d.batter_id
                            GROUP BY p.id, p.name
                            ORDER BY runs DESC
                            LIMIT 20
            """, nativeQuery = true)
    List<BattingLeaderProjection> getTopBatters();

    @Query(value = """
            SELECT
                                     p.id AS "playerId",
                                     p.name AS "playerName",
                                     COUNT(DISTINCT d.match_id) AS matches,
                                     SUM(d.batter_runs) AS runs,
                                     COUNT(*) AS "ballsFaced"
                                 FROM player p
                                 JOIN delivery d
                                     ON p.id = d.batter_id
                                 WHERE p.id = :playerId
                                 GROUP BY p.id, p.name
            """, nativeQuery = true)
    Optional<PlayerProfileProjection> getPlayerProfile(
            @Param("playerId")
            Long playerId
    );
}
