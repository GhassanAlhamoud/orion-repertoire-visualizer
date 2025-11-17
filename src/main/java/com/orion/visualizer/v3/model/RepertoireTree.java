package com.orion.visualizer.v3.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a complete chess opening repertoire as a tree structure.
 */
public class RepertoireTree {
    private static final String STARTING_FEN = 
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    
    private final Map<String, String> headers;
    private final RepertoireNode root;
    private boolean modified;
    
    public RepertoireTree() {
        this.headers = new HashMap<>();
        this.root = new RepertoireNode("", STARTING_FEN);
        this.modified = false;
        initializeDefaultHeaders();
    }
    
    /**
     * Initialize default PGN headers.
     */
    private void initializeDefaultHeaders() {
        headers.put("Event", "Repertoire");
        headers.put("Site", "?");
        headers.put("Date", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
        headers.put("Round", "?");
        headers.put("White", "?");
        headers.put("Black", "?");
        headers.put("Result", "*");
    }
    
    public Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }
    
    public String getHeader(String key) {
        return headers.get(key);
    }
    
    public void setHeader(String key, String value) {
        headers.put(key, value);
        modified = true;
    }
    
    public void setHeaders(Map<String, String> headers) {
        this.headers.clear();
        this.headers.putAll(headers);
        modified = true;
    }
    
    public RepertoireNode getRoot() {
        return root;
    }
    
    public boolean isModified() {
        return modified;
    }
    
    public void setModified(boolean modified) {
        this.modified = modified;
    }
    
    /**
     * Find a node by FEN position.
     */
    public RepertoireNode findNodeByFen(String fen) {
        return findNodeByFenRecursive(root, fen);
    }
    
    private RepertoireNode findNodeByFenRecursive(RepertoireNode node, String fen) {
        if (node.getFen().equals(fen)) {
            return node;
        }
        
        for (RepertoireNode child : node.getChildren()) {
            RepertoireNode found = findNodeByFenRecursive(child, fen);
            if (found != null) {
                return found;
            }
        }
        
        return null;
    }
    
    /**
     * Get total number of moves in the repertoire.
     */
    public int getTotalMoves() {
        return countMovesRecursive(root);
    }
    
    private int countMovesRecursive(RepertoireNode node) {
        int count = node.getParent() != null ? 1 : 0; // Don't count root
        for (RepertoireNode child : node.getChildren()) {
            count += countMovesRecursive(child);
        }
        return count;
    }
    
    /**
     * Get maximum depth of the repertoire tree.
     */
    public int getMaxDepth() {
        return getDepthRecursive(root, 0);
    }
    
    private int getDepthRecursive(RepertoireNode node, int currentDepth) {
        if (node.isLeaf()) {
            return currentDepth;
        }
        
        int maxDepth = currentDepth;
        for (RepertoireNode child : node.getChildren()) {
            int childDepth = getDepthRecursive(child, currentDepth + 1);
            maxDepth = Math.max(maxDepth, childDepth);
        }
        return maxDepth;
    }
    
    /**
     * Clear all moves (reset to starting position).
     */
    public void clear() {
        root.getChildren().clear();
        modified = true;
    }
    
    @Override
    public String toString() {
        return String.format("Repertoire: %s vs %s - %d moves, depth %d",
            getHeader("White"), getHeader("Black"), getTotalMoves(), getMaxDepth());
    }
}
