package com.adi.cricket.cricket_analytics.repository;

import com.adi.cricket.cricket_analytics.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DeliveryRepository
        extends JpaRepository<Delivery, Long> {

    boolean existsByMatchId(Long matchId);

    @Query(value = """
                            SELECT
                             p.id,
                            p.name,
                              SUM(d.batter_runs) AS runs,
                              COUNT(*) AS balls_faced
                            FROM delivery d
                            JOIN player p
                                ON p.id = d.batter_id
                            GROUP BY p.id, p.name
                            ORDER BY runs DESC
                            LIMIT 20
            """, nativeQuery = true)
    List<Object[]> getTopBatters();

    @Query(value = """
            SELECT
                                     p.id,
                                     p.name,
                                     COUNT(DISTINCT d.match_id) AS matches,
                                     SUM(d.batter_runs) AS runs,
                                     COUNT(*) AS balls_faced
                                 FROM player p
                                 JOIN delivery d
                                     ON p.id = d.batter_id
                                 WHERE p.id = :playerId
                                 GROUP BY p.id, p.name
            """, nativeQuery = true)
    List<Object[]> getPlayerProfile(
            @Param("playerId")
            Long playerId
    );
}
