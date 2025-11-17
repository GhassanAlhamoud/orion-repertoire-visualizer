package com.orion.visualizer.v2.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Statistics for an opening line over a time period.
 */
public class OpeningStatistics {
    private final String opening;
    private final String eco;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final int totalGames;
    private final int wins;
    private final int draws;
    private final int losses;
    private final double averageRating;
    private final List<TimePoint> timeline;
    
    public OpeningStatistics(String opening, String eco,
                           LocalDate startDate, LocalDate endDate,
                           int totalGames, int wins, int draws, int losses,
                           double averageRating) {
        this.opening = Objects.requireNonNull(opening, "Opening cannot be null");
        this.eco = eco;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalGames = totalGames;
        this.wins = wins;
        this.draws = draws;
        this.losses = losses;
        this.averageRating = averageRating;
        this.timeline = new ArrayList<>();
    }
    
    public String getOpening() {
        return opening;
    }
    
    public String getEco() {
        return eco;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public int getTotalGames() {
        return totalGames;
    }
    
    public int getWins() {
        return wins;
    }
    
    public int getDraws() {
        return draws;
    }
    
    public int getLosses() {
        return losses;
    }
    
    public double getAverageRating() {
        return averageRating;
    }
    
    public double getWinRate() {
        return totalGames > 0 ? (double) wins / totalGames * 100 : 0;
    }
    
    public double getDrawRate() {
        return totalGames > 0 ? (double) draws / totalGames * 100 : 0;
    }
    
    public double getLossRate() {
        return totalGames > 0 ? (double) losses / totalGames * 100 : 0;
    }
    
    public double getScore() {
        return totalGames > 0 ? (wins + draws * 0.5) / totalGames * 100 : 0;
    }
    
    public List<TimePoint> getTimeline() {
        return new ArrayList<>(timeline);
    }
    
    public void addTimePoint(TimePoint point) {
        timeline.add(point);
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s): %d games, %.1f%% score",
            opening, eco, totalGames, getScore());
    }
    
    /**
     * Represents a point in time with statistics.
     */
    public static class TimePoint {
        private final LocalDate date;
        private final int games;
        private final double winRate;
        private final double score;
        
        public TimePoint(LocalDate date, int games, double winRate, double score) {
            this.date = date;
            this.games = games;
            this.winRate = winRate;
            this.score = score;
        }
        
        public LocalDate getDate() {
            return date;
        }
        
        public int getGames() {
            return games;
        }
        
        public double getWinRate() {
            return winRate;
        }
        
        public double getScore() {
            return score;
        }
    }
}
