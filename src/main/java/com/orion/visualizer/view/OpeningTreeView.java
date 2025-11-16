package com.orion.visualizer.view;

import com.orion.visualizer.model.OpeningTreeNode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.paint.Color;
import javafx.scene.control.TreeCell;

import java.util.function.Consumer;

/**
 * JavaFX component for displaying the opening tree.
 */
public class OpeningTreeView extends TreeView<OpeningTreeNode> {
    private Consumer<OpeningTreeNode> onNodeSelected;

    public OpeningTreeView() {
        setCellFactory(tv -> new OpeningTreeCell());
        setShowRoot(true);
        
        // Handle selection
        getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && onNodeSelected != null) {
                onNodeSelected.accept(newVal.getValue());
            }
        });
    }

    /**
     * Set the root node of the tree.
     */
    public void setTreeRoot(OpeningTreeNode node) {
        if (node == null) {
            setRoot(null);
            return;
        }
        
        TreeItem<OpeningTreeNode> rootItem = buildTreeItem(node);
        rootItem.setExpanded(true);
        setRoot(rootItem);
    }

    /**
     * Build a TreeItem from an OpeningTreeNode recursively.
     */
    private TreeItem<OpeningTreeNode> buildTreeItem(OpeningTreeNode node) {
        TreeItem<OpeningTreeNode> item = new TreeItem<>(node);
        
        // Add children sorted by game count
        for (OpeningTreeNode child : node.getChildrenSorted()) {
            TreeItem<OpeningTreeNode> childItem = buildTreeItem(child);
            item.getChildren().add(childItem);
        }
        
        return item;
    }

    /**
     * Set callback for node selection.
     */
    public void setOnNodeSelected(Consumer<OpeningTreeNode> callback) {
        this.onNodeSelected = callback;
    }

    /**
     * Expand tree to a certain depth.
     */
    public void expandToDepth(int depth) {
        if (getRoot() != null) {
            expandToDepth(getRoot(), depth, 0);
        }
    }

    private void expandToDepth(TreeItem<OpeningTreeNode> item, int maxDepth, int currentDepth) {
        if (currentDepth < maxDepth) {
            item.setExpanded(true);
            for (TreeItem<OpeningTreeNode> child : item.getChildren()) {
                expandToDepth(child, maxDepth, currentDepth + 1);
            }
        }
    }

    /**
     * Navigate to a specific node by move path.
     */
    public void navigateToPath(java.util.List<String> movePath) {
        if (getRoot() == null || movePath.isEmpty()) {
            return;
        }
        
        TreeItem<OpeningTreeNode> current = getRoot();
        
        for (String move : movePath) {
            boolean found = false;
            for (TreeItem<OpeningTreeNode> child : current.getChildren()) {
                if (child.getValue().getMove() != null && 
                    child.getValue().getMove().equals(move)) {
                    current = child;
                    current.setExpanded(true);
                    found = true;
                    break;
                }
            }
            if (!found) {
                break;
            }
        }
        
        getSelectionModel().select(current);
        scrollTo(getRow(current));
    }

    /**
     * Custom tree cell for displaying opening tree nodes with color coding.
     */
    private static class OpeningTreeCell extends TreeCell<OpeningTreeNode> {
        @Override
        protected void updateItem(OpeningTreeNode node, boolean empty) {
            super.updateItem(node, empty);
            
            if (empty || node == null) {
                setText(null);
                setGraphic(null);
                setStyle("");
            } else {
                setText(node.getCompactDisplayString());
                
                // Color code based on win percentage
                double winRate = node.getWinPercentage();
                String color;
                
                if (node.getGameCount() < 3) {
                    color = "#888888"; // Gray for insufficient data
                } else if (winRate >= 55) {
                    color = "#2E7D32"; // Green for good
                } else if (winRate >= 45) {
                    color = "#F57C00"; // Orange for average
                } else {
                    color = "#C62828"; // Red for poor
                }
                
                setStyle("-fx-text-fill: " + color + ";");
            }
        }
    }
}
