package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.app.Config;

public class ControlPanel {
    private final GameApp app;
    private Button startButton;
    private Button stopButton;
    private Button resetButton;
    private Spinner<Integer> daysSpinner;
    private Spinner<Long> seedSpinner;

    public ControlPanel(GameApp app) {
        this.app = app;
    }

    public VBox createControlPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setPrefWidth(250);
        panel.setStyle("-fx-background-color: #0f3460; -fx-background-radius: 10;");
        
        Label title = new Label("🎮 УПРАВЛЕНИЕ");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.WHITE);
        
        Label daysLabel = new Label("Дни симуляции:");
        daysLabel.setTextFill(Color.WHITE);
        daysSpinner = new Spinner<>(1, 365, Config.SIM_DAYS_DEFAULT, 1);
        daysSpinner.setPrefWidth(150);
        daysSpinner.valueProperty().addListener((obs, oldVal, newVal) -> app.setDaysToSimulate(newVal));
        
        Label seedLabel = new Label("Seed:");
        seedLabel.setTextFill(Color.WHITE);
        seedSpinner = new Spinner<>(0L, Long.MAX_VALUE, 42L, 1L);
        seedSpinner.setPrefWidth(150);
        seedSpinner.valueProperty().addListener((obs, oldVal, newVal) -> app.setSeed(newVal));
        
        VBox settingsBox = new VBox(10, daysLabel, daysSpinner, seedLabel, seedSpinner);
        
        startButton = createStyledButton("▶ СТАРТ", "#2ecc71");
        startButton.setOnAction(e -> app.startSimulation());
        
        stopButton = createStyledButton("⏸ СТОП", "#e74c3c");
        stopButton.setOnAction(e -> app.stopSimulation());
        stopButton.setDisable(true);
        
        resetButton = createStyledButton("🔄 СБРОС", "#f39c12");
        resetButton.setOnAction(e -> app.resetSimulation());
        
        VBox buttonsBox = new VBox(10, startButton, stopButton, resetButton);
        panel.getChildren().addAll(title, settingsBox, buttonsBox);
        return panel;
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setPrefWidth(200);
        button.setPrefHeight(40);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        button.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-background-radius: 5;", color));
        button.setOnMouseEntered(e -> button.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-background-radius: 5; -fx-effect: dropshadow(gaussian, %s, 10, 0, 0, 0);", color, color)));
        button.setOnMouseExited(e -> button.setStyle(String.format("-fx-background-color: %s; -fx-text-fill: white; -fx-background-radius: 5;", color)));
        return button;
    }

    public void setRunning(boolean running) {
        startButton.setDisable(running);
        stopButton.setDisable(!running);
        daysSpinner.setDisable(running);
        seedSpinner.setDisable(running);
    }
}
