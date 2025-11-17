package com.orion.visualizer.controller;

import com.orion.visualizer.v2.controller.HistoricalAnalysisController;
import com.orion.visualizer.v3.view.RepertoireEditorView;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

/**
 * Enhanced main controller with V2 and V3 mode support.
 * Extends the original V1 functionality with mode switching.
 */
public class EnhancedMainController {
    private final MainController v1Controller;
    private final HistoricalAnalysisController v2Controller;
    private final RepertoireEditorView v3View;
    private final ModeManager modeManager;
    
    private final BorderPane mainView;
    private final MenuBar menuBar;
    
    public EnhancedMainController() {
        // Initialize controllers
        this.v1Controller = new MainController();
        this.v2Controller = new HistoricalAnalysisController();
        this.v3View = new RepertoireEditorView();
        this.modeManager = new ModeManager();
        
        // Create main view
        this.mainView = new BorderPane();
        this.menuBar = createMenuBar();
        mainView.setTop(menuBar);
        
        // Set up mode switching
        setupModeManager();
        
        // Start in V1 mode
        modeManager.switchMode(ModeManager.Mode.V1_PERSONAL_ANALYSIS);
    }
    
    /**
     * Get the main view.
     */
    public BorderPane getView() {
        return mainView;
    }
    
    /**
     * Get V1 controller.
     */
    public MainController getV1Controller() {
        return v1Controller;
    }
    
    /**
     * Create menu bar with mode switching.
     */
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        
        // File menu
        Menu fileMenu = new Menu("File");
        MenuItem importItem = new MenuItem("Import PGN...");
        MenuItem openDbItem = new MenuItem("Open Database...");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> System.exit(0));
        fileMenu.getItems().addAll(importItem, openDbItem, new SeparatorMenuItem(), exitItem);
        
        // Mode menu
        Menu modeMenu = new Menu("Mode");
        ToggleGroup modeGroup = new ToggleGroup();
        
        RadioMenuItem v1Item = new RadioMenuItem("Personal Analysis (V1)");
        v1Item.setToggleGroup(modeGroup);
        v1Item.setSelected(true);
        v1Item.setOnAction(e -> modeManager.switchMode(ModeManager.Mode.V1_PERSONAL_ANALYSIS));
        
        RadioMenuItem v2Item = new RadioMenuItem("Historical Analysis (V2)");
        v2Item.setToggleGroup(modeGroup);
        v2Item.setOnAction(e -> modeManager.switchMode(ModeManager.Mode.V2_HISTORICAL_ANALYSIS));
        
        RadioMenuItem v3Item = new RadioMenuItem("Repertoire Editor (V3)");
        v3Item.setToggleGroup(modeGroup);
        v3Item.setOnAction(e -> modeManager.switchMode(ModeManager.Mode.V3_REPERTOIRE_EDITOR));
        
        modeMenu.getItems().addAll(v1Item, v2Item, v3Item);
        
        // Help menu
        Menu helpMenu = new Menu("Help");
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.setOnAction(e -> showAboutDialog());
        helpMenu.getItems().add(aboutItem);
        
        menuBar.getMenus().addAll(fileMenu, modeMenu, helpMenu);
        
        return menuBar;
    }
    
    /**
     * Set up mode manager.
     */
    private void setupModeManager() {
        modeManager.setModeChangeListener((oldMode, newMode) -> {
            switch (newMode) {
                case V1_PERSONAL_ANALYSIS:
                    // V1 view is created in OrionVisualizerApp
                    // This is handled by the app itself
                    break;
                case V2_HISTORICAL_ANALYSIS:
                    mainView.setCenter(v2Controller.getView());
                    break;
                case V3_REPERTOIRE_EDITOR:
                    mainView.setCenter(v3View);
                    break;
            }
        });
    }
    
    /**
     * Show about dialog.
     */
    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Orion Repertoire Visualizer");
        alert.setHeaderText("Orion Repertoire Visualizer");
        alert.setContentText(
            "Version 3.0\n\n" +
            "V1: Personal game analysis\n" +
            "V2: Historical player analysis\n" +
            "V3: Repertoire editor with visual annotations\n\n" +
            "© 2024 Orion Chess Project"
        );
        alert.showAndWait();
    }
    
    /**
     * Get current mode.
     */
    public ModeManager.Mode getCurrentMode() {
        return modeManager.getCurrentMode();
    }
    
    /**
     * Switch mode programmatically.
     */
    public void switchMode(ModeManager.Mode mode) {
        modeManager.switchMode(mode);
    }
}
