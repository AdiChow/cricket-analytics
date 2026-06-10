package com.adi.cricket.cricket_analytics.repository;

import com.adi.cricket.cricket_analytics.dto.PlayerSearchProjection;
import com.adi.cricket.cricket_analytics.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository <Player,Long>{
    Optional<Player> findByCricsheetId(
            String cricsheetId
    );
    Optional<Player> findByName(String name);
    @Query(value = """
        SELECT
            id AS "playerId",
            name AS "playerName"
        FROM player
        WHERE LOWER(name)
              LIKE LOWER(
                    CONCAT('%', :searchTerm, '%')
              )
        ORDER BY name
        LIMIT 20
        """, nativeQuery = true)
    List<PlayerSearchProjection> searchPlayers(
            @Param("searchTerm")
            String searchTerm
    );
}
