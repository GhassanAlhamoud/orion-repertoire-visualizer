package com.orion.visualizer.v3.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single move node in a repertoire tree.
 */
public class RepertoireNode {
    private final String move; // SAN notation (e.g., "e4", "Nf3")
    private final String fen; // Position after this move
    private String comment;
    private final List<Integer> nags; // Numeric Annotation Glyphs
    private VisualAnnotations visualAnnotations;
    private RepertoireNode parent;
    private final List<RepertoireNode> children;
    private boolean isMainLine;
    
    public RepertoireNode(String move, String fen) {
        this.move = Objects.requireNonNull(move, "Move cannot be null");
        this.fen = Objects.requireNonNull(fen, "FEN cannot be null");
        this.comment = "";
        this.nags = new ArrayList<>();
        this.visualAnnotations = new VisualAnnotations();
        this.children = new ArrayList<>();
        this.isMainLine = false;
    }
    
    public String getMove() {
        return move;
    }
    
    public String getFen() {
        return fen;
    }
    
    public String getComment() {
        return comment;
    }
    
    public void setComment(String comment) {
        this.comment = comment != null ? comment : "";
    }
    
    public List<Integer> getNags() {
        return new ArrayList<>(nags);
    }
    
    public void addNag(int nag) {
        if (!nags.contains(nag)) {
            nags.add(nag);
        }
    }
    
    public void removeNag(int nag) {
        nags.remove(Integer.valueOf(nag));
    }
    
    public void clearNags() {
        nags.clear();
    }
    
    public VisualAnnotations getVisualAnnotations() {
        return visualAnnotations;
    }
    
    public void setVisualAnnotations(VisualAnnotations annotations) {
        this.visualAnnotations = annotations != null ? annotations : new VisualAnnotations();
    }
    
    public RepertoireNode getParent() {
        return parent;
    }
    
    public void setParent(RepertoireNode parent) {
        this.parent = parent;
    }
    
    public List<RepertoireNode> getChildren() {
        return new ArrayList<>(children);
    }
    
    public void addChild(RepertoireNode child) {
        if (!children.contains(child)) {
            children.add(child);
            child.setParent(this);
        }
    }
    
    public void removeChild(RepertoireNode child) {
        children.remove(child);
        child.setParent(null);
    }
    
    public boolean isMainLine() {
        return isMainLine;
    }
    
    public void setMainLine(boolean mainLine) {
        this.isMainLine = mainLine;
    }
    
    public boolean isLeaf() {
        return children.isEmpty();
    }
    
    public boolean hasComment() {
        return comment != null && !comment.trim().isEmpty();
    }
    
    public boolean hasNags() {
        return !nags.isEmpty();
    }
    
    public boolean hasVisualAnnotations() {
        return visualAnnotations.hasAnnotations();
    }
    
    /**
     * Get the main line child (first child).
     */
    public RepertoireNode getMainLineChild() {
        return children.isEmpty() ? null : children.get(0);
    }
    
    /**
     * Get variation children (all except first).
     */
    public List<RepertoireNode> getVariations() {
        if (children.size() <= 1) {
            return new ArrayList<>();
        }
        return new ArrayList<>(children.subList(1, children.size()));
    }
    
    /**
     * Promote a variation to main line.
     */
    public void promoteVariation(RepertoireNode variation) {
        int index = children.indexOf(variation);
        if (index > 0) {
            children.remove(index);
            children.add(0, variation);
            updateMainLineFlags();
        }
    }
    
    /**
     * Update main line flags for all children.
     */
    private void updateMainLineFlags() {
        for (int i = 0; i < children.size(); i++) {
            children.get(i).setMainLine(i == 0);
        }
    }
    
    /**
     * Get move number (calculated from root).
     */
    public int getMoveNumber() {
        int count = 0;
        RepertoireNode current = this;
        while (current.parent != null) {
            count++;
            current = current.parent;
        }
        return (count + 1) / 2 + 1; // Convert ply to move number
    }
    
    /**
     * Check if this is White's move.
     */
    public boolean isWhiteMove() {
        int ply = 0;
        RepertoireNode current = this;
        while (current.parent != null) {
            ply++;
            current = current.parent;
        }
        return ply % 2 == 0;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(move);
        
        // Add NAGs
        for (int nag : nags) {
            sb.append(" $").append(nag);
        }
        
        // Add comment
        if (hasComment()) {
            sb.append(" {").append(comment).append("}");
        }
        
        return sb.toString();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepertoireNode that = (RepertoireNode) o;
        return Objects.equals(move, that.move) && Objects.equals(fen, that.fen);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(move, fen);
    }
}
