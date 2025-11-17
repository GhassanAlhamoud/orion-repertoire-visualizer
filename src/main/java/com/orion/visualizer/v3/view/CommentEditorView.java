package com.orion.visualizer.v3.view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

/**
 * Comment editor view for adding text annotations to moves.
 */
public class CommentEditorView extends VBox {
    private final TextArea commentArea;
    private final Button saveButton;
    private final Button clearButton;
    private final Label statusLabel;
    private Consumer<String> onSave;
    
    public CommentEditorView() {
        // Comment text area
        commentArea = new TextArea();
        commentArea.setPromptText("Add comment for this move...");
        commentArea.setWrapText(true);
        commentArea.setPrefRowCount(5);
        VBox.setVgrow(commentArea, Priority.ALWAYS);
        
        // Buttons
        saveButton = new Button("Save Comment");
        clearButton = new Button("Clear");
        
        HBox buttonBox = new HBox(10, saveButton, clearButton);
        buttonBox.setPadding(new Insets(5, 0, 0, 0));
        
        // Status label
        statusLabel = new Label("");
        statusLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: gray;");
        
        // Layout
        getChildren().addAll(
            new Label("Comment:"),
            commentArea,
            buttonBox,
            statusLabel
        );
        setSpacing(5);
        setPadding(new Insets(10));
        
        // Event handlers
        saveButton.setOnAction(e -> handleSave());
        clearButton.setOnAction(e -> handleClear());
        
        // Auto-save on focus loss
        commentArea.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (wasFocused && !isNowFocused) {
                handleSave();
            }
        });
    }
    
    /**
     * Get comment text.
     */
    public String getComment() {
        return commentArea.getText();
    }
    
    /**
     * Set comment text.
     */
    public void setComment(String comment) {
        commentArea.setText(comment != null ? comment : "");
    }
    
    /**
     * Clear comment.
     */
    public void clear() {
        commentArea.clear();
        statusLabel.setText("");
    }
    
    /**
     * Set callback for when comment is saved.
     */
    public void setOnSave(Consumer<String> callback) {
        this.onSave = callback;
    }
    
    /**
     * Handle save button click.
     */
    private void handleSave() {
        if (onSave != null) {
            onSave.accept(commentArea.getText());
            statusLabel.setText("Comment saved");
        }
    }
    
    /**
     * Handle clear button click.
     */
    private void handleClear() {
        commentArea.clear();
        statusLabel.setText("Comment cleared");
    }
    
    /**
     * Enable/disable editing.
     */
    public void setEditable(boolean editable) {
        commentArea.setEditable(editable);
        saveButton.setDisable(!editable);
        clearButton.setDisable(!editable);
    }
}
