package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class HeaderPanel {
    private final GameApp app;
    private Label dateLabel;
    private Label dayNumberLabel;

    public HeaderPanel(GameApp app) {
        this.app = app;
    }

    public HBox createHeader() {
        HBox header = new HBox(20);
        header.setPadding(new Insets(15));
        header.setStyle("-fx-background-color: #16213e; -fx-background-radius: 10;");
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label title = new Label("🏪 СИМУЛЯЦИЯ СУПЕРМАРКЕТА");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setTextFill(Color.WHITE);
        
        dateLabel = new Label();
        dateLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        dateLabel.setTextFill(Color.LIGHTGREEN);
        
        dayNumberLabel = new Label();
        dayNumberLabel.setFont(Font.font("Arial", 16));
        dayNumberLabel.setTextFill(Color.LIGHTBLUE);
        
        HBox infoBox = new HBox(15, dateLabel, dayNumberLabel);
        infoBox.setAlignment(Pos.CENTER);
        
        header.getChildren().addAll(title, infoBox);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        
        return header;
    }

    public void update() {
        if (dateLabel != null && app.getClock() != null) {
            dateLabel.setText("📅 " + app.getClock().today().toString());
        }
    }
}
