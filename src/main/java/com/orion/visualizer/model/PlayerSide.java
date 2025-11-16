package com.orion.visualizer.model;

/**
 * Represents the side (color) a player is playing in a chess game.
 */
public enum PlayerSide {
    WHITE("White", "1-0", "0-1"),
    BLACK("Black", "0-1", "1-0"),
    BOTH("Both", null, null);

    private final String displayName;
    private final String winResult;
    private final String lossResult;

    PlayerSide(String displayName, String winResult, String lossResult) {
        this.displayName = displayName;
        this.winResult = winResult;
        this.lossResult = lossResult;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getWinResult() {
        return winResult;
    }

    public String getLossResult() {
        return lossResult;
    }

    /**
     * Determine if a result is a win from this side's perspective.
     */
    public boolean isWin(String result) {
        if (this == BOTH) {
            return false;
        }
        return winResult.equals(result);
    }

    /**
     * Determine if a result is a loss from this side's perspective.
     */
    public boolean isLoss(String result) {
        if (this == BOTH) {
            return false;
        }
        return lossResult.equals(result);
    }

    /**
     * Determine if a result is a draw.
     */
    public boolean isDraw(String result) {
        return "1/2-1/2".equals(result) || "1/2".equals(result);
    }

    @Override
    public String toString() {
        return displayName;
    }
}
