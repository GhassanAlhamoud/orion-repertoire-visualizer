package com.orion.visualizer.model;

import java.util.*;

/**
 * Represents a node in the opening tree.
 * Each node corresponds to a position reached after a sequence of moves.
 */
public class OpeningTreeNode {
    private final String fen;
    private final String move; // Move in SAN notation that led to this position
    private final int moveNumber;
    private final OpeningTreeNode parent;
    
    private final Map<String, OpeningTreeNode> children;
    private final List<GameReference> games;
    
    // Statistics
    private int wins;
    private int draws;
    private int losses;

    /**
     * Constructor for root node.
     */
    public OpeningTreeNode(String fen) {
        this(fen, null, 0, null);
    }

    /**
     * Constructor for child node.
     */
    public OpeningTreeNode(String fen, String move, int moveNumber, OpeningTreeNode parent) {
        this.fen = fen;
        this.move = move;
        this.moveNumber = moveNumber;
        this.parent = parent;
        this.children = new LinkedHashMap<>();
        this.games = new ArrayList<>();
        this.wins = 0;
        this.draws = 0;
        this.losses = 0;
    }

    /**
     * Add a game to this node and update statistics.
     */
    public void addGame(GameReference game) {
        games.add(game);
        
        if (game.isWin()) {
            wins++;
        } else if (game.isDraw()) {
            draws++;
        } else if (game.isLoss()) {
            losses++;
        }
    }

    /**
     * Get or create a child node for a given move.
     */
    public OpeningTreeNode getOrCreateChild(String move, String fen, int moveNumber) {
        return children.computeIfAbsent(move, 
            m -> new OpeningTreeNode(fen, m, moveNumber, this));
    }

    /**
     * Get child node for a specific move.
     */
    public OpeningTreeNode getChild(String move) {
        return children.get(move);
    }

    /**
     * Get all children sorted by game count (descending).
     */
    public List<OpeningTreeNode> getChildrenSorted() {
        List<OpeningTreeNode> sortedChildren = new ArrayList<>(children.values());
        sortedChildren.sort((a, b) -> Integer.compare(b.getGameCount(), a.getGameCount()));
        return sortedChildren;
    }

    public String getFen() {
        return fen;
    }

    public String getMove() {
        return move;
    }

    public int getMoveNumber() {
        return moveNumber;
    }

    public OpeningTreeNode getParent() {
        return parent;
    }

    public Map<String, OpeningTreeNode> getChildren() {
        return Collections.unmodifiableMap(children);
    }

    public List<GameReference> getGames() {
        return Collections.unmodifiableList(games);
    }

    public int getGameCount() {
        return games.size();
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

    /**
     * Get win percentage.
     */
    public double getWinPercentage() {
        int total = getGameCount();
        return total > 0 ? (wins * 100.0 / total) : 0.0;
    }

    /**
     * Get draw percentage.
     */
    public double getDrawPercentage() {
        int total = getGameCount();
        return total > 0 ? (draws * 100.0 / total) : 0.0;
    }

    /**
     * Get loss percentage.
     */
    public double getLossPercentage() {
        int total = getGameCount();
        return total > 0 ? (losses * 100.0 / total) : 0.0;
    }

    /**
     * Get the path from root to this node as a list of moves.
     */
    public List<String> getMovePath() {
        List<String> path = new ArrayList<>();
        OpeningTreeNode current = this;
        while (current.parent != null) {
            path.add(0, current.move);
            current = current.parent;
        }
        return path;
    }

    /**
     * Get a display string for this node showing move and statistics.
     */
    public String getDisplayString() {
        if (move == null) {
            return "Start Position";
        }
        return String.format("%s (N=%d, W:%.1f%% D:%.1f%% L:%.1f%%)",
                move, getGameCount(), getWinPercentage(), getDrawPercentage(), getLossPercentage());
    }

    /**
     * Get a compact display string for tree view.
     */
    public String getCompactDisplayString() {
        if (move == null) {
            return String.format("Start (N=%d)", getGameCount());
        }
        return String.format("%s (N=%d, %.0f%%)", move, getGameCount(), getWinPercentage());
    }

    @Override
    public String toString() {
        return getDisplayString();
    }
}
