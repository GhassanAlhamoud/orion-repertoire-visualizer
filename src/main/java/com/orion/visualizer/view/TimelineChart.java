package com.orion.visualizer.view;

import com.orion.visualizer.model.GameReference;
import com.orion.visualizer.model.OpeningTreeNode;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Timeline chart for visualizing opening usage and performance over time.
 */
public class TimelineChart extends LineChart<String, Number> {
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final DateTimeFormatter YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy");
    
    private TimeGranularity granularity;

    public enum TimeGranularity {
        MONTH("Month"),
        YEAR("Year");
        
        private final String displayName;
        
        TimeGranularity(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }

    public TimelineChart() {
        super(new CategoryAxis(), new NumberAxis());
        this.granularity = TimeGranularity.YEAR;
        
        setTitle("Opening Usage Over Time");
        setLegendVisible(true);
        setCreateSymbols(true);
        
        CategoryAxis xAxis = (CategoryAxis) getXAxis();
        xAxis.setLabel("Time Period");
        
        NumberAxis yAxis = (NumberAxis) getYAxis();
        yAxis.setLabel("Game Count");
    }

    /**
     * Set time granularity for the chart.
     */
    public void setGranularity(TimeGranularity granularity) {
        this.granularity = granularity;
    }

    /**
     * Update chart with data from opening tree nodes.
     * Shows the top N most played openings over time.
     */
    public void updateChart(OpeningTreeNode root, int topN) {
        getData().clear();
        
        if (root == null || root.getChildren().isEmpty()) {
            return;
        }
        
        // Get top N openings by total game count
        List<OpeningTreeNode> topOpenings = root.getChildrenSorted()
            .stream()
            .limit(topN)
            .toList();
        
        // Create a series for each opening
        for (OpeningTreeNode opening : topOpenings) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(opening.getMove());
            
            // Group games by time period
            Map<String, Long> timePeriodCounts = opening.getGames().stream()
                .filter(game -> game.getDate() != null)
                .collect(Collectors.groupingBy(
                    game -> formatTimePeriod(game.getDate()),
                    Collectors.counting()
                ));
            
            // Sort by time period and add to series
            timePeriodCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
                });
            
            getData().add(series);
        }
    }

    /**
     * Update chart with win percentage over time for a specific opening.
     */
    public void updateWinPercentageChart(OpeningTreeNode node) {
        getData().clear();
        
        if (node == null || node.getGames().isEmpty()) {
            return;
        }
        
        setTitle("Win Percentage Over Time: " + (node.getMove() != null ? node.getMove() : "All"));
        
        NumberAxis yAxis = (NumberAxis) getYAxis();
        yAxis.setLabel("Win Percentage (%)");
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(100);
        
        // Group games by time period
        Map<String, List<GameReference>> timePeriodGames = node.getGames().stream()
            .filter(game -> game.getDate() != null)
            .collect(Collectors.groupingBy(game -> formatTimePeriod(game.getDate())));
        
        // Calculate win percentage for each period
        XYChart.Series<String, Number> winSeries = new XYChart.Series<>();
        winSeries.setName("Win %");
        
        timePeriodGames.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> {
                List<GameReference> games = entry.getValue();
                long wins = games.stream().filter(GameReference::isWin).count();
                double winPercentage = (wins * 100.0) / games.size();
                winSeries.getData().add(new XYChart.Data<>(entry.getKey(), winPercentage));
            });
        
        getData().add(winSeries);
    }

    /**
     * Format a date according to current granularity.
     */
    private String formatTimePeriod(LocalDate date) {
        if (granularity == TimeGranularity.MONTH) {
            return date.format(MONTH_FORMATTER);
        } else {
            return date.format(YEAR_FORMATTER);
        }
    }

    /**
     * Clear the chart.
     */
    public void clear() {
        getData().clear();
    }
}
