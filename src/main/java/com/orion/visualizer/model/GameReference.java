package com.orion.visualizer.model;

import java.time.LocalDate;

/**
 * Lightweight reference to a game, used in opening tree nodes.
 */
public class GameReference {
    private final int gameId;
    private final String white;
    private final String black;
    private final String opponent;
    private final String result;
    private final LocalDate date;
    private final String event;
    private final PlayerSide playerSide;

    public GameReference(int gameId, String white, String black, String result, 
                        LocalDate date, String event, PlayerSide playerSide) {
        this.gameId = gameId;
        this.white = white;
        this.black = black;
        this.result = result;
        this.date = date;
        this.event = event;
        this.playerSide = playerSide;
        
        // Determine opponent based on player side
        if (playerSide == PlayerSide.WHITE) {
            this.opponent = black;
        } else if (playerSide == PlayerSide.BLACK) {
            this.opponent = white;
        } else {
            this.opponent = null;
        }
    }

    public int getGameId() {
        return gameId;
    }

    public String getWhite() {
        return white;
    }

    public String getBlack() {
        return black;
    }

    public String getOpponent() {
        return opponent;
    }

    public String getResult() {
        return result;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getEvent() {
        return event;
    }

    public PlayerSide getPlayerSide() {
        return playerSide;
    }

    /**
     * Check if this game is a win from the player's perspective.
     */
    public boolean isWin() {
        return playerSide.isWin(result);
    }

    /**
     * Check if this game is a loss from the player's perspective.
     */
    public boolean isLoss() {
        return playerSide.isLoss(result);
    }

    /**
     * Check if this game is a draw.
     */
    public boolean isDraw() {
        return playerSide.isDraw(result);
    }

    @Override
    public String toString() {
        return String.format("Game #%d: %s vs %s (%s) - %s", 
                gameId, white, black, date, result);
    }
}
