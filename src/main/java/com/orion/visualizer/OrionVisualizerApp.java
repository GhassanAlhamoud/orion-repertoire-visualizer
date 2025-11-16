package com.orion.visualizer;

import com.orion.visualizer.controller.MainController;
import com.orion.visualizer.model.FilterCriteria;
import com.orion.visualizer.model.PlayerSide;
import com.orion.visualizer.view.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;

/**
 * Main JavaFX application for Orion Repertoire Visualizer.
 */
public class OrionVisualizerApp extends Application {
    private MainController controller;
    private Stage primaryStage;
    
    // UI Components
    private ChessboardView chessboardView;
    private OpeningTreeView openingTreeView;
    private GameListView gameListView;
    private Label statusLabel;
    private ProgressBar progressBar;
    
    // Filter components
    private TextField playerNameField;
    private ComboBox<PlayerSide> sideComboBox;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private TextField opponentField;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.controller = new MainController();
        
        // Setup UI
        BorderPane root = new BorderPane();
        root.setTop(createMenuBar());
        root.setCenter(createMainContent());
        root.setBottom(createStatusBar());
        
        // Setup controller callbacks
        controller.setStatusCallback(this::updateStatus);
        controller.setLoadingCallback(this::setLoading);
        
        // Initialize views in controller
        controller.setViews(chessboardView, openingTreeView, gameListView);
        
        // Create scene with stylesheet
        Scene scene = new Scene(root, 1400, 900);
        scene.getStylesheets().add(
            getClass().getResource("/css/styles.css").toExternalForm()
        );
        
        primaryStage.setTitle("Orion Repertoire Visualizer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Create menu bar.
     */
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        
        // File menu
        Menu fileMenu = new Menu("File");
        
        MenuItem importPgnItem = new MenuItem("Import PGN...");
        importPgnItem.setOnAction(e -> handleImportPgn());
        
        MenuItem loadDbItem = new MenuItem("Load Database...");
        loadDbItem.setOnAction(e -> handleLoadDatabase());
        
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> primaryStage.close());
        
        fileMenu.getItems().addAll(importPgnItem, loadDbItem, new SeparatorMenuItem(), exitItem);
        
        // View menu
        Menu viewMenu = new Menu("View");
        
        MenuItem resetViewItem = new MenuItem("Reset View");
        resetViewItem.setOnAction(e -> controller.reset());
        
        MenuItem expandTreeItem = new MenuItem("Expand Tree");
        expandTreeItem.setOnAction(e -> openingTreeView.expandToDepth(3));
        
        viewMenu.getItems().addAll(resetViewItem, expandTreeItem);
        
        // Help menu
        Menu helpMenu = new Menu("Help");
        
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAboutDialog());
        
        helpMenu.getItems().add(aboutItem);
        
        menuBar.getMenus().addAll(fileMenu, viewMenu, helpMenu);
        return menuBar;
    }

    /**
     * Create main content area.
     */
    private Node createMainContent() {
        // Create main split pane
        SplitPane mainSplit = new SplitPane();
        mainSplit.setOrientation(Orientation.HORIZONTAL);
        
        // Left side: Filters and chessboard
        VBox leftPanel = createLeftPanel();
        
        // Center: Opening tree
        VBox centerPanel = createCenterPanel();
        
        // Right: Game list
        VBox rightPanel = createRightPanel();
        
        mainSplit.getItems().addAll(leftPanel, centerPanel, rightPanel);
        mainSplit.setDividerPositions(0.25, 0.55);
        
        return mainSplit;
    }

    /**
     * Create left panel with filters and chessboard.
     */
    private VBox createLeftPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.getStyleClass().add("filter-panel");
        
        // Title
        Label titleLabel = new Label("Filters");
        titleLabel.getStyleClass().add("label-header");
        
        // Player name
        Label playerLabel = new Label("Player Name:");
        playerNameField = new TextField();
        playerNameField.setPromptText("Enter player name...");
        
        // Side selection
        Label sideLabel = new Label("Side:");
        sideComboBox = new ComboBox<>();
        sideComboBox.getItems().addAll(PlayerSide.values());
        sideComboBox.setValue(PlayerSide.BOTH);
        
        // Date range
        Label dateLabel = new Label("Date Range:");
        HBox dateBox = new HBox(5);
        startDatePicker = new DatePicker(LocalDate.of(1900, 1, 1));
        endDatePicker = new DatePicker(LocalDate.now());
        dateBox.getChildren().addAll(startDatePicker, new Label("to"), endDatePicker);
        
        // Opponent
        Label opponentLabel = new Label("Opponent:");
        opponentField = new TextField();
        opponentField.setPromptText("Filter by opponent...");
        
        // Apply button
        Button applyButton = new Button("Apply Filters");
        applyButton.getStyleClass().add("button-primary");
        applyButton.setMaxWidth(Double.MAX_VALUE);
        applyButton.setOnAction(e -> handleApplyFilters());
        
        // Reset button
        Button resetButton = new Button("Reset Filters");
        resetButton.getStyleClass().add("button-secondary");
        resetButton.setMaxWidth(Double.MAX_VALUE);
        resetButton.setOnAction(e -> handleResetFilters());
        
        // Chessboard
        Label boardLabel = new Label("Board Position:");
        boardLabel.getStyleClass().add("label-subheader");
        chessboardView = new ChessboardView();
        chessboardView.getStyleClass().add("chessboard");
        
        // Add all components
        panel.getChildren().addAll(
            titleLabel,
            new Separator(),
            playerLabel, playerNameField,
            sideLabel, sideComboBox,
            dateLabel, dateBox,
            opponentLabel, opponentField,
            applyButton, resetButton,
            new Separator(),
            boardLabel, chessboardView
        );
        
        return panel;
    }

    /**
     * Create center panel with opening tree.
     */
    private VBox createCenterPanel() {
        VBox panel = new VBox(5);
        panel.setPadding(new Insets(10));
        
        Label titleLabel = new Label("Opening Tree");
        titleLabel.getStyleClass().add("label-header");
        
        openingTreeView = new OpeningTreeView();
        VBox.setVgrow(openingTreeView, Priority.ALWAYS);
        
        panel.getChildren().addAll(titleLabel, openingTreeView);
        return panel;
    }

    /**
     * Create right panel with game list.
     */
    private VBox createRightPanel() {
        VBox panel = new VBox(5);
        panel.setPadding(new Insets(10));
        
        Label titleLabel = new Label("Games");
        titleLabel.getStyleClass().add("label-header");
        
        gameListView = new GameListView();
        VBox.setVgrow(gameListView, Priority.ALWAYS);
        
        panel.getChildren().addAll(titleLabel, gameListView);
        return panel;
    }

    /**
     * Create status bar.
     */
    private Node createStatusBar() {
        HBox statusBar = new HBox(10);
        statusBar.setPadding(new Insets(5, 10, 5, 10));
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.getStyleClass().add("status-bar");
        
        statusLabel = new Label("Ready");
        HBox.setHgrow(statusLabel, Priority.ALWAYS);
        
        progressBar = new ProgressBar();
        progressBar.setPrefWidth(200);
        progressBar.setVisible(false);
        
        statusBar.getChildren().addAll(statusLabel, progressBar);
        return statusBar;
    }

    /**
     * Handle import PGN action.
     */
    private void handleImportPgn() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select PGN File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("PGN Files", "*.pgn")
        );
        
        File pgnFile = fileChooser.showOpenDialog(primaryStage);
        if (pgnFile == null) {
            return;
        }
        
        // Choose output location
        FileChooser saveChooser = new FileChooser();
        saveChooser.setTitle("Save Database As");
        saveChooser.setInitialFileName(pgnFile.getName().replace(".pgn", ".oriondb"));
        saveChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("OrionDB Files", "*.oriondb")
        );
        
        File dbFile = saveChooser.showSaveDialog(primaryStage);
        if (dbFile == null) {
            return;
        }
        
        controller.importPgn(pgnFile, dbFile);
    }

    /**
     * Handle load database action.
     */
    private void handleLoadDatabase() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Database");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("OrionDB Files", "*.oriondb")
        );
        
        File dbFile = fileChooser.showOpenDialog(primaryStage);
        if (dbFile != null) {
            controller.loadDatabase(dbFile);
        }
    }

    /**
     * Handle apply filters action.
     */
    private void handleApplyFilters() {
        FilterCriteria filters = new FilterCriteria();
        
        String playerName = playerNameField.getText().trim();
        if (!playerName.isEmpty()) {
            filters.setPlayerName(playerName);
        }
        
        filters.setSide(sideComboBox.getValue());
        filters.setStartDate(startDatePicker.getValue());
        filters.setEndDate(endDatePicker.getValue());
        
        String opponent = opponentField.getText().trim();
        if (!opponent.isEmpty()) {
            filters.setOpponent(opponent);
        }
        
        controller.applyFilters(filters);
    }

    /**
     * Handle reset filters action.
     */
    private void handleResetFilters() {
        playerNameField.clear();
        sideComboBox.setValue(PlayerSide.BOTH);
        startDatePicker.setValue(LocalDate.of(1900, 1, 1));
        endDatePicker.setValue(LocalDate.now());
        opponentField.clear();
        
        controller.applyFilters(new FilterCriteria());
    }

    /**
     * Show about dialog.
     */
    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Orion Repertoire Visualizer");
        alert.setHeaderText("Orion Repertoire Visualizer v1.0");
        alert.setContentText(
            "A JavaFX application for visualizing chess opening repertoire evolution over time.\n\n" +
            "Built with:\n" +
            "- JavaFX 21\n" +
            "- OrionDB Chess Engine\n\n" +
            "© 2024 Orion Chess Project"
        );
        alert.showAndWait();
    }

    /**
     * Update status message.
     */
    private void updateStatus(String message) {
        statusLabel.setText(message);
    }

    /**
     * Set loading state.
     */
    private void setLoading(boolean loading) {
        progressBar.setVisible(loading);
        if (loading) {
            progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
