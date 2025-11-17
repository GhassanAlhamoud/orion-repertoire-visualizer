package com.orion.visualizer.v2.controller;

import com.orion.visualizer.model.PlayerSide;
import com.orion.visualizer.v2.model.OpeningStatistics;
import com.orion.visualizer.v2.model.PlayerProfile;
import com.orion.visualizer.v2.model.TimeframeType;
import com.orion.visualizer.v2.service.HistoricalAnalysisService;
import com.orion.visualizer.v2.service.PlayerSearchService;
import com.orion.visualizer.v2.view.OpeningEvolutionChart;
import com.orion.visualizer.v2.view.PlayerSearchView;
import com.orion.visualizer.view.OpeningTreeView;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for V2 Historical Analysis mode.
 */
public class HistoricalAnalysisController {
    private final PlayerSearchService playerSearchService;
    private final HistoricalAnalysisService analysisService;
    
    private final BorderPane mainView;
    private final PlayerSearchView playerSearchView;
    private final OpeningEvolutionChart evolutionChart;
    private final OpeningTreeView treeView;
    
    private PlayerProfile currentPlayer;
    private PlayerSide currentSide = PlayerSide.WHITE;
    private TimeframeType currentTimeframe = TimeframeType.YEARLY;
    
    public HistoricalAnalysisController() {
        this.playerSearchService = new PlayerSearchService();
        this.analysisService = new HistoricalAnalysisService();
        
        // Initialize views
        this.playerSearchView = new PlayerSearchView();
        this.evolutionChart = new OpeningEvolutionChart();
        this.treeView = new OpeningTreeView();
        this.mainView = createMainView();
        
        // Set up event handlers
        setupEventHandlers();
        
        // Load initial data
        loadAllPlayers();
    }
    
    /**
     * Get the main view.
     */
    public BorderPane getView() {
        return mainView;
    }
    
    /**
     * Create the main view layout.
     */
    private BorderPane createMainView() {
        BorderPane view = new BorderPane();
        
        // Left: Player search
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setPrefWidth(350);
        leftPanel.getChildren().add(playerSearchView);
        
        // Center: Charts and tree
        SplitPane centerPanel = new SplitPane();
        centerPanel.setOrientation(Orientation.VERTICAL);
        centerPanel.getItems().addAll(evolutionChart, treeView);
        centerPanel.setDividerPositions(0.5);
        
        // Top: Controls
        HBox controlsPanel = createControlsPanel();
        
        view.setLeft(leftPanel);
        view.setCenter(centerPanel);
        view.setTop(controlsPanel);
        
        return view;
    }
    
    /**
     * Create controls panel.
     */
    private HBox createControlsPanel() {
        HBox controls = new HBox(15);
        controls.setPadding(new Insets(10));
        controls.setStyle("-fx-background-color: #f0f0f0;");
        
        // Side selection
        Label sideLabel = new Label("Side:");
        ComboBox<PlayerSide> sideCombo = new ComboBox<>();
        sideCombo.getItems().addAll(PlayerSide.values());
        sideCombo.setValue(PlayerSide.WHITE);
        sideCombo.setOnAction(e -> {
            currentSide = sideCombo.getValue();
            analyzeCurrentPlayer();
        });
        
        // Timeframe selection
        Label timeframeLabel = new Label("Timeframe:");
        ComboBox<TimeframeType> timeframeCombo = new ComboBox<>();
        timeframeCombo.getItems().addAll(TimeframeType.values());
        timeframeCombo.setValue(TimeframeType.YEARLY);
        timeframeCombo.setOnAction(e -> {
            currentTimeframe = timeframeCombo.getValue();
            analyzeCurrentPlayer();
        });
        
        // Date range
        Label dateLabel = new Label("Date Range:");
        DatePicker startDate = new DatePicker(LocalDate.of(1970, 1, 1));
        DatePicker endDate = new DatePicker(LocalDate.now());
        
        Button analyzeButton = new Button("Analyze");
        analyzeButton.setOnAction(e -> analyzeCurrentPlayer());
        
        controls.getChildren().addAll(
            sideLabel, sideCombo,
            new Separator(Orientation.VERTICAL),
            timeframeLabel, timeframeCombo,
            new Separator(Orientation.VERTICAL),
            dateLabel, startDate, new Label("to"), endDate,
            new Separator(Orientation.VERTICAL),
            analyzeButton
        );
        
        return controls;
    }
    
    /**
     * Set up event handlers.
     */
    private void setupEventHandlers() {
        playerSearchView.setOnPlayerSelected(this::onPlayerSelected);
    }
    
    /**
     * Load all players.
     */
    private void loadAllPlayers() {
        List<PlayerProfile> players = playerSearchService.getAllPlayers();
        playerSearchView.setPlayers(players);
    }
    
    /**
     * Handle player selection.
     */
    private void onPlayerSelected(PlayerProfile player) {
        this.currentPlayer = player;
        analyzeCurrentPlayer();
    }
    
    /**
     * Analyze current player's openings.
     */
    private void analyzeCurrentPlayer() {
        if (currentPlayer == null) {
            return;
        }
        
        // Get date range from player's career
        LocalDate startDate = currentPlayer.getCareerStart();
        LocalDate endDate = currentPlayer.getCareerEnd() != null ? 
            currentPlayer.getCareerEnd() : LocalDate.now();
        
        // Analyze openings
        List<OpeningStatistics> openings = analysisService.analyzePlayerOpenings(
            currentPlayer,
            currentSide,
            startDate,
            endDate,
            currentTimeframe
        );
        
        // Display results
        evolutionChart.displayOpeningEvolution(openings);
        
        // Update tree view with first opening
        if (!openings.isEmpty()) {
            // TODO: Update tree view with opening moves
        }
    }
}
