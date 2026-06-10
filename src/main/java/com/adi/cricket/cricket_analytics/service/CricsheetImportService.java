package com.adi.cricket.cricket_analytics.service;

import com.adi.cricket.cricket_analytics.dto.CricsheetMatch;
import com.adi.cricket.cricket_analytics.dto.DeliveryDto;
import com.adi.cricket.cricket_analytics.dto.InningsDto;
import com.adi.cricket.cricket_analytics.dto.OverDto;
import com.adi.cricket.cricket_analytics.entity.*;
import com.adi.cricket.cricket_analytics.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CricsheetImportService {

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final MatchPlayerRepository matchPlayerRepository;
    private final DeliveryRepository deliveryRepository;

    @Transactional
    public void importMatch(
            CricsheetMatch dto,
            String cricsheetMatchId
    ) {
        Optional<Match> existingMatch =
                matchRepository.findByCricsheetMatchId(
                        cricsheetMatchId
                );
        if(existingMatch.isPresent()) {
            Match match =
                    existingMatch.get();

            if (!deliveryRepository.existsByMatchId(match.getId())) {
                System.out.println(
                        "Resuming incomplete match import"
                );

                importMatchData(
                        dto,
                        match
                );

                return;
            }

            System.out.println(
                    "Match already imported"
            );

            return;
        }
        Match match = new Match();
        List<String> dates = dto.getInfo().getDates();
        match.setVenue(dto.getInfo().getVenue());
        match.setCity(dto.getInfo().getCity());
        match.setMatchType(dto.getInfo().getMatchType());
        match.setStartDate(
                LocalDate.parse(
                        dates.getFirst()
                )
        );
        match.setEndDate(
                LocalDate.parse(
                        dates.getLast()
                )
        );
        match.setCricsheetMatchId(
                cricsheetMatchId
        );
        matchRepository.save(match);

        importMatchData(
                dto,
                match
        );
    }

    private void importMatchData(
            CricsheetMatch dto,
            Match match
    ) {

        Map<String, Team> teamsByName =
                new HashMap<>();

        for (String teamName : dto.getInfo().getTeams()) {

            Team team =
                    teamRepository
                            .findByName(teamName)
                            .orElseGet(() -> {

                                Team newTeam = new Team();
                                newTeam.setName(teamName);

                                return teamRepository.save(newTeam);
                            });

            teamsByName.put(teamName, team);

            System.out.println("Imported Team: " + team.getName());
        }
        Map<String,String> registry =
                dto.getInfo()
                        .getRegistry()
                        .getPeople();
        Map<String, Player> playersByCricsheetId =
                new HashMap<>();

        for (Map.Entry<String,String> entry
                : registry.entrySet()) {

            String playerName = entry.getKey();


            String cricsheetId =
                    entry.getValue();

            Player player =
                    findOrCreatePlayer(
                            playerName,
                            cricsheetId
                    );

            playersByCricsheetId.put(
                    cricsheetId,
                    player
            );

            System.out.println(
                    "Imported Player: "
                            + player.getName()
            );
        }

        Map<String, List<String>> matchPlayers =
                dto.getInfo().getPlayers();

        for (Map.Entry<String, List<String>> entry
                : matchPlayers.entrySet()) {
            String teamName = entry.getKey();

            List<String> playerNames =
                    entry.getValue();
            Team team =
                    teamsByName.get(teamName);

            for(String playerName : playerNames) {
                Player player =
                        findRegisteredPlayer(
                                playerName,
                                registry,
                                playersByCricsheetId
                        );
                Optional<MatchPlayer> existing =
                        matchPlayerRepository
                                .findByMatchIdAndPlayerId(
                                        match.getId(),
                                        player.getId()
                                );
                if(existing.isPresent()) {
                    continue;
                }
                MatchPlayer matchPlayer =
                        new MatchPlayer();
                matchPlayer.setMatchId(
                        match.getId()
                );

                matchPlayer.setPlayerId(
                        player.getId()
                );

                matchPlayer.setTeamId(
                        team.getId()
                );
                matchPlayerRepository.save(
                        matchPlayer
                );
                System.out.println(
                        player.getName()
                                + " linked to "
                                + team.getName()
                );
            }
        }

        Map<String, Long> playerIdMap =
                new HashMap<>();
        for (Map.Entry<String, Player> entry
                : playersByCricsheetId.entrySet()) {
            playerIdMap.put(
                    entry.getKey(),
                    entry.getValue().getId()
            );
        }

        //Innings
        int inningsNumber = 1;
        List<Delivery> deliveries =
                new ArrayList<>();
        for (InningsDto innings : dto.getInnings()) {
            System.out.println(
                    "Processing innings "
                            + inningsNumber
            );
            for (OverDto over : innings.getOvers()) {
                int ballNumber = 1;
                for (DeliveryDto delivery
                        : over.getDeliveries()) {
                    Delivery deliveryEntity =
                            new Delivery();
                    deliveryEntity.setMatchId(
                            match.getId()
                    );

                    deliveryEntity.setInningsNumber(
                            inningsNumber
                    );

                    deliveryEntity.setOverNumber(
                            over.getOver()
                    );

                    deliveryEntity.setBallNumber(
                            ballNumber
                    );
                    String batterCricsheetId =
                            registry.get(
                                    delivery.getBatter()
                            );

                    deliveryEntity.setBatterId(
                            getPlayerId(
                                    delivery.getBatter(),
                                    batterCricsheetId,
                                    playerIdMap
                            )
                    );

                    String bowlerCricsheetId =
                            registry.get(
                                    delivery.getBowler()
                            );

                    deliveryEntity.setBowlerId(
                            getPlayerId(
                                    delivery.getBowler(),
                                    bowlerCricsheetId,
                                    playerIdMap
                            )
                    );

                    String nonStrikerCricsheetId =
                            registry.get(
                                    delivery.getNonStriker()
                            );

                    deliveryEntity.setNonStrikerId(
                            getPlayerId(
                                    delivery.getNonStriker(),
                                    nonStrikerCricsheetId,
                                    playerIdMap
                            )
                    );
                    deliveryEntity.setBatterRuns(
                            delivery.getRuns().getBatter()
                    );

                    deliveryEntity.setExtraRuns(
                            delivery.getRuns().getExtras()
                    );

                    deliveryEntity.setTotalRuns(
                            delivery.getRuns().getTotal()
                    );
                    deliveries.add(
                            deliveryEntity
                    );
                    ballNumber++;
                    System.out.println(
                            delivery.getBatter()
                                    + " | "
                                    + delivery.getBowler()
                                    + " | "
                                    + delivery.getRuns().getTotal()
                    );
                }

            }
            inningsNumber++;
        }
        deliveryRepository.saveAll(
                deliveries
        );
        System.out.println("Match saved!");
    }

    private Player findOrCreatePlayer(
            String playerName,
            String cricsheetId
    ) {
        if (cricsheetId == null || cricsheetId.isBlank()) {
            throw new IllegalArgumentException(
                    "Missing Cricsheet player id for "
                            + playerName
            );
        }

        return playerRepository
                .findByCricsheetId(cricsheetId)
                .map(player -> {
                    if (player.getName() == null
                            || player.getName().isBlank()) {
                        player.setName(playerName);
                    }

                    return player;
                })
                .orElseGet(() -> {
                    Player newPlayer = new Player();
                    newPlayer.setName(playerName);
                    newPlayer.setCricsheetId(cricsheetId);

                    return playerRepository.save(newPlayer);
                });
    }

    private Player findRegisteredPlayer(
            String playerName,
            Map<String, String> registry,
            Map<String, Player> playersByCricsheetId
    ) {
        String cricsheetId =
                registry.get(playerName);
        Player player =
                playersByCricsheetId.get(cricsheetId);

        if (player == null) {
            throw new IllegalStateException(
                    "No registry entry found for player "
                            + playerName
            );
        }

        return player;
    }

    private Long getPlayerId(
            String playerName,
            String cricsheetId,
            Map<String, Long> playerIdMap
    ) {
        Long playerId =
                playerIdMap.get(cricsheetId);

        if (playerId == null) {
            throw new IllegalStateException(
                    "No imported player id found for "
                            + playerName
            );
        }

        return playerId;
    }
}
