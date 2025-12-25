package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.gui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.AppContext;

import java.util.ArrayList;
import java.util.List;

/**
 * График выручки в реальном времени.
 */
public class RevenueChart extends VBox {
    private final LineChart<Number, Number> chart;
    private final XYChart.Series<Number, Number> revenueSeries;
    private final List<Double> revenueHistory;
    private int timeCounter = 0;
    private Timeline updateTimeline;

    public RevenueChart() {
        setPadding(new Insets(10));
        setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); -fx-background-radius: 8;");
        
        Label title = new Label("📈 Выручка в реальном времени");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        title.setTextFill(Color.WHITE);
        
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Время");
        xAxis.setTickLabelFill(Color.WHITE);
        
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Выручка (₽)");
        yAxis.setTickLabelFill(Color.WHITE);
        
        chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("");
        chart.setLegendVisible(false);
        chart.setPrefHeight(200);
        chart.setStyle("-fx-background-color: transparent;");
        chart.setCreateSymbols(false);
        
        revenueSeries = new XYChart.Series<>();
        revenueSeries.getData().add(new XYChart.Data<>(0, 0));
        chart.getData().add(revenueSeries);
        
        revenueHistory = new ArrayList<>();
        revenueHistory.add(0.0);
        
        // Стилизация линии
        revenueSeries.getNode().setStyle("-fx-stroke: #3498db; -fx-stroke-width: 2px;");
        
        getChildren().addAll(title, chart);
        
        startAutoUpdate();
    }
    
    private void startAutoUpdate() {
        updateTimeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> update()));
        updateTimeline.setCycleCount(Animation.INDEFINITE);
        updateTimeline.play();
    }
    
    public void update() {
        double currentRevenue = AppContext.dayStats.revenue().doubleValue();
        
        if (revenueHistory.isEmpty() || revenueHistory.get(revenueHistory.size() - 1) != currentRevenue) {
            revenueHistory.add(currentRevenue);
            timeCounter++;
            
            if (revenueHistory.size() > 50) {
                revenueHistory.remove(0);
            }
            
            revenueSeries.getData().clear();
            for (int i = 0; i < revenueHistory.size(); i++) {
                revenueSeries.getData().add(new XYChart.Data<>(i, revenueHistory.get(i)));
            }
        }
    }
    
    public void reset() {
        revenueHistory.clear();
        revenueHistory.add(0.0);
        timeCounter = 0;
        revenueSeries.getData().clear();
        revenueSeries.getData().add(new XYChart.Data<>(0, 0));
    }
    
    public void stop() {
        if (updateTimeline != null) {
            updateTimeline.stop();
        }
    }
}


