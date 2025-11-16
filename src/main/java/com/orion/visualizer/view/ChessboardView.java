package com.orion.visualizer.view;

import com.orion.visualizer.service.ChessEngineService;
import com.orion.visualizer.util.ChessNotation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

/**
 * JavaFX component for displaying a chess board.
 */
public class ChessboardView extends GridPane {
    private static final int SQUARE_SIZE = 60;
    private static final Color LIGHT_SQUARE = Color.rgb(240, 217, 181);
    private static final Color DARK_SQUARE = Color.rgb(181, 136, 99);
    private static final Color HIGHLIGHT_COLOR = Color.rgb(255, 255, 0, 0.5);
    
    private final SquarePane[][] squares;
    private ChessEngineService engine;
    private int highlightedFile = -1;
    private int highlightedRank = -1;

    public ChessboardView() {
        this.squares = new SquarePane[8][8];
        this.engine = new ChessEngineService();
        
        initializeBoard();
        updateBoard();
    }

    /**
     * Initialize the board grid.
     */
    private void initializeBoard() {
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                // Create square (flip rank for display: rank 7 at top, rank 0 at bottom)
                int displayRank = 7 - rank;
                boolean isLight = (file + displayRank) % 2 == 0;
                Color squareColor = isLight ? LIGHT_SQUARE : DARK_SQUARE;
                
                SquarePane square = new SquarePane(file, displayRank, squareColor);
                squares[file][displayRank] = square;
                
                // Add to grid (rank 0 at bottom, so use rank index directly)
                add(square, file, rank);
            }
        }
        
        setHgap(0);
        setVgap(0);
    }

    /**
     * Update board display from current engine state.
     */
    public void updateBoard() {
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                Character piece = engine.getPieceAt(file, rank);
                squares[file][rank].setPiece(piece);
                
                // Update highlight
                boolean highlighted = (file == highlightedFile && rank == highlightedRank);
                squares[file][rank].setHighlighted(highlighted);
            }
        }
    }

    /**
     * Set the chess engine to display.
     */
    public void setEngine(ChessEngineService engine) {
        this.engine = engine;
        updateBoard();
    }

    /**
     * Get the current engine.
     */
    public ChessEngineService getEngine() {
        return engine;
    }

    /**
     * Highlight a specific square.
     */
    public void highlightSquare(int file, int rank) {
        this.highlightedFile = file;
        this.highlightedRank = rank;
        updateBoard();
    }

    /**
     * Clear highlight.
     */
    public void clearHighlight() {
        this.highlightedFile = -1;
        this.highlightedRank = -1;
        updateBoard();
    }

    /**
     * Reset board to starting position.
     */
    public void reset() {
        engine.reset();
        clearHighlight();
        updateBoard();
    }

    /**
     * Individual square pane.
     */
    private static class SquarePane extends StackPane {
        private final int file;
        private final int rank;
        private final Rectangle background;
        private final Rectangle highlight;
        private final Label pieceLabel;
        private final Color baseColor;

        public SquarePane(int file, int rank, Color color) {
            this.file = file;
            this.rank = rank;
            this.baseColor = color;
            
            // Background
            background = new Rectangle(SQUARE_SIZE, SQUARE_SIZE);
            background.setFill(color);
            
            // Highlight overlay
            highlight = new Rectangle(SQUARE_SIZE, SQUARE_SIZE);
            highlight.setFill(HIGHLIGHT_COLOR);
            highlight.setVisible(false);
            
            // Piece label
            pieceLabel = new Label();
            pieceLabel.setFont(Font.font("Arial Unicode MS", 40));
            pieceLabel.setAlignment(Pos.CENTER);
            
            getChildren().addAll(background, highlight, pieceLabel);
            
            setPrefSize(SQUARE_SIZE, SQUARE_SIZE);
            setMinSize(SQUARE_SIZE, SQUARE_SIZE);
            setMaxSize(SQUARE_SIZE, SQUARE_SIZE);
        }

        public void setPiece(Character piece) {
            if (piece == null) {
                pieceLabel.setText("");
            } else {
                pieceLabel.setText(ChessNotation.getPieceSymbol(piece));
            }
        }

        public void setHighlighted(boolean highlighted) {
            highlight.setVisible(highlighted);
        }

        public int getFile() {
            return file;
        }

        public int getRank() {
            return rank;
        }
    }
}
