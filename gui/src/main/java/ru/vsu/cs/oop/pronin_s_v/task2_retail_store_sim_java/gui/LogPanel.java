package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.gui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LogPanel {
    private final GameApp app;
    private TextArea logArea;

    public LogPanel(GameApp app) {
        this.app = app;
    }

    public VBox createLogPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));
        panel.setPrefHeight(200);
        panel.setStyle("-fx-background-color: #0f3460; -fx-background-radius: 10;");
        
        HBox header = new HBox(10);
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label title = new Label("📝 ЛОГ СОБЫТИЙ");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        title.setTextFill(Color.WHITE);
        
        Button clearButton = new Button("🗑️ Очистить");
        clearButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5;");
        clearButton.setOnAction(e -> clear());
        clearButton.setOnMouseEntered(e -> clearButton.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-background-radius: 5;"));
        clearButton.setOnMouseExited(e -> clearButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 5;"));
        
        HBox.setHgrow(title, Priority.ALWAYS);
        header.getChildren().addAll(title, clearButton);
        
        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setFont(Font.font("Consolas", 12));
        logArea.setStyle("-fx-background-color: #1a1a2e; -fx-text-fill: #00ff00; -fx-control-inner-background: #1a1a2e;");
        logArea.setWrapText(true);
        
        ScrollPane scrollPane = new ScrollPane(logArea);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        panel.getChildren().addAll(header, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        return panel;
    }

    public void log(String message) {
        Platform.runLater(() -> {
            logArea.appendText(message + "\n");
            logArea.setScrollTop(Double.MAX_VALUE);
        });
    }

    public void clear() {
        Platform.runLater(() -> {
            logArea.clear();
        });
    }
}
