package com.orion.visualizer.v3.service;

import com.orion.visualizer.v3.model.RepertoireTree;
import com.orion.visualizer.v3.parser.PGNParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Service for managing repertoire files (load, save, etc.).
 */
public class RepertoireManager {
    private final PGNParser parser;
    private RepertoireTree currentRepertoire;
    private Path currentFilePath;
    
    public RepertoireManager() {
        this.parser = new PGNParser();
        this.currentRepertoire = new RepertoireTree();
    }
    
    /**
     * Create a new empty repertoire.
     */
    public RepertoireTree createNew() {
        currentRepertoire = new RepertoireTree();
        currentFilePath = null;
        return currentRepertoire;
    }
    
    /**
     * Open a repertoire from file.
     */
    public RepertoireTree open(Path filePath) throws IOException {
        String pgn = Files.readString(filePath);
        currentRepertoire = parser.parse(pgn);
        currentFilePath = filePath;
        return currentRepertoire;
    }
    
    /**
     * Save current repertoire to file.
     */
    public void save() throws IOException {
        if (currentFilePath == null) {
            throw new IllegalStateException("No file path set. Use saveAs() instead.");
        }
        saveAs(currentFilePath);
    }
    
    /**
     * Save current repertoire to a new file.
     */
    public void saveAs(Path filePath) throws IOException {
        // Create backup if file exists
        if (Files.exists(filePath)) {
            Path backupPath = Path.of(filePath.toString() + ".bak");
            Files.copy(filePath, backupPath, StandardCopyOption.REPLACE_EXISTING);
        }
        
        // Generate PGN
        String pgn = parser.generate(currentRepertoire);
        
        // Write to temporary file first
        Path tempFile = Files.createTempFile("repertoire", ".pgn");
        Files.writeString(tempFile, pgn);
        
        // Move to target location (atomic operation)
        Files.move(tempFile, filePath, StandardCopyOption.REPLACE_EXISTING);
        
        currentFilePath = filePath;
        currentRepertoire.setModified(false);
    }
    
    /**
     * Get current repertoire.
     */
    public RepertoireTree getCurrentRepertoire() {
        return currentRepertoire;
    }
    
    /**
     * Get current file path.
     */
    public Path getCurrentFilePath() {
        return currentFilePath;
    }
    
    /**
     * Check if current repertoire has unsaved changes.
     */
    public boolean hasUnsavedChanges() {
        return currentRepertoire.isModified();
    }
    
    /**
     * Set current repertoire as modified.
     */
    public void setModified(boolean modified) {
        currentRepertoire.setModified(modified);
    }
    
    /**
     * Import PGN from string.
     */
    public RepertoireTree importPGN(String pgn) throws IOException {
        currentRepertoire = parser.parse(pgn);
        currentFilePath = null;
        return currentRepertoire;
    }
    
    /**
     * Export current repertoire as PGN string.
     */
    public String exportPGN() {
        return parser.generate(currentRepertoire);
    }
    
    /**
     * Get file name without extension.
     */
    public String getFileName() {
        if (currentFilePath == null) {
            return "Untitled";
        }
        String name = currentFilePath.getFileName().toString();
        int dotIndex = name.lastIndexOf('.');
        return dotIndex > 0 ? name.substring(0, dotIndex) : name;
    }
}
