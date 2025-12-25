package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.AppContext;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.Product;

import java.util.Comparator;
import java.util.Map;

public class StatsPanel {
    private final GameApp app;
    private VBox statsContainer;
    private Label revenueValue;
    private Label receiptsValue;
    private Label positionsValue;
    private Label expiredValue;
    private BarChart<String, Number> salesChart;
    private RevenueChart revenueChart;

    public StatsPanel(GameApp app) {
        this.app = app;
    }

    public VBox createStatsPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setPrefWidth(350);
        panel.setStyle("-fx-background-color: #16213e; -fx-background-radius: 10;");
        
        Label title = new Label("📊 СТАТИСТИКА");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.WHITE);
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        statsContainer = new VBox(15);
        statsContainer.setPadding(new Insets(10));
        
        VBox metricsBox = createMetricsBox();
        revenueChart = new RevenueChart();
        salesChart = createSalesChart();
        VBox topSalesBox = createTopSalesBox();
        
        statsContainer.getChildren().addAll(metricsBox, revenueChart, salesChart, topSalesBox);
        scrollPane.setContent(statsContainer);
        
        panel.getChildren().addAll(title, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        return panel;
    }

    private VBox createMetricsBox() {
        VBox metricsBox = new VBox(10);
        metricsBox.setPadding(new Insets(10));
        metricsBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); -fx-background-radius: 8;");
        
        Label metricsTitle = new Label("💰 Метрики за день");
        metricsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        metricsTitle.setTextFill(Color.WHITE);
        
        HBox revenueRow = createMetricRow("Выручка:", "0.00 ₽", Color.GREEN);
        revenueValue = (Label) revenueRow.getChildren().get(1);
        
        HBox receiptsRow = createMetricRow("Чеков:", "0", Color.CYAN);
        receiptsValue = (Label) receiptsRow.getChildren().get(1);
        
        HBox positionsRow = createMetricRow("Позиций:", "0", Color.YELLOW);
        positionsValue = (Label) positionsRow.getChildren().get(1);
        
        HBox expiredRow = createMetricRow("Удалено:", "0", Color.ORANGE);
        expiredValue = (Label) expiredRow.getChildren().get(1);
        
        metricsBox.getChildren().addAll(metricsTitle, revenueRow, receiptsRow, positionsRow, expiredRow);
        return metricsBox;
    }

    private HBox createMetricRow(String label, String value, Color color) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        
        Label labelNode = new Label(label);
        labelNode.setFont(Font.font("Arial", 12));
        labelNode.setTextFill(Color.LIGHTGRAY);
        
        Label valueNode = new Label(value);
        valueNode.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        valueNode.setTextFill(color);
        
        HBox.setHgrow(valueNode, Priority.ALWAYS);
        row.getChildren().addAll(labelNode, valueNode);
        return row;
    }

    private BarChart<String, Number> createSalesChart() {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Количество");
        
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("📈 Топ продаж");
        chart.setLegendVisible(false);
        chart.setPrefHeight(200);
        chart.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); -fx-background-radius: 8;");
        
        xAxis.setTickLabelFill(Color.WHITE);
        yAxis.setTickLabelFill(Color.WHITE);
        return chart;
    }

    private VBox createTopSalesBox() {
        VBox topSalesBox = new VBox(8);
        topSalesBox.setPadding(new Insets(10));
        topSalesBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.05); -fx-background-radius: 8;");
        
        Label title = new Label("🏆 Топ товаров");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        title.setTextFill(Color.WHITE);
        
        topSalesBox.getChildren().add(title);
        topSalesBox.setUserData(topSalesBox);
        return topSalesBox;
    }

    public void update() {
        var stats = AppContext.dayStats;
        
        if (revenueValue != null) {
            revenueValue.setText(String.format("%.2f ₽", stats.revenue().doubleValue()));
        }
        if (receiptsValue != null) {
            receiptsValue.setText(String.valueOf(stats.receipts()));
        }
        if (positionsValue != null) {
            positionsValue.setText(String.valueOf(stats.positionsTotal()));
        }
        if (expiredValue != null) {
            expiredValue.setText(String.valueOf(stats.expiredLotsRemoved()));
        }
        
        if (revenueChart != null) {
            revenueChart.update();
        }
        updateSalesChart();
        updateTopSales();
    }

    private void updateSalesChart() {
        salesChart.getData().clear();
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        
        AppContext.dayStats.soldQty().entrySet().stream()
            .sorted(Map.Entry.<Product, Double>comparingByValue(Comparator.reverseOrder()))
            .limit(5)
            .forEach(entry -> {
                String name = entry.getKey().name();
                if (name.length() > 10) {
                    name = name.substring(0, 10) + "...";
                }
                series.getData().add(new XYChart.Data<>(name, entry.getValue()));
            });
        
        salesChart.getData().add(series);
        
        for (XYChart.Data<String, Number> data : series.getData()) {
            data.getNode().setStyle("-fx-bar-fill: #3498db;");
        }
    }

    private void updateTopSales() {
        VBox topSalesBox = (VBox) statsContainer.getChildren().get(3);
        topSalesBox.getChildren().clear();
        
        Label title = new Label("🏆 Топ товаров");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        title.setTextFill(Color.WHITE);
        topSalesBox.getChildren().add(title);
        
        if (AppContext.dayStats.soldQty().isEmpty()) {
            Label empty = new Label("Нет продаж");
            empty.setTextFill(Color.GRAY);
            topSalesBox.getChildren().add(empty);
            return;
        }
        
        AppContext.dayStats.soldQty().entrySet().stream()
            .sorted(Map.Entry.<Product, Double>comparingByValue(Comparator.reverseOrder()))
            .limit(5)
            .forEach(entry -> {
                HBox item = createTopSalesItem(entry.getKey().name(), entry.getValue());
                topSalesBox.getChildren().add(item);
            });
    }

    private HBox createTopSalesItem(String name, Double qty) {
        HBox item = new HBox(10);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(5));
        
        Circle dot = new Circle(5, Color.GOLD);
        
        Label nameLabel = new Label(name);
        nameLabel.setFont(Font.font("Arial", 12));
        nameLabel.setTextFill(Color.WHITE);
        
        Label qtyLabel = new Label(String.format("%.2f", qty));
        qtyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        qtyLabel.setTextFill(Color.LIGHTGREEN);
        
        HBox.setHgrow(nameLabel, Priority.ALWAYS);
        item.getChildren().addAll(dot, nameLabel, qtyLabel);
        return item;
    }
}
