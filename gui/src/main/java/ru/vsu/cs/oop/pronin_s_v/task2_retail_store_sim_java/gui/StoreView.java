package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.gui;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.AppContext;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.Location;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.Product;

import java.util.Comparator;
import java.util.Map;

/**
 * Визуализация склада и торгового зала.
 * Простая и надежная концепция - просто отображаем текущее состояние инвентаря.
 */
public class StoreView {
    private final GameApp app;
    private VBox warehouseContent;
    private VBox floorContent;
    private Label eventNotification;
    private Stage parentStage;

    public StoreView(GameApp app) {
        this.app = app;
    }
    
    public void setParentStage(Stage stage) {
        this.parentStage = stage;
    }

    public BorderPane createStoreView() {
        BorderPane storePane = new BorderPane();
        storePane.setPadding(new Insets(15));
        storePane.setStyle("-fx-background-color: #1a1a2e;");
        
        // Уведомление о событиях
        eventNotification = new Label();
        eventNotification.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        eventNotification.setTextFill(Color.YELLOW);
        eventNotification.setVisible(false);
        eventNotification.setAlignment(Pos.CENTER);
        eventNotification.setPadding(new Insets(10));
        
        HBox notificationBox = new HBox(eventNotification);
        notificationBox.setAlignment(Pos.CENTER);
        storePane.setTop(notificationBox);
        
        // Основная область: склад и торговый зал
        HBox mainArea = new HBox(20);
        mainArea.setPadding(new Insets(10));
        
        VBox warehouseView = createLocationView("🏭 СКЛАД", Location.WAREHOUSE, "#2c3e50");
        VBox floorView = createLocationView("🛒 ТОРГОВЫЙ ЗАЛ", Location.FLOOR, "#27ae60");
        
        mainArea.getChildren().addAll(warehouseView, floorView);
        HBox.setHgrow(warehouseView, Priority.ALWAYS);
        HBox.setHgrow(floorView, Priority.ALWAYS);
        
        storePane.setCenter(mainArea);
        return storePane;
    }

    private VBox createLocationView(String title, Location location, String bgColor) {
        VBox locationBox = new VBox(10);
        locationBox.setPadding(new Insets(15));
        locationBox.setStyle(String.format("-fx-background-color: %s; -fx-background-radius: 10;", bgColor));
        locationBox.setPrefWidth(400);
        
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.WHITE);
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        
        VBox contentBox = new VBox(8);
        contentBox.setPadding(new Insets(10));
        scrollPane.setContent(contentBox);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        locationBox.getChildren().addAll(titleLabel, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        // Сохраняем ссылку на contentBox
        if (location == Location.WAREHOUSE) {
            warehouseContent = contentBox;
        } else {
            floorContent = contentBox;
        }
        
        return locationBox;
    }

    /**
     * Простое обновление - перерисовываем все товары заново.
     */
    public void update() {
        updateLocation(warehouseContent, Location.WAREHOUSE);
        updateLocation(floorContent, Location.FLOOR);
    }
    
    private void updateLocation(VBox contentBox, Location location) {
        if (contentBox == null) return;
        
        // Очищаем содержимое
        contentBox.getChildren().clear();
        
        // Получаем текущее состояние инвентаря
        Map<Product, Double> totals = AppContext.inventory.totalByLocation(location);
        
        // Если нет товаров, показываем сообщение
        if (totals.isEmpty() || totals.values().stream().allMatch(q -> q <= 0)) {
            Label emptyLabel = new Label("📦 Нет товаров");
            emptyLabel.setTextFill(Color.GRAY);
            emptyLabel.setFont(Font.font("Arial", 14));
            contentBox.getChildren().add(emptyLabel);
            return;
        }
        
        // Создаем карточки для всех товаров
        totals.entrySet().stream()
            .filter(entry -> entry.getValue() > 0) // Только товары с количеством > 0
            .sorted(Map.Entry.<Product, Double>comparingByValue(Comparator.reverseOrder()))
            .forEach(entry -> {
                Product product = entry.getKey();
                Double quantity = entry.getValue();
                
                ProductCard card = new ProductCard(product, quantity, () -> {
                    if (parentStage != null) {
                        ProductDetailDialog.show(product, quantity, parentStage);
                    }
                });
                
                contentBox.getChildren().add(card);
            });
    }

    public void animateEvent(String eventType) {
        String message = switch (eventType) {
            case "DeliveryEvent" -> "🚚 Поставка товаров на склад!";
            case "MoveToFloorEvent" -> "📦 Товары перемещены в торговый зал!";
            case "PurchaseEvent" -> "💰 Покупка!";
            case "RemoveExpiredEvent" -> "🗑️ Удаление просроченных товаров!";
            case "SetDiscountEvent" -> "🏷️ Установлена скидка!";
            default -> "⚡ Событие: " + eventType;
        };
        
        eventNotification.setText(message);
        eventNotification.setVisible(true);
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), eventNotification);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        PauseTransition pause = new PauseTransition(Duration.millis(2000));
        
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), eventNotification);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> eventNotification.setVisible(false));
        
        SequentialTransition seq = new SequentialTransition(fadeIn, pause, fadeOut);
        seq.play();
    }
}
