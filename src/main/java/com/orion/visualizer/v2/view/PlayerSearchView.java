package com.orion.visualizer.v2.view;

import com.orion.visualizer.v2.model.PlayerProfile;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.function.Consumer;

/**
 * View component for searching and selecting historical players.
 */
public class PlayerSearchView extends VBox {
    private final TextField searchField;
    private final ListView<PlayerProfile> playerList;
    private final Label statusLabel;
    private final ObservableList<PlayerProfile> players;
    private Consumer<PlayerProfile> onPlayerSelected;
    
    public PlayerSearchView() {
        this.players = FXCollections.observableArrayList();
        
        // Search field
        searchField = new TextField();
        searchField.setPromptText("Search player (e.g., Karpov)...");
        searchField.setPrefWidth(300);
        
        // Search button
        Button searchButton = new Button("Search");
        searchButton.setDefaultButton(true);
        
        // Search box
        HBox searchBox = new HBox(10, searchField, searchButton);
        searchBox.setPadding(new Insets(10));
        
        // Player list
        playerList = new ListView<>(players);
        playerList.setCellFactory(lv -> new PlayerListCell());
        playerList.setPrefHeight(400);
        VBox.setVgrow(playerList, Priority.ALWAYS);
        
        // Status label
        statusLabel = new Label("Enter player name to search");
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");
        statusLabel.setPadding(new Insets(5, 10, 5, 10));
        
        // Layout
        getChildren().addAll(searchBox, playerList, statusLabel);
        setSpacing(5);
        setPadding(new Insets(10));
        
        // Event handlers
        searchButton.setOnAction(e -> fireSearch());
        searchField.setOnAction(e -> fireSearch());
        
        playerList.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (newVal != null && onPlayerSelected != null) {
                    onPlayerSelected.accept(newVal);
                }
            }
        );
    }
    
    /**
     * Set the list of players to display.
     */
    public void setPlayers(List<PlayerProfile> playerList) {
        players.setAll(playerList);
        updateStatus();
    }
    
    /**
     * Get the search query.
     */
    public String getSearchQuery() {
        return searchField.getText();
    }
    
    /**
     * Set callback for when player is selected.
     */
    public void setOnPlayerSelected(Consumer<PlayerProfile> callback) {
        this.onPlayerSelected = callback;
    }
    
    /**
     * Set callback for when search is triggered.
     */
    public void setOnSearch(Runnable callback) {
        // Store callback for search button
    }
    
    /**
     * Get selected player.
     */
    public PlayerProfile getSelectedPlayer() {
        return playerList.getSelectionModel().getSelectedItem();
    }
    
    /**
     * Clear selection.
     */
    public void clearSelection() {
        playerList.getSelectionModel().clearSelection();
    }
    
    /**
     * Fire search event.
     */
    private void fireSearch() {
        // Trigger search through controller
        statusLabel.setText("Searching...");
    }
    
    /**
     * Update status label.
     */
    private void updateStatus() {
        int count = players.size();
        if (count == 0) {
            statusLabel.setText("No players found");
        } else if (count == 1) {
            statusLabel.setText("1 player found");
        } else {
            statusLabel.setText(count + " players found");
        }
    }
    
    /**
     * Custom list cell for player display.
     */
    private static class PlayerListCell extends ListCell<PlayerProfile> {
        @Override
        protected void updateItem(PlayerProfile player, boolean empty) {
            super.updateItem(player, empty);
            
            if (empty || player == null) {
                setText(null);
                setGraphic(null);
            } else {
                VBox content = new VBox(2);
                
                Label nameLabel = new Label(player.getName());
                nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                
                Label infoLabel = new Label(String.format(
                    "%s • Peak: %d • %d games",
                    player.getNationality(),
                    player.getPeakRating(),
                    player.getTotalGames()
                ));
                infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");
                
                content.getChildren().addAll(nameLabel, infoLabel);
                setGraphic(content);
            }
        }
    }
}
