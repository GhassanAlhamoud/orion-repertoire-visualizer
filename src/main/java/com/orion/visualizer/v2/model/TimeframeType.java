package com.orion.visualizer.v2.model;

/**
 * Enum for different timeframe aggregations.
 */
public enum TimeframeType {
    MONTHLY("Month"),
    QUARTERLY("Quarter"),
    YEARLY("Year"),
    CUSTOM("Custom");
    
    private final String displayName;
    
    TimeframeType(String displayName) {
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
