package com.adi.cricket.cricket_analytics.repository;

import com.adi.cricket.cricket_analytics.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match,Long> {
    Optional<Match> findByCricsheetMatchId(String cricsheetMatchId);
}
