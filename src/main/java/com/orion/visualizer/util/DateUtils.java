package com.orion.visualizer.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class for date parsing and formatting in chess PGN format.
 */
public class DateUtils {
    private static final DateTimeFormatter PGN_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    private static final DateTimeFormatter[] FALLBACK_FORMATTERS = {
        DateTimeFormatter.ofPattern("yyyy.MM.d"),
        DateTimeFormatter.ofPattern("yyyy.M.dd"),
        DateTimeFormatter.ofPattern("yyyy.M.d"),
        DateTimeFormatter.ISO_LOCAL_DATE
    };

    /**
     * Parse a PGN date string to LocalDate.
     * PGN dates are in format "yyyy.MM.dd" but may have "??" for unknown parts.
     * 
     * @param pgnDate The date string from PGN (e.g., "2023.05.15" or "2023.??.??")
     * @return LocalDate or null if date cannot be parsed
     */
    public static LocalDate parsePgnDate(String pgnDate) {
        if (pgnDate == null || pgnDate.trim().isEmpty()) {
            return null;
        }

        // Handle unknown dates
        if (pgnDate.contains("?")) {
            // Try to extract at least the year
            String[] parts = pgnDate.split("\\.");
            if (parts.length > 0 && !parts[0].contains("?")) {
                try {
                    int year = Integer.parseInt(parts[0]);
                    // Use January 1st as default for partial dates
                    return LocalDate.of(year, 1, 1);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        }

        // Try standard PGN format first
        try {
            return LocalDate.parse(pgnDate, PGN_DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            // Try fallback formatters
            for (DateTimeFormatter formatter : FALLBACK_FORMATTERS) {
                try {
                    return LocalDate.parse(pgnDate, formatter);
                } catch (DateTimeParseException ex) {
                    // Continue to next formatter
                }
            }
        }

        return null;
    }

    /**
     * Format a LocalDate to PGN format.
     */
    public static String formatPgnDate(LocalDate date) {
        if (date == null) {
            return "????.??.??";
        }
        return date.format(PGN_DATE_FORMATTER);
    }

    /**
     * Get a human-readable date range string.
     */
    public static String formatDateRange(LocalDate start, LocalDate end) {
        if (start == null && end == null) {
            return "All Time";
        }
        if (start == null) {
            return "Until " + end;
        }
        if (end == null) {
            return "From " + start;
        }
        return start + " to " + end;
    }
}
