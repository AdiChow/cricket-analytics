package com.adi.cricket.cricket_analytics.repository;

import com.adi.cricket.cricket_analytics.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team,Long> {
    Optional<Team> findByName(String name);
}
