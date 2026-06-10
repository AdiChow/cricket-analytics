package com.adi.cricket.cricket_analytics.service;

import com.adi.cricket.cricket_analytics.dto.BattingLeaderDto;
import com.adi.cricket.cricket_analytics.dto.PlayerProfileDto;
import com.adi.cricket.cricket_analytics.dto.PlayerSearchDto;
import com.adi.cricket.cricket_analytics.exception.PlayerNotFoundException;
import com.adi.cricket.cricket_analytics.repository.DeliveryRepository;
import com.adi.cricket.cricket_analytics.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final DeliveryRepository deliveryRepository;
    private final PlayerRepository playerRepository;

    public List<BattingLeaderDto> getTopBatters() {

        return deliveryRepository
                .getTopBatters()
                .stream()
                .map(row -> {

            Long playerId =
                    ((Number) row[0]).longValue();

            String playerName =
                    (String) row[1];

            long runs =
                    ((Number) row[2]).longValue();

            long balls =
                    ((Number) row[3]).longValue();

            double strikeRate =
                    balls == 0
                            ? 0
                            : (runs * 100.0) / balls;

            strikeRate =
                    Math.round(
                            strikeRate * 100
                    ) / 100.0;

            return new BattingLeaderDto(
                    playerId,
                    playerName,
                    runs,
                    balls,
                    strikeRate
            );
        }).toList();
    }
    public PlayerProfileDto getPlayerProfile(
            Long playerId
    ) {

        Object[] row =
                deliveryRepository
                        .getPlayerProfile(
                                playerId
                        )
                        .stream()
                        .findFirst()
                        .orElseThrow(() ->
                                new PlayerNotFoundException(playerId)
                        );

        long matches =
                ((Number) row[2]).longValue();

        long runs =
                ((Number) row[3]).longValue();

        long balls =
                ((Number) row[4]).longValue();

        double strikeRate =
                balls == 0
                        ? 0
                        : (runs * 100.0) / balls;

        strikeRate =
                Math.round(
                        strikeRate * 100
                ) / 100.0;

        return new PlayerProfileDto(
                ((Number) row[0]).longValue(),
                (String) row[1],
                matches,
                runs,
                balls,
                strikeRate
        );
    }
    public List<PlayerSearchDto> searchPlayers(
            String searchTerm
    ) {

        return playerRepository
                .searchPlayers(
                        searchTerm
                )
                .stream()
                .map(row ->
                        new PlayerSearchDto(
                                ((Number) row[0]).longValue(),
                                (String) row[1]
                        )
                )
                .toList();
    }
}
