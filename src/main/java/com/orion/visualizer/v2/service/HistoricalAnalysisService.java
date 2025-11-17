package com.orion.visualizer.v2.service;

import com.orion.visualizer.model.PlayerSide;
import com.orion.visualizer.v2.model.OpeningStatistics;
import com.orion.visualizer.v2.model.PlayerProfile;
import com.orion.visualizer.v2.model.TimeframeType;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Service for analyzing historical player opening repertoires.
 */
public class HistoricalAnalysisService {
    private final Random random = new Random(42); // Fixed seed for consistent results
    
    /**
     * Analyze player's opening repertoire for a specific side and timeframe.
     */
    public List<OpeningStatistics> analyzePlayerOpenings(
            PlayerProfile player,
            PlayerSide side,
            LocalDate startDate,
            LocalDate endDate,
            TimeframeType timeframe) {
        
        List<OpeningStatistics> statistics = new ArrayList<>();
        
        // Generate sample statistics for demonstration
        // In production, this would query the OrionDB database
        if (side == PlayerSide.WHITE || side == PlayerSide.BOTH) {
            statistics.addAll(generateWhiteOpenings(player, startDate, endDate, timeframe));
        }
        
        if (side == PlayerSide.BLACK || side == PlayerSide.BOTH) {
            statistics.addAll(generateBlackOpenings(player, startDate, endDate, timeframe));
        }
        
        return statistics;
    }
    
    /**
     * Generate sample White openings for a player.
     */
    private List<OpeningStatistics> generateWhiteOpenings(
            PlayerProfile player,
            LocalDate startDate,
            LocalDate endDate,
            TimeframeType timeframe) {
        
        List<OpeningStatistics> openings = new ArrayList<>();
        
        // 1.e4 repertoire
        OpeningStatistics e4 = createOpening(
            "King's Pawn Game", "B00",
            startDate, endDate,
            calculateGames(player, 0.45), // 45% of games
            timeframe
        );
        openings.add(e4);
        
        // 1.d4 repertoire
        OpeningStatistics d4 = createOpening(
            "Queen's Pawn Game", "D00",
            startDate, endDate,
            calculateGames(player, 0.35), // 35% of games
            timeframe
        );
        openings.add(d4);
        
        // 1.c4 repertoire
        OpeningStatistics c4 = createOpening(
            "English Opening", "A10",
            startDate, endDate,
            calculateGames(player, 0.15), // 15% of games
            timeframe
        );
        openings.add(c4);
        
        // 1.Nf3 repertoire
        OpeningStatistics nf3 = createOpening(
            "Reti Opening", "A04",
            startDate, endDate,
            calculateGames(player, 0.05), // 5% of games
            timeframe
        );
        openings.add(nf3);
        
        return openings;
    }
    
    /**
     * Generate sample Black openings for a player.
     */
    private List<OpeningStatistics> generateBlackOpenings(
            PlayerProfile player,
            LocalDate startDate,
            LocalDate endDate,
            TimeframeType timeframe) {
        
        List<OpeningStatistics> openings = new ArrayList<>();
        
        // vs 1.e4
        OpeningStatistics sicilian = createOpening(
            "Sicilian Defense", "B20",
            startDate, endDate,
            calculateGames(player, 0.25), // 25% of games
            timeframe
        );
        openings.add(sicilian);
        
        OpeningStatistics e5 = createOpening(
            "King's Pawn Opening", "C20",
            startDate, endDate,
            calculateGames(player, 0.15), // 15% of games
            timeframe
        );
        openings.add(e5);
        
        // vs 1.d4
        OpeningStatistics kingsIndian = createOpening(
            "King's Indian Defense", "E60",
            startDate, endDate,
            calculateGames(player, 0.20), // 20% of games
            timeframe
        );
        openings.add(kingsIndian);
        
        OpeningStatistics nimzo = createOpening(
            "Nimzo-Indian Defense", "E20",
            startDate, endDate,
            calculateGames(player, 0.15), // 15% of games
            timeframe
        );
        openings.add(nimzo);
        
        return openings;
    }
    
    /**
     * Create opening statistics with timeline.
     */
    private OpeningStatistics createOpening(
            String name,
            String eco,
            LocalDate startDate,
            LocalDate endDate,
            int totalGames,
            TimeframeType timeframe) {
        
        // Generate realistic statistics
        int wins = (int) (totalGames * (0.35 + random.nextDouble() * 0.20)); // 35-55% wins
        int draws = (int) (totalGames * (0.25 + random.nextDouble() * 0.15)); // 25-40% draws
        int losses = totalGames - wins - draws;
        double avgRating = 2500 + random.nextInt(300);
        
        OpeningStatistics stats = new OpeningStatistics(
            name, eco, startDate, endDate,
            totalGames, wins, draws, losses, avgRating
        );
        
        // Generate timeline data
        generateTimeline(stats, startDate, endDate, totalGames, timeframe);
        
        return stats;
    }
    
    /**
     * Generate timeline data points.
     */
    private void generateTimeline(
            OpeningStatistics stats,
            LocalDate startDate,
            LocalDate endDate,
            int totalGames,
            TimeframeType timeframe) {
        
        long months = ChronoUnit.MONTHS.between(startDate, endDate);
        if (months == 0) months = 1;
        
        int gamesPerMonth = totalGames / (int) months;
        if (gamesPerMonth == 0) gamesPerMonth = 1;
        
        LocalDate current = startDate;
        while (current.isBefore(endDate)) {
            int games = gamesPerMonth + random.nextInt(gamesPerMonth / 2 + 1);
            double winRate = 35 + random.nextDouble() * 20; // 35-55%
            double score = 45 + random.nextDouble() * 15; // 45-60%
            
            stats.addTimePoint(new OpeningStatistics.TimePoint(
                current, games, winRate, score
            ));
            
            // Advance based on timeframe
            switch (timeframe) {
                case MONTHLY:
                    current = current.plusMonths(1);
                    break;
                case QUARTERLY:
                    current = current.plusMonths(3);
                    break;
                case YEARLY:
                    current = current.plusYears(1);
                    break;
                default:
                    current = current.plusMonths(1);
            }
        }
    }
    
    /**
     * Calculate number of games based on player's total games and percentage.
     */
    private int calculateGames(PlayerProfile player, double percentage) {
        return (int) (player.getTotalGames() * percentage);
    }
}
