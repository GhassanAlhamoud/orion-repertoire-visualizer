package com.orion.visualizer.v3.service;

import com.orion.visualizer.v3.model.RepertoireNode;
import com.orion.visualizer.v3.model.RepertoireTree;

/**
 * Service for editing repertoire tree structure.
 */
public class TreeEditorService {
    private static final String STARTING_FEN = 
        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    
    /**
     * Add a move to the tree at the specified position.
     * If the move already exists, return the existing node.
     */
    public RepertoireNode addMove(RepertoireNode parent, String move, String resultingFen) {
        // Check if move already exists
        for (RepertoireNode child : parent.getChildren()) {
            if (child.getMove().equals(move)) {
                return child; // Move already exists, return it
            }
        }
        
        // Create new node
        RepertoireNode newNode = new RepertoireNode(move, resultingFen);
        parent.addChild(newNode);
        
        // Set as main line if it's the first child
        if (parent.getChildren().size() == 1) {
            newNode.setMainLine(true);
        }
        
        return newNode;
    }
    
    /**
     * Delete a move and all its descendants.
     */
    public boolean deleteMove(RepertoireNode node) {
        if (node.getParent() == null) {
            return false; // Cannot delete root
        }
        
        RepertoireNode parent = node.getParent();
        parent.removeChild(node);
        
        // Update main line flags
        if (!parent.getChildren().isEmpty()) {
            parent.getChildren().get(0).setMainLine(true);
        }
        
        return true;
    }
    
    /**
     * Promote a variation to the main line.
     */
    public void promoteVariation(RepertoireNode node) {
        if (node.getParent() == null) {
            return; // Root has no parent
        }
        
        node.getParent().promoteVariation(node);
    }
    
    /**
     * Add a comment to a move.
     */
    public void addComment(RepertoireNode node, String comment) {
        node.setComment(comment);
    }
    
    /**
     * Add a NAG (Numeric Annotation Glyph) to a move.
     */
    public void addNAG(RepertoireNode node, int nag) {
        node.addNag(nag);
    }
    
    /**
     * Remove a NAG from a move.
     */
    public void removeNAG(RepertoireNode node, int nag) {
        node.removeNag(nag);
    }
    
    /**
     * Clear all NAGs from a move.
     */
    public void clearNAGs(RepertoireNode node) {
        node.clearNags();
    }
    
    /**
     * Find or create a path of moves from root.
     */
    public RepertoireNode findOrCreatePath(RepertoireTree tree, String[] moves, String[] fens) {
        if (moves.length != fens.length) {
            throw new IllegalArgumentException("Moves and FENs arrays must have same length");
        }
        
        RepertoireNode current = tree.getRoot();
        
        for (int i = 0; i < moves.length; i++) {
            current = addMove(current, moves[i], fens[i]);
        }
        
        return current;
    }
    
    /**
     * Get the path from root to a node as a list of moves.
     */
    public String[] getMovePath(RepertoireNode node) {
        if (node.getParent() == null) {
            return new String[0]; // Root
        }
        
        // Count depth
        int depth = 0;
        RepertoireNode current = node;
        while (current.getParent() != null) {
            depth++;
            current = current.getParent();
        }
        
        // Build path
        String[] path = new String[depth];
        current = node;
        for (int i = depth - 1; i >= 0; i--) {
            path[i] = current.getMove();
            current = current.getParent();
        }
        
        return path;
    }
    
    /**
     * Check if a move is legal (simplified - would use chess engine in production).
     */
    public boolean isLegalMove(String fen, String move) {
        // TODO: Integrate with chess engine for move validation
        // For now, accept all moves
        return true;
    }
    
    /**
     * Calculate resulting FEN after a move (simplified).
     */
    public String calculateFEN(String currentFen, String move) {
        // TODO: Integrate with chess engine for FEN calculation
        // For now, return a placeholder
        return currentFen; // Placeholder
    }
}
