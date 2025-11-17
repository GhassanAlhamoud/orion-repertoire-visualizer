package com.orion.visualizer.v2.service;

import com.orion.visualizer.v2.model.PlayerProfile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for searching and managing historical player profiles.
 */
public class PlayerSearchService {
    private final List<PlayerProfile> players;
    
    public PlayerSearchService() {
        this.players = new ArrayList<>();
        initializeSamplePlayers();
    }
    
    /**
     * Search for players by name (case-insensitive, partial match).
     */
    public List<PlayerProfile> searchPlayers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>(players);
        }
        
        String lowerQuery = query.toLowerCase().trim();
        return players.stream()
            .filter(p -> p.getName().toLowerCase().contains(lowerQuery))
            .collect(Collectors.toList());
    }
    
    /**
     * Get player by exact name.
     */
    public PlayerProfile getPlayerByName(String name) {
        return players.stream()
            .filter(p -> p.getName().equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Get all players.
     */
    public List<PlayerProfile> getAllPlayers() {
        return new ArrayList<>(players);
    }
    
    /**
     * Add a player profile.
     */
    public void addPlayer(PlayerProfile player) {
        if (!players.contains(player)) {
            players.add(player);
        }
    }
    
    /**
     * Initialize with sample historical players.
     */
    private void initializeSamplePlayers() {
        // World Champions and top players
        players.add(new PlayerProfile(
            "Karpov, Anatoly",
            "4100018",
            LocalDate.of(1951, 5, 23),
            "RUS",
            2780,
            LocalDate.of(1966, 1, 1),
            LocalDate.of(2016, 12, 31),
            3500
        ));
        
        players.add(new PlayerProfile(
            "Kasparov, Garry",
            "4100017",
            LocalDate.of(1963, 4, 13),
            "RUS",
            2851,
            LocalDate.of(1973, 1, 1),
            LocalDate.of(2005, 12, 31),
            2800
        ));
        
        players.add(new PlayerProfile(
            "Fischer, Robert James",
            "2000682",
            LocalDate.of(1943, 3, 9),
            "USA",
            2785,
            LocalDate.of(1955, 1, 1),
            LocalDate.of(1992, 12, 31),
            800
        ));
        
        players.add(new PlayerProfile(
            "Carlsen, Magnus",
            "1503014",
            LocalDate.of(1990, 11, 30),
            "NOR",
            2882,
            LocalDate.of(2000, 1, 1),
            LocalDate.now(),
            3000
        ));
        
        players.add(new PlayerProfile(
            "Tal, Mikhail",
            "4100042",
            LocalDate.of(1936, 11, 9),
            "LAT",
            2705,
            LocalDate.of(1953, 1, 1),
            LocalDate.of(1992, 6, 28),
            2500
        ));
        
        players.add(new PlayerProfile(
            "Botvinnik, Mikhail",
            "4100018",
            LocalDate.of(1911, 8, 17),
            "RUS",
            2720,
            LocalDate.of(1924, 1, 1),
            LocalDate.of(1970, 12, 31),
            1500
        ));
        
        players.add(new PlayerProfile(
            "Petrosian, Tigran",
            "4100034",
            LocalDate.of(1929, 6, 17),
            "ARM",
            2645,
            LocalDate.of(1946, 1, 1),
            LocalDate.of(1984, 8, 13),
            1800
        ));
        
        players.add(new PlayerProfile(
            "Spassky, Boris",
            "4100050",
            LocalDate.of(1937, 1, 30),
            "RUS",
            2660,
            LocalDate.of(1953, 1, 1),
            LocalDate.of(2006, 12, 31),
            2000
        ));
        
        players.add(new PlayerProfile(
            "Anand, Viswanathan",
            "5000017",
            LocalDate.of(1969, 12, 11),
            "IND",
            2817,
            LocalDate.of(1984, 1, 1),
            LocalDate.now(),
            2800
        ));
        
        players.add(new PlayerProfile(
            "Kramnik, Vladimir",
            "4100018",
            LocalDate.of(1975, 6, 25),
            "RUS",
            2817,
            LocalDate.of(1987, 1, 1),
            LocalDate.of(2019, 12, 31),
            2500
        ));
    }
}
