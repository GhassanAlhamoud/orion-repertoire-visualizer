package com.orion.visualizer.v2.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a historical chess player profile with metadata.
 */
public class PlayerProfile {
    private final String name;
    private final String fideId;
    private final LocalDate birthDate;
    private final String nationality;
    private final int peakRating;
    private final LocalDate careerStart;
    private final LocalDate careerEnd;
    private final int totalGames;
    
    public PlayerProfile(String name, String fideId, LocalDate birthDate,
                        String nationality, int peakRating,
                        LocalDate careerStart, LocalDate careerEnd,
                        int totalGames) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.fideId = fideId;
        this.birthDate = birthDate;
        this.nationality = nationality;
        this.peakRating = peakRating;
        this.careerStart = careerStart;
        this.careerEnd = careerEnd;
        this.totalGames = totalGames;
    }
    
    public String getName() {
        return name;
    }
    
    public String getFideId() {
        return fideId;
    }
    
    public LocalDate getBirthDate() {
        return birthDate;
    }
    
    public String getNationality() {
        return nationality;
    }
    
    public int getPeakRating() {
        return peakRating;
    }
    
    public LocalDate getCareerStart() {
        return careerStart;
    }
    
    public LocalDate getCareerEnd() {
        return careerEnd;
    }
    
    public int getTotalGames() {
        return totalGames;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerProfile that = (PlayerProfile) o;
        return Objects.equals(name, that.name) && Objects.equals(fideId, that.fideId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name, fideId);
    }
    
    @Override
    public String toString() {
        return String.format("%s (%s) - Peak: %d, Games: %d",
            name, nationality, peakRating, totalGames);
    }
}
