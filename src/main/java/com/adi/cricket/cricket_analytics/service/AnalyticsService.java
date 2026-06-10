package com.adi.cricket.cricket_analytics.service;

import com.adi.cricket.cricket_analytics.dto.BattingLeaderDto;
import com.adi.cricket.cricket_analytics.dto.PlayerComparisonDto;
import com.adi.cricket.cricket_analytics.dto.PlayerProfileDto;
import com.adi.cricket.cricket_analytics.dto.PlayerProfileProjection;
import com.adi.cricket.cricket_analytics.dto.PlayerSearchDto;
import com.adi.cricket.cricket_analytics.exception.InvalidPlayerComparisonException;
import com.adi.cricket.cricket_analytics.exception.PlayerNotFoundException;
import com.adi.cricket.cricket_analytics.repository.DeliveryRepository;
import com.adi.cricket.cricket_analytics.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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

            long runs =
                    row.getRuns();

            long balls =
                    row.getBallsFaced();

            double strikeRate =
                    balls == 0
                            ? 0
                            : (runs * 100.0) / balls;

            strikeRate =
                    Math.round(
                            strikeRate * 100
                    ) / 100.0;

            return new BattingLeaderDto(
                    row.getPlayerId(),
                    row.getPlayerName(),
                    runs,
                    balls,
                    strikeRate
            );
        }).toList();
    }
    public PlayerProfileDto getPlayerProfile(
            Long playerId
    ) {

        PlayerProfileProjection row =
                deliveryRepository
                        .getPlayerProfile(
                                playerId
                        )
                        .orElseThrow(() ->
                                new PlayerNotFoundException(playerId)
                        );

        long matches =
                row.getMatches();

        long runs =
                row.getRuns();

        long balls =
                row.getBallsFaced();

        double strikeRate =
                balls == 0
                        ? 0
                        : (runs * 100.0) / balls;

        strikeRate =
                Math.round(
                        strikeRate * 100
                ) / 100.0;

        return new PlayerProfileDto(
                row.getPlayerId(),
                row.getPlayerName(),
                matches,
                runs,
                balls,
                strikeRate
        );
    }
    public PlayerComparisonDto comparePlayers(
            Long player1Id,
            Long player2Id
    ) {

        if (Objects.equals(player1Id, player2Id)) {
            throw new InvalidPlayerComparisonException();
        }

        return new PlayerComparisonDto(
                getPlayerProfile(player1Id),
                getPlayerProfile(player2Id)
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
                                row.getPlayerId(),
                                row.getPlayerName()
                        )
                )
                .toList();
    }
}
