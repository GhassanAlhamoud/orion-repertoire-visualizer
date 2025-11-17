package com.orion.visualizer.controller;

/**
 * Manager for switching between different application modes.
 */
public class ModeManager {
    
    /**
     * Application modes.
     */
    public enum Mode {
        V1_PERSONAL_ANALYSIS("Personal Analysis"),
        V2_HISTORICAL_ANALYSIS("Historical Analysis"),
        V3_REPERTOIRE_EDITOR("Repertoire Editor");
        
        private final String displayName;
        
        Mode(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        @Override
        public String toString() {
            return displayName;
        }
    }
    
    private Mode currentMode;
    private ModeChangeListener listener;
    
    public ModeManager() {
        this.currentMode = Mode.V1_PERSONAL_ANALYSIS;
    }
    
    /**
     * Get current mode.
     */
    public Mode getCurrentMode() {
        return currentMode;
    }
    
    /**
     * Switch to a different mode.
     */
    public void switchMode(Mode newMode) {
        if (newMode == currentMode) {
            return; // Already in this mode
        }
        
        Mode oldMode = currentMode;
        currentMode = newMode;
        
        if (listener != null) {
            listener.onModeChanged(oldMode, newMode);
        }
    }
    
    /**
     * Set mode change listener.
     */
    public void setModeChangeListener(ModeChangeListener listener) {
        this.listener = listener;
    }
    
    /**
     * Interface for mode change notifications.
     */
    public interface ModeChangeListener {
        void onModeChanged(Mode oldMode, Mode newMode);
    }
}
