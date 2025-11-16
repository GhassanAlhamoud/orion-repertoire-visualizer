package com.orion.visualizer.model;

import java.time.LocalDate;

/**
 * Encapsulates all filter criteria for repertoire analysis.
 */
public class FilterCriteria {
    private String playerName;
    private PlayerSide side;
    private LocalDate startDate;
    private LocalDate endDate;
    private String opponent;

    public FilterCriteria() {
        this.side = PlayerSide.BOTH;
        this.startDate = LocalDate.of(1900, 1, 1);
        this.endDate = LocalDate.now();
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public PlayerSide getSide() {
        return side;
    }

    public void setSide(PlayerSide side) {
        this.side = side;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getOpponent() {
        return opponent;
    }

    public void setOpponent(String opponent) {
        this.opponent = opponent;
    }

    /**
     * Check if a date is within the filter range.
     */
    public boolean isDateInRange(LocalDate date) {
        if (date == null) {
            return false;
        }
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    /**
     * Check if opponent matches the filter (case-insensitive partial match).
     */
    public boolean matchesOpponent(String opponentName) {
        if (opponent == null || opponent.trim().isEmpty()) {
            return true;
        }
        if (opponentName == null) {
            return false;
        }
        return opponentName.toLowerCase().contains(opponent.toLowerCase());
    }

    @Override
    public String toString() {
        return String.format("FilterCriteria{player='%s', side=%s, dates=%s to %s, opponent='%s'}",
                playerName, side, startDate, endDate, opponent);
    }
}
