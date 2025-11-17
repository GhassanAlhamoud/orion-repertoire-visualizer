package com.orion.visualizer.v3.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Container for visual annotations (highlights and arrows) on a chess position.
 */
public class VisualAnnotations {
    private final List<SquareHighlight> highlights;
    private final List<Arrow> arrows;
    
    public VisualAnnotations() {
        this.highlights = new ArrayList<>();
        this.arrows = new ArrayList<>();
    }
    
    public List<SquareHighlight> getHighlights() {
        return new ArrayList<>(highlights);
    }
    
    public void addHighlight(SquareHighlight highlight) {
        // Remove existing highlight on same square
        highlights.removeIf(h -> h.getSquare().equals(highlight.getSquare()));
        highlights.add(highlight);
    }
    
    public void removeHighlight(String square) {
        highlights.removeIf(h -> h.getSquare().equals(square));
    }
    
    public void clearHighlights() {
        highlights.clear();
    }
    
    public List<Arrow> getArrows() {
        return new ArrayList<>(arrows);
    }
    
    public void addArrow(Arrow arrow) {
        // Remove existing arrow with same from/to
        arrows.removeIf(a -> a.getFrom().equals(arrow.getFrom()) && 
                            a.getTo().equals(arrow.getTo()));
        arrows.add(arrow);
    }
    
    public void removeArrow(String from, String to) {
        arrows.removeIf(a -> a.getFrom().equals(from) && a.getTo().equals(to));
    }
    
    public void clearArrows() {
        arrows.clear();
    }
    
    public void clearAll() {
        highlights.clear();
        arrows.clear();
    }
    
    public boolean hasAnnotations() {
        return !highlights.isEmpty() || !arrows.isEmpty();
    }
    
    public boolean hasHighlights() {
        return !highlights.isEmpty();
    }
    
    public boolean hasArrows() {
        return !arrows.isEmpty();
    }
    
    /**
     * Generate PGN [%csl] command for highlights.
     */
    public String generateCslCommand() {
        if (highlights.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder("[%csl ");
        for (int i = 0; i < highlights.size(); i++) {
            if (i > 0) sb.append(",");
            SquareHighlight h = highlights.get(i);
            sb.append(h.getColor().getPgnCode()).append(h.getSquare());
        }
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * Generate PGN [%cal] command for arrows.
     */
    public String generateCalCommand() {
        if (arrows.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder("[%cal ");
        for (int i = 0; i < arrows.size(); i++) {
            if (i > 0) sb.append(",");
            Arrow a = arrows.get(i);
            sb.append(a.getColor().getPgnCode())
              .append(a.getFrom())
              .append(a.getTo());
        }
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * Parse [%csl] command and add highlights.
     */
    public void parseCslCommand(String command) {
        // Remove [%csl and ]
        String content = command.replace("[%csl", "").replace("]", "").trim();
        if (content.isEmpty()) return;
        
        String[] parts = content.split(",");
        for (String part : parts) {
            part = part.trim();
            if (part.length() >= 3) {
                char colorCode = part.charAt(0);
                String square = part.substring(1);
                AnnotationColor color = AnnotationColor.fromPgnCode(colorCode);
                if (color != null) {
                    addHighlight(new SquareHighlight(square, color));
                }
            }
        }
    }
    
    /**
     * Parse [%cal] command and add arrows.
     */
    public void parseCalCommand(String command) {
        // Remove [%cal and ]
        String content = command.replace("[%cal", "").replace("]", "").trim();
        if (content.isEmpty()) return;
        
        String[] parts = content.split(",");
        for (String part : parts) {
            part = part.trim();
            if (part.length() >= 5) {
                char colorCode = part.charAt(0);
                String from = part.substring(1, 3);
                String to = part.substring(3, 5);
                AnnotationColor color = AnnotationColor.fromPgnCode(colorCode);
                if (color != null) {
                    addArrow(new Arrow(from, to, color));
                }
            }
        }
    }
    
    @Override
    public String toString() {
        return String.format("Highlights: %d, Arrows: %d", 
            highlights.size(), arrows.size());
    }
    
    /**
     * Square highlight.
     */
    public static class SquareHighlight {
        private final String square; // e.g., "e4"
        private final AnnotationColor color;
        
        public SquareHighlight(String square, AnnotationColor color) {
            this.square = Objects.requireNonNull(square, "Square cannot be null");
            this.color = Objects.requireNonNull(color, "Color cannot be null");
        }
        
        public String getSquare() {
            return square;
        }
        
        public AnnotationColor getColor() {
            return color;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SquareHighlight that = (SquareHighlight) o;
            return Objects.equals(square, that.square) && color == that.color;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(square, color);
        }
    }
    
    /**
     * Arrow annotation.
     */
    public static class Arrow {
        private final String from; // e.g., "e2"
        private final String to;   // e.g., "e4"
        private final AnnotationColor color;
        
        public Arrow(String from, String to, AnnotationColor color) {
            this.from = Objects.requireNonNull(from, "From square cannot be null");
            this.to = Objects.requireNonNull(to, "To square cannot be null");
            this.color = Objects.requireNonNull(color, "Color cannot be null");
        }
        
        public String getFrom() {
            return from;
        }
        
        public String getTo() {
            return to;
        }
        
        public AnnotationColor getColor() {
            return color;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Arrow arrow = (Arrow) o;
            return Objects.equals(from, arrow.from) && 
                   Objects.equals(to, arrow.to) && 
                   color == arrow.color;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(from, to, color);
        }
    }
    
    /**
     * Annotation color enum.
     */
    public enum AnnotationColor {
        RED('R'),
        GREEN('G'),
        YELLOW('Y'),
        BLUE('B');
        
        private final char pgnCode;
        
        AnnotationColor(char pgnCode) {
            this.pgnCode = pgnCode;
        }
        
        public char getPgnCode() {
            return pgnCode;
        }
        
        public static AnnotationColor fromPgnCode(char code) {
            for (AnnotationColor color : values()) {
                if (color.pgnCode == code) {
                    return color;
                }
            }
            return null;
        }
    }
}
