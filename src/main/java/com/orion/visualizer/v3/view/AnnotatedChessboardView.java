package com.orion.visualizer.v3.view;

import com.orion.visualizer.v3.model.VisualAnnotations;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Chessboard view with support for visual annotations (highlights and arrows).
 */
public class AnnotatedChessboardView extends Pane {
    private static final int BOARD_SIZE = 480;
    private static final int SQUARE_SIZE = BOARD_SIZE / 8;
    private static final Color LIGHT_SQUARE = Color.web("#F0D9B5");
    private static final Color DARK_SQUARE = Color.web("#B58863");
    
    private final Canvas boardCanvas;
    private final Canvas annotationCanvas;
    private final Canvas pieceCanvas;
    
    private VisualAnnotations currentAnnotations;
    private String currentFen;
    
    public AnnotatedChessboardView() {
        // Create layered canvases
        this.boardCanvas = new Canvas(BOARD_SIZE, BOARD_SIZE);
        this.annotationCanvas = new Canvas(BOARD_SIZE, BOARD_SIZE);
        this.pieceCanvas = new Canvas(BOARD_SIZE, BOARD_SIZE);
        
        this.currentAnnotations = new VisualAnnotations();
        this.currentFen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
        
        // Add canvases to pane
        getChildren().addAll(boardCanvas, annotationCanvas, pieceCanvas);
        
        // Set size
        setPrefSize(BOARD_SIZE, BOARD_SIZE);
        setMinSize(BOARD_SIZE, BOARD_SIZE);
        setMaxSize(BOARD_SIZE, BOARD_SIZE);
        
        // Initial render
        renderBoard();
        renderPosition();
    }
    
    /**
     * Render the board squares.
     */
    private void renderBoard() {
        GraphicsContext gc = boardCanvas.getGraphicsContext2D();
        
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                boolean isLight = (rank + file) % 2 == 0;
                gc.setFill(isLight ? LIGHT_SQUARE : DARK_SQUARE);
                gc.fillRect(file * SQUARE_SIZE, rank * SQUARE_SIZE, 
                          SQUARE_SIZE, SQUARE_SIZE);
            }
        }
        
        // Draw coordinates
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(12));
        
        // Files (a-h)
        for (int file = 0; file < 8; file++) {
            char letter = (char) ('a' + file);
            gc.fillText(String.valueOf(letter), 
                       file * SQUARE_SIZE + 5, 
                       BOARD_SIZE - 5);
        }
        
        // Ranks (1-8)
        for (int rank = 0; rank < 8; rank++) {
            int number = 8 - rank;
            gc.fillText(String.valueOf(number), 
                       BOARD_SIZE - 15, 
                       rank * SQUARE_SIZE + 15);
        }
    }
    
    /**
     * Render visual annotations.
     */
    private void renderAnnotations() {
        GraphicsContext gc = annotationCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, BOARD_SIZE, BOARD_SIZE);
        
        if (currentAnnotations == null) return;
        
        // Render highlights
        for (VisualAnnotations.SquareHighlight highlight : currentAnnotations.getHighlights()) {
            renderHighlight(gc, highlight);
        }
        
        // Render arrows
        for (VisualAnnotations.Arrow arrow : currentAnnotations.getArrows()) {
            renderArrow(gc, arrow);
        }
    }
    
    /**
     * Render a square highlight.
     */
    private void renderHighlight(GraphicsContext gc, VisualAnnotations.SquareHighlight highlight) {
        int[] coords = squareToCoords(highlight.getSquare());
        if (coords == null) return;
        
        Color color = getAnnotationColor(highlight.getColor());
        
        // Fill with semi-transparent color
        gc.setFill(color.deriveColor(0, 1, 1, 0.4));
        gc.fillRect(coords[0] * SQUARE_SIZE, coords[1] * SQUARE_SIZE, 
                   SQUARE_SIZE, SQUARE_SIZE);
        
        // Draw border
        gc.setStroke(color);
        gc.setLineWidth(3);
        gc.strokeRect(coords[0] * SQUARE_SIZE + 2, coords[1] * SQUARE_SIZE + 2, 
                     SQUARE_SIZE - 4, SQUARE_SIZE - 4);
    }
    
    /**
     * Render an arrow.
     */
    private void renderArrow(GraphicsContext gc, VisualAnnotations.Arrow arrow) {
        int[] fromCoords = squareToCoords(arrow.getFrom());
        int[] toCoords = squareToCoords(arrow.getTo());
        if (fromCoords == null || toCoords == null) return;
        
        Color color = getAnnotationColor(arrow.getColor());
        
        // Calculate center points
        double fromX = (fromCoords[0] + 0.5) * SQUARE_SIZE;
        double fromY = (fromCoords[1] + 0.5) * SQUARE_SIZE;
        double toX = (toCoords[0] + 0.5) * SQUARE_SIZE;
        double toY = (toCoords[1] + 0.5) * SQUARE_SIZE;
        
        // Draw arrow line
        gc.setStroke(color.deriveColor(0, 1, 1, 0.8));
        gc.setLineWidth(8);
        gc.strokeLine(fromX, fromY, toX, toY);
        
        // Draw arrow head
        drawArrowHead(gc, fromX, fromY, toX, toY, color);
    }
    
    /**
     * Draw arrow head.
     */
    private void drawArrowHead(GraphicsContext gc, double fromX, double fromY, 
                              double toX, double toY, Color color) {
        double angle = Math.atan2(toY - fromY, toX - fromX);
        double headLength = 20;
        double headWidth = 10;
        
        double tipX = toX;
        double tipY = toY;
        
        double baseX = toX - headLength * Math.cos(angle);
        double baseY = toY - headLength * Math.sin(angle);
        
        double leftX = baseX + headWidth * Math.cos(angle + Math.PI / 2);
        double leftY = baseY + headWidth * Math.sin(angle + Math.PI / 2);
        
        double rightX = baseX + headWidth * Math.cos(angle - Math.PI / 2);
        double rightY = baseY + headWidth * Math.sin(angle - Math.PI / 2);
        
        gc.setFill(color.deriveColor(0, 1, 1, 0.8));
        gc.fillPolygon(
            new double[]{tipX, leftX, rightX},
            new double[]{tipY, leftY, rightY},
            3
        );
    }
    
    /**
     * Render chess pieces (simplified).
     */
    private void renderPosition() {
        GraphicsContext gc = pieceCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, BOARD_SIZE, BOARD_SIZE);
        
        // TODO: Parse FEN and render pieces
        // For now, just show starting position symbols
        gc.setFont(javafx.scene.text.Font.font("Arial Unicode MS", 40));
        
        // Example: Render some pieces
        String[] pieces = {"♜", "♞", "♝", "♛", "♚", "♝", "♞", "♜"};
        for (int i = 0; i < 8; i++) {
            gc.fillText(pieces[i], i * SQUARE_SIZE + 15, SQUARE_SIZE - 10);
            gc.fillText("♟", i * SQUARE_SIZE + 15, 2 * SQUARE_SIZE - 10);
        }
        
        String[] whitePieces = {"♖", "♘", "♗", "♕", "♔", "♗", "♘", "♖"};
        for (int i = 0; i < 8; i++) {
            gc.fillText("♙", i * SQUARE_SIZE + 15, 7 * SQUARE_SIZE - 10);
            gc.fillText(whitePieces[i], i * SQUARE_SIZE + 15, 8 * SQUARE_SIZE - 10);
        }
    }
    
    /**
     * Set visual annotations to display.
     */
    public void setAnnotations(VisualAnnotations annotations) {
        this.currentAnnotations = annotations != null ? annotations : new VisualAnnotations();
        renderAnnotations();
    }
    
    /**
     * Set position from FEN.
     */
    public void setPosition(String fen) {
        this.currentFen = fen;
        renderPosition();
    }
    
    /**
     * Convert square notation to board coordinates.
     */
    private int[] squareToCoords(String square) {
        if (square == null || square.length() != 2) return null;
        
        int file = square.charAt(0) - 'a'; // 0-7
        int rank = '8' - square.charAt(1); // 0-7 (flipped)
        
        if (file < 0 || file > 7 || rank < 0 || rank > 7) return null;
        
        return new int[]{file, rank};
    }
    
    /**
     * Get JavaFX color from annotation color.
     */
    private Color getAnnotationColor(VisualAnnotations.AnnotationColor color) {
        switch (color) {
            case RED: return Color.RED;
            case GREEN: return Color.GREEN;
            case YELLOW: return Color.YELLOW;
            case BLUE: return Color.BLUE;
            default: return Color.GRAY;
        }
    }
}
