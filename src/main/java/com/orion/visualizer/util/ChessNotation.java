package com.orion.visualizer.util;

/**
 * Utility class for chess notation conversions and formatting.
 */
public class ChessNotation {
    
    /**
     * Unicode chess piece symbols.
     */
    public static final String WHITE_KING = "♔";
    public static final String WHITE_QUEEN = "♕";
    public static final String WHITE_ROOK = "♖";
    public static final String WHITE_BISHOP = "♗";
    public static final String WHITE_KNIGHT = "♘";
    public static final String WHITE_PAWN = "♙";
    
    public static final String BLACK_KING = "♚";
    public static final String BLACK_QUEEN = "♛";
    public static final String BLACK_ROOK = "♜";
    public static final String BLACK_BISHOP = "♝";
    public static final String BLACK_KNIGHT = "♞";
    public static final String BLACK_PAWN = "♟";

    /**
     * Get Unicode symbol for a piece.
     * 
     * @param piece Piece character (K, Q, R, B, N, P for white; k, q, r, b, n, p for black)
     * @return Unicode chess symbol
     */
    public static String getPieceSymbol(char piece) {
        switch (piece) {
            case 'K': return WHITE_KING;
            case 'Q': return WHITE_QUEEN;
            case 'R': return WHITE_ROOK;
            case 'B': return WHITE_BISHOP;
            case 'N': return WHITE_KNIGHT;
            case 'P': return WHITE_PAWN;
            case 'k': return BLACK_KING;
            case 'q': return BLACK_QUEEN;
            case 'r': return BLACK_ROOK;
            case 'b': return BLACK_BISHOP;
            case 'n': return BLACK_KNIGHT;
            case 'p': return BLACK_PAWN;
            default: return "";
        }
    }

    /**
     * Convert file letter (a-h) to index (0-7).
     */
    public static int fileToIndex(char file) {
        return file - 'a';
    }

    /**
     * Convert rank number (1-8) to index (0-7).
     */
    public static int rankToIndex(char rank) {
        return rank - '1';
    }

    /**
     * Convert file index (0-7) to letter (a-h).
     */
    public static char indexToFile(int index) {
        return (char) ('a' + index);
    }

    /**
     * Convert rank index (0-7) to number (1-8).
     */
    public static char indexToRank(int index) {
        return (char) ('1' + index);
    }

    /**
     * Convert algebraic notation to square index.
     * e.g., "e4" -> (4, 3)
     */
    public static int[] algebraicToIndex(String square) {
        if (square == null || square.length() != 2) {
            throw new IllegalArgumentException("Invalid square notation: " + square);
        }
        int file = fileToIndex(square.charAt(0));
        int rank = rankToIndex(square.charAt(1));
        return new int[]{file, rank};
    }

    /**
     * Convert square index to algebraic notation.
     * e.g., (4, 3) -> "e4"
     */
    public static String indexToAlgebraic(int file, int rank) {
        return "" + indexToFile(file) + indexToRank(rank);
    }

    /**
     * Clean up a move string by removing annotations and comments.
     */
    public static String cleanMove(String move) {
        if (move == null) {
            return null;
        }
        // Remove check/checkmate symbols, annotations, etc.
        return move.replaceAll("[+#!?]", "").trim();
    }

    /**
     * Format move number with move (e.g., "1. e4" or "1... e5").
     */
    public static String formatMove(int moveNumber, String move, boolean isWhite) {
        if (isWhite) {
            return moveNumber + ". " + move;
        } else {
            return moveNumber + "... " + move;
        }
    }

    /**
     * Extract the starting position FEN.
     */
    public static String getStartingFen() {
        return "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    }
}
