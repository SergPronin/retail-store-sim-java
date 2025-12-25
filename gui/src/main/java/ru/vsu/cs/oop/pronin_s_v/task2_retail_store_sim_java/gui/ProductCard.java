package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.gui;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.Product;

/**
 * Интерактивная карточка товара с анимациями и hover эффектами.
 */
public class ProductCard extends HBox {
    private final Product product;
    private final Double quantity;
    private final Runnable onCardClick;
    
    private Label iconLabel;
    private Label nameLabel;
    private Label qtyLabel;
    private StackPane progressPane;
    private ScaleTransition hoverAnimation;

    public ProductCard(Product product, Double quantity, Runnable onCardClick) {
        this.product = product;
        this.quantity = quantity;
        this.onCardClick = onCardClick;
        
        setSpacing(10);
        setPadding(new Insets(8));
        setAlignment(Pos.CENTER_LEFT);
        setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 8;");
        
        createComponents();
        setupAnimations();
        setupInteractivity();
    }

    private void createComponents() {
        String emoji = getEmojiForCategory(product.category());
        iconLabel = new Label(emoji);
        iconLabel.setFont(Font.font(28));
        iconLabel.setEffect(new Glow(0.5));
        
        VBox infoBox = new VBox(3);
        nameLabel = new Label(product.name());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        nameLabel.setTextFill(Color.WHITE);
        
        qtyLabel = new Label(String.format("%.2f %s", quantity, product.measure()));
        qtyLabel.setFont(Font.font("Arial", 12));
        qtyLabel.setTextFill(Color.LIGHTGRAY);
        
        Label priceLabel = new Label(String.format("%.2f ₽", product.basePrice()));
        priceLabel.setFont(Font.font("Arial", 11));
        priceLabel.setTextFill(Color.LIGHTGREEN);
        
        infoBox.getChildren().addAll(nameLabel, qtyLabel, priceLabel);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        
        double maxQty = 100.0;
        double percentage = Math.min(quantity / maxQty, 1.0);
        
        progressPane = createProgressBar(percentage);
        
        getChildren().addAll(iconLabel, infoBox, progressPane);
    }

    private StackPane createProgressBar(double percentage) {
        StackPane progressPane = new StackPane();
        progressPane.setPrefWidth(100);
        progressPane.setPrefHeight(25);
        
        Rectangle bg = new Rectangle(100, 25);
        bg.setFill(Color.rgb(50, 50, 50));
        bg.setArcWidth(12);
        bg.setArcHeight(12);
        
        Rectangle fill = new Rectangle(100 * percentage, 25);
        fill.setFill(getColorForQuantity(percentage));
        fill.setArcWidth(12);
        fill.setArcHeight(12);
        
        Label percentageLabel = new Label(String.format("%.0f%%", percentage * 100));
        percentageLabel.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        percentageLabel.setTextFill(Color.WHITE);
        
        progressPane.getChildren().addAll(bg, fill, percentageLabel);
        return progressPane;
    }

    private void setupAnimations() {
        hoverAnimation = new ScaleTransition(Duration.millis(200), this);
        hoverAnimation.setFromX(1.0);
        hoverAnimation.setFromY(1.0);
        hoverAnimation.setToX(1.05);
        hoverAnimation.setToY(1.05);
    }

    private void setupInteractivity() {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(100, 150, 255, 0.6));
        shadow.setRadius(10);
        
        setOnMouseEntered(e -> {
            setStyle("-fx-background-color: rgba(100, 150, 255, 0.2); -fx-background-radius: 8; -fx-cursor: hand;");
            setEffect(shadow);
            hoverAnimation.play();
            iconLabel.setEffect(new Glow(1.0));
        });
        
        setOnMouseExited(e -> {
            setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-background-radius: 8;");
            setEffect(null);
            hoverAnimation.setRate(-1);
            hoverAnimation.play();
            iconLabel.setEffect(new Glow(0.5));
        });
        
        setOnMouseClicked(e -> {
            if (onCardClick != null) {
                onCardClick.run();
            }
        });
    }

    private String getEmojiForCategory(String category) {
        return switch (category.toLowerCase()) {
            case "мясо" -> "🥩";
            case "молочные" -> "🥛";
            case "хлебобулочные" -> "🍞";
            case "овощи-фрукты" -> "🍎";
            case "бытовая химия" -> "🧼";
            default -> "📦";
        };
    }

    private Color getColorForQuantity(double percentage) {
        if (percentage > 0.7) return Color.rgb(46, 204, 113);
        if (percentage > 0.3) return Color.rgb(241, 196, 15);
        return Color.rgb(231, 76, 60);
    }

    public Product getProduct() {
        return product;
    }

    public Double getQuantity() {
        return quantity;
    }
    
}


