package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.Product;

/**
 * Модальное окно с детальной информацией о товаре.
 */
public class ProductDetailDialog {
    
    public static void show(Product product, Double quantity, Stage parent) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(parent);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle("Детали товара");
        
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #1a1a2e;");
        root.setAlignment(Pos.CENTER);
        
        String emoji = getEmojiForCategory(product.category());
        Label iconLabel = new Label(emoji);
        iconLabel.setFont(Font.font(48));
        
        Label nameLabel = new Label(product.name());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        nameLabel.setTextFill(Color.WHITE);
        
        VBox details = new VBox(8);
        details.setAlignment(Pos.CENTER_LEFT);
        
        addDetailRow(details, "Артикул:", product.code());
        addDetailRow(details, "Категория:", product.category());
        addDetailRow(details, "Тип измерения:", product.measure().toString());
        addDetailRow(details, "Базовая цена:", String.format("%.2f ₽", product.basePrice()));
        addDetailRow(details, "Количество:", String.format("%.2f %s", quantity, product.measure()));
        addDetailRow(details, "Скоропортящийся:", product.perishable() ? "Да" : "Нет");
        
        Button closeButton = new Button("Закрыть");
        closeButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 10 20;");
        closeButton.setOnAction(e -> dialog.close());
        closeButton.setOnMouseEntered(e -> closeButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 10 20;"));
        closeButton.setOnMouseExited(e -> closeButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 10 20;"));
        
        root.getChildren().addAll(iconLabel, nameLabel, details, closeButton);
        
        Scene scene = new Scene(root, 350, 400);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    
    private static void addDetailRow(VBox container, String label, String value) {
        Label labelNode = new Label(label);
        labelNode.setFont(Font.font("Arial", 12));
        labelNode.setTextFill(Color.LIGHTGRAY);
        
        Label valueNode = new Label(value);
        valueNode.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        valueNode.setTextFill(Color.WHITE);
        
        javafx.scene.layout.HBox row = new javafx.scene.layout.HBox(10, labelNode, valueNode);
        row.setAlignment(Pos.CENTER_LEFT);
        container.getChildren().add(row);
    }
    
    private static String getEmojiForCategory(String category) {
        return switch (category.toLowerCase()) {
            case "мясо" -> "🥩";
            case "молочные" -> "🥛";
            case "хлебобулочные" -> "🍞";
            case "овощи-фрукты" -> "🍎";
            case "бытовая химия" -> "🧼";
            default -> "📦";
        };
    }
}


