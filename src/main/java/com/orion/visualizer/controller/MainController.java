package com.orion.visualizer.controller;

import com.orion.visualizer.model.*;
import com.orion.visualizer.service.*;
import com.orion.visualizer.view.*;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.List;
import java.util.function.Consumer;

/**
 * Main controller coordinating the application logic.
 */
public class MainController {
    private final DatabaseService databaseService;
    private final AnalysisService analysisService;
    private final ChessEngineService displayEngine;
    
    private ChessboardView chessboardView;
    private OpeningTreeView openingTreeView;
    private GameListView gameListView;
    
    private Consumer<String> statusCallback;
    private Consumer<Boolean> loadingCallback;

    public MainController() {
        this.databaseService = DatabaseService.getInstance();
        this.analysisService = new AnalysisService(databaseService);
        this.displayEngine = new ChessEngineService();
    }

    /**
     * Set UI components.
     */
    public void setViews(ChessboardView chessboardView, 
                        OpeningTreeView openingTreeView,
                        GameListView gameListView) {
        this.chessboardView = chessboardView;
        this.openingTreeView = openingTreeView;
        this.gameListView = gameListView;
        
        // Setup event handlers
        setupEventHandlers();
    }

    /**
     * Setup event handlers for UI components.
     */
    private void setupEventHandlers() {
        // When a node is selected in the tree, update the board
        openingTreeView.setOnNodeSelected(this::onTreeNodeSelected);
        
        // When a game is selected, could show full game (future feature)
        gameListView.setOnGameSelected(this::onGameSelected);
    }

    /**
     * Handle tree node selection.
     */
    private void onTreeNodeSelected(OpeningTreeNode node) {
        if (node == null) {
            return;
        }
        
        // Reset engine and replay moves to this position
        displayEngine.reset();
        List<String> movePath = node.getMovePath();
        displayEngine.makeMoves(movePath);
        
        // Update board display
        chessboardView.setEngine(displayEngine);
        
        // Update game list
        gameListView.setGames(node.getGames());
        
        // Update status
        updateStatus(String.format("Position: %s | Games: %d | Win: %.1f%% Draw: %.1f%% Loss: %.1f%%",
            node.getMove() != null ? node.getMove() : "Start",
            node.getGameCount(),
            node.getWinPercentage(),
            node.getDrawPercentage(),
            node.getLossPercentage()));
    }

    /**
     * Handle game selection.
     */
    private void onGameSelected(GameReference game) {
        updateStatus(String.format("Selected: %s vs %s (%s) - %s",
            game.getWhite(), game.getBlack(), game.getDate(), game.getResult()));
    }

    /**
     * Import PGN file and build database.
     */
    public void importPgn(java.io.File pgnFile, java.io.File dbFile) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Importing PGN file...");
                setLoading(true);
                
                var stats = databaseService.createDatabaseFromPgn(
                    pgnFile, 
                    dbFile,
                    (current, total, message) -> {
                        updateMessage(message);
                        if (total > 0) {
                            updateProgress(current, total);
                        }
                    }
                );
                
                updateMessage("Import complete: " + stats.getGamesImported() + " games");
                return null;
            }
        };
        
        task.setOnSucceeded(e -> {
            setLoading(false);
            updateStatus("Database created successfully");
            // Auto-build tree with default filters
            buildTree(new FilterCriteria());
        });
        
        task.setOnFailed(e -> {
            setLoading(false);
            updateStatus("Error importing PGN: " + task.getException().getMessage());
        });
        
        new Thread(task).start();
    }

    /**
     * Load existing database.
     */
    public void loadDatabase(java.io.File dbFile) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Loading database...");
                setLoading(true);
                databaseService.loadDatabase(dbFile);
                return null;
            }
        };
        
        task.setOnSucceeded(e -> {
            setLoading(false);
            updateStatus("Database loaded: " + databaseService.getGameCount() + " games");
            // Auto-build tree with default filters
            buildTree(new FilterCriteria());
        });
        
        task.setOnFailed(e -> {
            setLoading(false);
            updateStatus("Error loading database: " + task.getException().getMessage());
        });
        
        new Thread(task).start();
    }

    /**
     * Build opening tree with filters.
     */
    public void buildTree(FilterCriteria filters) {
        Task<OpeningTreeNode> task = new Task<>() {
            @Override
            protected OpeningTreeNode call() throws Exception {
                updateMessage("Building opening tree...");
                setLoading(true);
                return analysisService.buildTree(filters);
            }
        };
        
        task.setOnSucceeded(e -> {
            setLoading(false);
            OpeningTreeNode root = task.getValue();
            openingTreeView.setTreeRoot(root);
            openingTreeView.expandToDepth(2);
            
            // Reset board to starting position
            displayEngine.reset();
            chessboardView.setEngine(displayEngine);
            
            updateStatus("Tree built: " + analysisService.getTreeStatistics());
        });
        
        task.setOnFailed(e -> {
            setLoading(false);
            updateStatus("Error building tree: " + task.getException().getMessage());
            e.getStackTrace();
        });
        
        new Thread(task).start();
    }

    /**
     * Apply filters and rebuild tree.
     */
    public void applyFilters(FilterCriteria filters) {
        if (!databaseService.isDatabaseLoaded()) {
            updateStatus("No database loaded");
            return;
        }
        buildTree(filters);
    }

    /**
     * Reset to starting position.
     */
    public void reset() {
        displayEngine.reset();
        chessboardView.setEngine(displayEngine);
        gameListView.clearGames();
        updateStatus("Reset to starting position");
    }

    /**
     * Get database service.
     */
    public DatabaseService getDatabaseService() {
        return databaseService;
    }

    /**
     * Get analysis service.
     */
    public AnalysisService getAnalysisService() {
        return analysisService;
    }

    /**
     * Set status callback.
     */
    public void setStatusCallback(Consumer<String> callback) {
        this.statusCallback = callback;
    }

    /**
     * Set loading callback.
     */
    public void setLoadingCallback(Consumer<Boolean> callback) {
        this.loadingCallback = callback;
    }

    /**
     * Update status message.
     */
    private void updateStatus(String message) {
        if (statusCallback != null) {
            Platform.runLater(() -> statusCallback.accept(message));
        }
    }

    /**
     * Set loading state.
     */
    private void setLoading(boolean loading) {
        if (loadingCallback != null) {
            Platform.runLater(() -> loadingCallback.accept(loading));
        }
    }
}
