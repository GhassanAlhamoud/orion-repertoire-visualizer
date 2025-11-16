package com.orion.visualizer.service;

import com.oriondb.model.Game;
import com.oriondb.model.Move;
import com.orion.visualizer.model.*;
import com.orion.visualizer.util.ChessNotation;
import com.orion.visualizer.util.DateUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service for analyzing chess games and building opening repertoire trees.
 */
public class AnalysisService {
    private static final int MAX_OPENING_DEPTH = 20; // Analyze first 20 moves
    
    private final DatabaseService databaseService;
    private OpeningTreeNode currentTree;
    private FilterCriteria currentFilters;

    public AnalysisService(DatabaseService databaseService) {
        this.databaseService = databaseService;
        this.currentFilters = new FilterCriteria();
    }

    /**
     * Build opening tree for all games in the database with current filters.
     */
    public CompletableFuture<OpeningTreeNode> buildTreeAsync(FilterCriteria filters) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return buildTree(filters);
            } catch (Exception e) {
                throw new RuntimeException("Error building tree: " + e.getMessage(), e);
            }
        });
    }

    /**
     * Build opening tree synchronously.
     */
    public OpeningTreeNode buildTree(FilterCriteria filters) throws Exception {
        this.currentFilters = filters;
        
        // Get games based on filters
        List<Game> games = getFilteredGames(filters);
        
        // Build tree from games
        OpeningTreeNode root = new OpeningTreeNode(ChessNotation.getStartingFen());
        
        for (Game game : games) {
            processGame(game, root, filters);
        }
        
        this.currentTree = root;
        return root;
    }

    /**
     * Get games filtered by criteria.
     */
    private List<Game> getFilteredGames(FilterCriteria filters) throws Exception {
        List<Game> games;
        
        // Filter by player and side
        if (filters.getPlayerName() != null && !filters.getPlayerName().isEmpty()) {
            if (filters.getSide() == PlayerSide.WHITE) {
                games = databaseService.searchByPlayerAndSide(filters.getPlayerName(), true);
            } else if (filters.getSide() == PlayerSide.BLACK) {
                games = databaseService.searchByPlayerAndSide(filters.getPlayerName(), false);
            } else {
                games = databaseService.searchByPlayer(filters.getPlayerName());
            }
        } else {
            games = databaseService.getAllGames();
        }
        
        // Apply additional filters
        return games.stream()
            .filter(game -> matchesFilters(game, filters))
            .toList();
    }

    /**
     * Check if a game matches all filter criteria.
     */
    private boolean matchesFilters(Game game, FilterCriteria filters) {
        // Date filter
        LocalDate gameDate = DateUtils.parsePgnDate(game.getDate());
        if (!filters.isDateInRange(gameDate)) {
            return false;
        }
        
        // Opponent filter
        if (filters.getOpponent() != null && !filters.getOpponent().isEmpty()) {
            String opponent = getOpponent(game, filters);
            if (!filters.matchesOpponent(opponent)) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Get opponent name based on player side.
     */
    private String getOpponent(Game game, FilterCriteria filters) {
        if (filters.getPlayerName() == null) {
            return null;
        }
        
        String playerName = filters.getPlayerName();
        if (game.getWhite().equalsIgnoreCase(playerName)) {
            return game.getBlack();
        } else if (game.getBlack().equalsIgnoreCase(playerName)) {
            return game.getWhite();
        }
        return null;
    }

    /**
     * Process a single game and add it to the tree.
     */
    private void processGame(Game game, OpeningTreeNode root, FilterCriteria filters) {
        // Determine player side in this game
        PlayerSide playerSide = determinePlayerSide(game, filters);
        if (playerSide == PlayerSide.BOTH) {
            return; // Skip if we can't determine side
        }
        
        // Create game reference
        LocalDate gameDate = DateUtils.parsePgnDate(game.getDate());
        GameReference gameRef = new GameReference(
            game.getId(),
            game.getWhite(),
            game.getBlack(),
            game.getResult(),
            gameDate,
            game.getEvent(),
            playerSide
        );
        
        // Build chess engine to track position
        ChessEngineService engine = new ChessEngineService();
        OpeningTreeNode currentNode = root;
        
        // Process moves up to MAX_OPENING_DEPTH
        List<Move> moves = game.getMoves();
        int moveCount = 0;
        
        for (Move move : moves) {
            if (moveCount >= MAX_OPENING_DEPTH) {
                break;
            }
            
            String moveStr = move.getSan();
            if (moveStr == null || moveStr.isEmpty()) {
                break;
            }
            
            // Make move on engine
            if (!engine.makeMove(moveStr)) {
                break; // Invalid move, stop processing
            }
            
            // Get new position
            String fen = engine.getFen();
            int moveNumber = engine.getMoveNumber();
            
            // Get or create child node
            currentNode = currentNode.getOrCreateChild(moveStr, fen, moveNumber);
            
            // Add game to this node
            currentNode.addGame(gameRef);
            
            moveCount++;
        }
    }

    /**
     * Determine which side the player was playing in this game.
     */
    private PlayerSide determinePlayerSide(Game game, FilterCriteria filters) {
        if (filters.getPlayerName() == null || filters.getPlayerName().isEmpty()) {
            return PlayerSide.BOTH;
        }
        
        String playerName = filters.getPlayerName().toLowerCase();
        String white = game.getWhite().toLowerCase();
        String black = game.getBlack().toLowerCase();
        
        if (white.contains(playerName)) {
            return PlayerSide.WHITE;
        } else if (black.contains(playerName)) {
            return PlayerSide.BLACK;
        }
        
        return PlayerSide.BOTH;
    }

    /**
     * Get the current tree.
     */
    public OpeningTreeNode getCurrentTree() {
        return currentTree;
    }

    /**
     * Get current filters.
     */
    public FilterCriteria getCurrentFilters() {
        return currentFilters;
    }

    /**
     * Navigate to a specific node in the tree by following a move path.
     */
    public OpeningTreeNode navigateToNode(List<String> movePath) {
        if (currentTree == null) {
            return null;
        }
        
        OpeningTreeNode node = currentTree;
        for (String move : movePath) {
            node = node.getChild(move);
            if (node == null) {
                return null;
            }
        }
        return node;
    }

    /**
     * Get statistics summary for current tree.
     */
    public String getTreeStatistics() {
        if (currentTree == null) {
            return "No tree built";
        }
        
        int totalGames = currentTree.getGameCount();
        int totalVariations = countNodes(currentTree) - 1; // Exclude root
        
        return String.format("Total Games: %d, Variations: %d", totalGames, totalVariations);
    }

    /**
     * Count total nodes in tree.
     */
    private int countNodes(OpeningTreeNode node) {
        int count = 1;
        for (OpeningTreeNode child : node.getChildren().values()) {
            count += countNodes(child);
        }
        return count;
    }
}
