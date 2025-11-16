package com.orion.visualizer.service;

import com.oriondb.core.OrionDatabase;
import com.oriondb.model.Game;
import com.oriondb.util.ProgressCallback;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Service for managing OrionDB database operations.
 * Singleton pattern to ensure single database instance.
 */
public class DatabaseService {
    private static DatabaseService instance;
    
    private OrionDatabase database;
    private File currentDatabaseFile;

    private DatabaseService() {
    }

    public static synchronized DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }

    /**
     * Create a new database from a PGN file.
     */
    public OrionDatabase.ImportStats createDatabaseFromPgn(File pgnFile, File outputFile, 
                                                           ProgressCallback callback) throws IOException {
        // Close existing database if open
        closeDatabase();
        
        // Create new database with position indexing enabled
        OrionDatabase.ImportStats stats = OrionDatabase.createFromPgn(
            pgnFile, outputFile, callback, true, false
        );
        
        // Load the newly created database
        loadDatabase(outputFile);
        
        return stats;
    }

    /**
     * Load an existing database.
     */
    public void loadDatabase(File dbFile) throws IOException, ClassNotFoundException {
        // Close existing database if open
        closeDatabase();
        
        database = OrionDatabase.load(dbFile);
        currentDatabaseFile = dbFile;
    }

    /**
     * Close the current database.
     */
    public void closeDatabase() {
        if (database != null) {
            try {
                database.close();
            } catch (IOException e) {
                System.err.println("Error closing database: " + e.getMessage());
            }
            database = null;
            currentDatabaseFile = null;
        }
    }

    /**
     * Check if a database is currently loaded.
     */
    public boolean isDatabaseLoaded() {
        return database != null;
    }

    /**
     * Get the current database instance.
     */
    public OrionDatabase getDatabase() {
        return database;
    }

    /**
     * Get the current database file.
     */
    public File getCurrentDatabaseFile() {
        return currentDatabaseFile;
    }

    /**
     * Search for games by player name.
     */
    public List<Game> searchByPlayer(String playerName) throws IOException {
        if (database == null) {
            throw new IllegalStateException("No database loaded");
        }
        return database.search()
            .withPlayer(playerName)
            .execute();
    }

    /**
     * Search for games by player name and side.
     */
    public List<Game> searchByPlayerAndSide(String playerName, boolean asWhite) throws IOException {
        if (database == null) {
            throw new IllegalStateException("No database loaded");
        }
        
        if (asWhite) {
            return database.search()
                .withWhite(playerName)
                .execute();
        } else {
            return database.search()
                .withBlack(playerName)
                .execute();
        }
    }

    /**
     * Get all games from the database.
     */
    public List<Game> getAllGames() throws IOException {
        if (database == null) {
            throw new IllegalStateException("No database loaded");
        }
        return database.search().execute();
    }

    /**
     * Get a game by ID.
     */
    public Game getGameById(int gameId) throws IOException {
        if (database == null) {
            throw new IllegalStateException("No database loaded");
        }
        return database.getGameById(gameId);
    }

    /**
     * Get database statistics.
     */
    public String getDatabaseStats() {
        if (database == null) {
            return "No database loaded";
        }
        return database.getStats();
    }

    /**
     * Get total game count.
     */
    public int getGameCount() {
        if (database == null) {
            return 0;
        }
        return database.getGameCount();
    }

    /**
     * Find unique player names in the database.
     * This is a simplified implementation - for large databases, 
     * this should be optimized with an index.
     */
    public List<String> findPlayerNames(String searchTerm) throws IOException {
        if (database == null) {
            throw new IllegalStateException("No database loaded");
        }
        
        // For now, return games matching the search term
        // In a production system, this would query a player name index
        List<Game> games = database.search()
            .withPlayer(searchTerm)
            .execute();
        
        // Extract unique player names
        return games.stream()
            .flatMap(game -> java.util.stream.Stream.of(game.getWhite(), game.getBlack()))
            .distinct()
            .filter(name -> name.toLowerCase().contains(searchTerm.toLowerCase()))
            .sorted()
            .limit(20)
            .toList();
    }
}
