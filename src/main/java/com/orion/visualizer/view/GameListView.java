package com.orion.visualizer.view;

import com.orion.visualizer.model.GameReference;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

/**
 * JavaFX component for displaying a list of games.
 */
public class GameListView extends TableView<GameReference> {
    private Consumer<GameReference> onGameSelected;

    public GameListView() {
        setupColumns();
        setupSelectionHandler();
    }

    /**
     * Setup table columns.
     */
    private void setupColumns() {
        // Result column
        TableColumn<GameReference, String> resultCol = new TableColumn<>("Result");
        resultCol.setCellValueFactory(new PropertyValueFactory<>("result"));
        resultCol.setPrefWidth(80);
        
        // White column
        TableColumn<GameReference, String> whiteCol = new TableColumn<>("White");
        whiteCol.setCellValueFactory(new PropertyValueFactory<>("white"));
        whiteCol.setPrefWidth(150);
        
        // Black column
        TableColumn<GameReference, String> blackCol = new TableColumn<>("Black");
        blackCol.setCellValueFactory(new PropertyValueFactory<>("black"));
        blackCol.setPrefWidth(150);
        
        // Date column
        TableColumn<GameReference, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(100);
        
        // Event column
        TableColumn<GameReference, String> eventCol = new TableColumn<>("Event");
        eventCol.setCellValueFactory(new PropertyValueFactory<>("event"));
        eventCol.setPrefWidth(200);
        
        getColumns().addAll(resultCol, whiteCol, blackCol, dateCol, eventCol);
    }

    /**
     * Setup selection handler.
     */
    private void setupSelectionHandler() {
        getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && onGameSelected != null) {
                onGameSelected.accept(newVal);
            }
        });
    }

    /**
     * Set the list of games to display.
     */
    public void setGames(List<GameReference> games) {
        ObservableList<GameReference> items = FXCollections.observableArrayList(games);
        setItems(items);
    }

    /**
     * Clear the game list.
     */
    public void clearGames() {
        getItems().clear();
    }

    /**
     * Set callback for game selection.
     */
    public void setOnGameSelected(Consumer<GameReference> callback) {
        this.onGameSelected = callback;
    }
}
