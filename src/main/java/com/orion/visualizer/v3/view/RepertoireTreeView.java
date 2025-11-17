package com.orion.visualizer.v3.view;

import com.orion.visualizer.v3.model.RepertoireNode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Tree view for displaying repertoire moves.
 */
public class RepertoireTreeView extends TreeView<RepertoireNode> {
    
    public RepertoireTreeView() {
        setCellFactory(tv -> new RepertoireTreeCell());
        setShowRoot(false);
    }
    
    /**
     * Display a repertoire tree.
     */
    public void displayRepertoire(RepertoireNode root) {
        TreeItem<RepertoireNode> rootItem = buildTreeItem(root);
        setRoot(rootItem);
        rootItem.setExpanded(true);
    }
    
    /**
     * Build tree item recursively.
     */
    private TreeItem<RepertoireNode> buildTreeItem(RepertoireNode node) {
        TreeItem<RepertoireNode> item = new TreeItem<>(node);
        
        for (RepertoireNode child : node.getChildren()) {
            TreeItem<RepertoireNode> childItem = buildTreeItem(child);
            item.getChildren().add(childItem);
        }
        
        return item;
    }
    
    /**
     * Custom tree cell for repertoire nodes.
     */
    private static class RepertoireTreeCell extends TreeCell<RepertoireNode> {
        @Override
        protected void updateItem(RepertoireNode node, boolean empty) {
            super.updateItem(node, empty);
            
            if (empty || node == null || node.getMove().isEmpty()) {
                setText(null);
                setGraphic(null);
                setStyle("");
            } else {
                StringBuilder text = new StringBuilder();
                
                // Add move number
                if (node.isWhiteMove()) {
                    text.append(node.getMoveNumber()).append(". ");
                }
                
                // Add move
                text.append(node.getMove());
                
                // Add NAGs
                for (int nag : node.getNags()) {
                    text.append(" ").append(nagToSymbol(nag));
                }
                
                // Add comment preview
                if (node.hasComment()) {
                    String comment = node.getComment();
                    if (comment.length() > 30) {
                        comment = comment.substring(0, 27) + "...";
                    }
                    text.append(" {").append(comment).append("}");
                }
                
                setText(text.toString());
                
                // Style main line vs variations
                if (node.isMainLine()) {
                    setFont(Font.font("System", FontWeight.BOLD, 13));
                } else {
                    setFont(Font.font("System", FontWeight.NORMAL, 12));
                    setStyle("-fx-text-fill: gray;");
                }
            }
        }
        
        /**
         * Convert NAG code to symbol.
         */
        private String nagToSymbol(int nag) {
            switch (nag) {
                case 1: return "!";
                case 2: return "?";
                case 3: return "!!";
                case 4: return "??";
                case 5: return "!?";
                case 6: return "?!";
                default: return "$" + nag;
            }
        }
    }
}
