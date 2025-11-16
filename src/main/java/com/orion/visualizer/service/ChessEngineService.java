package com.orion.visualizer.service;

import com.oriondb.chess.ChessBoard;
import com.oriondb.chess.Piece;
import com.oriondb.chess.Square;
import com.oriondb.model.Position;
import com.orion.visualizer.util.ChessNotation;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing chess board state and move generation.
 * Wraps OrionDB's ChessBoard functionality.
 */
public class ChessEngineService {
    private ChessBoard board;
    private List<String> moveHistory;

    public ChessEngineService() {
        reset();
    }

    /**
     * Reset the board to starting position.
     */
    public void reset() {
        board = new ChessBoard();
        moveHistory = new ArrayList<>();
    }

    /**
     * Make a move on the board.
     * 
     * @param moveStr Move in SAN notation
     * @return true if move was successful
     */
    public boolean makeMove(String moveStr) {
        try {
            if (board.applyMove(moveStr)) {
                moveHistory.add(moveStr);
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error making move " + moveStr + ": " + e.getMessage());
        }
        return false;
    }

    /**
     * Make a sequence of moves.
     */
    public boolean makeMoves(List<String> moves) {
        for (String move : moves) {
            if (!makeMove(move)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get current board FEN.
     */
    public String getFen() {
        return board.getPosition().toFen();
    }

    /**
     * Get the current board.
     */
    public ChessBoard getBoard() {
        return board;
    }

    /**
     * Get current position.
     */
    public Position getPosition() {
        return board.getPosition();
    }

    /**
     * Get move history.
     */
    public List<String> getMoveHistory() {
        return new ArrayList<>(moveHistory);
    }

    /**
     * Set board to a specific FEN position.
     */
    public void setPosition(String fen) {
        try {
            Position position = Position.fromFen(fen);
            board = new ChessBoard(position);
            moveHistory.clear();
        } catch (Exception e) {
            System.err.println("Error setting FEN position: " + e.getMessage());
            reset();
        }
    }

    /**
     * Get piece at a specific square.
     * 
     * @param file File index (0-7)
     * @param rank Rank index (0-7)
     * @return Piece character or null if empty
     */
    public Character getPieceAt(int file, int rank) {
        try {
            int square = rank * 8 + file;
            int piece = board.getPosition().getPiece(square);
            
            if (piece == Piece.NONE) {
                return null;
            }
            
            return pieceToChar(piece);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convert OrionDB piece constant to character.
     */
    private char pieceToChar(int piece) {
        int type = Piece.type(piece);
        boolean isWhite = Piece.isWhite(piece);
        
        char baseChar;
        switch (type) {
            case 0: baseChar = 'P'; break; // Pawn
            case 1: baseChar = 'N'; break; // Knight
            case 2: baseChar = 'B'; break; // Bishop
            case 3: baseChar = 'R'; break; // Rook
            case 4: baseChar = 'Q'; break; // Queen
            case 5: baseChar = 'K'; break; // King
            default: return ' ';
        }
        
        return isWhite ? baseChar : Character.toLowerCase(baseChar);
    }

    /**
     * Check if it's white's turn.
     */
    public boolean isWhiteToMove() {
        return board.getPosition().getSideToMove() == Piece.WHITE;
    }

    /**
     * Get the current move number.
     */
    public int getMoveNumber() {
        return board.getPosition().getFullMoveNumber();
    }

    /**
     * Clone this engine service to create an independent copy.
     */
    public ChessEngineService clone() {
        ChessEngineService cloned = new ChessEngineService();
        cloned.setPosition(this.getFen());
        cloned.moveHistory = new ArrayList<>(this.moveHistory);
        return cloned;
    }
}
