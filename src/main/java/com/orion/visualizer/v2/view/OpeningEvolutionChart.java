package com.orion.visualizer.v2.view;

import com.orion.visualizer.v2.model.OpeningStatistics;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Chart showing opening usage and performance evolution over time.
 */
public class OpeningEvolutionChart extends LineChart<String, Number> {
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("MMM yyyy");
    
    public OpeningEvolutionChart() {
        super(new CategoryAxis(), new NumberAxis());
        
        setTitle("Opening Evolution Over Time");
        setLegendVisible(true);
        setCreateSymbols(true);
        setAnimated(true);
        
        CategoryAxis xAxis = (CategoryAxis) getXAxis();
        xAxis.setLabel("Time Period");
        
        NumberAxis yAxis = (NumberAxis) getYAxis();
        yAxis.setLabel("Score %");
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        yAxis.setUpperBound(100);
        yAxis.setTickUnit(10);
    }
    
    /**
     * Display opening statistics with timeline.
     */
    public void displayOpeningEvolution(List<OpeningStatistics> openings) {
        getData().clear();
        
        for (OpeningStatistics opening : openings) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(opening.getOpening());
            
            for (OpeningStatistics.TimePoint point : opening.getTimeline()) {
                String dateStr = point.getDate().format(DATE_FORMATTER);
                series.getData().add(new XYChart.Data<>(dateStr, point.getScore()));
            }
            
            getData().add(series);
        }
    }
    
    /**
     * Display single opening evolution.
     */
    public void displaySingleOpening(OpeningStatistics opening) {
        getData().clear();
        
        // Score series
        XYChart.Series<String, Number> scoreSeries = new XYChart.Series<>();
        scoreSeries.setName("Score %");
        
        // Win rate series
        XYChart.Series<String, Number> winSeries = new XYChart.Series<>();
        winSeries.setName("Win Rate %");
        
        for (OpeningStatistics.TimePoint point : opening.getTimeline()) {
            String dateStr = point.getDate().format(DATE_FORMATTER);
            scoreSeries.getData().add(new XYChart.Data<>(dateStr, point.getScore()));
            winSeries.getData().add(new XYChart.Data<>(dateStr, point.getWinRate()));
        }
        
        getData().addAll(scoreSeries, winSeries);
    }
    
    /**
     * Clear the chart.
     */
    public void clear() {
        getData().clear();
    }
}
