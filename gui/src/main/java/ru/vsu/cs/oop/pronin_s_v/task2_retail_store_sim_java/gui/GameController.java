package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.gui;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.AppContext;

/**
 * Контроллер игрового интерфейса.
 */
public class GameController {

    private final GameApp app;
    private BorderPane root;
    
    private ControlPanel controlPanel;
    private StoreView storeView;
    private StatsPanel statsPanel;
    private LogPanel logPanel;
    private HeaderPanel headerPanel;

    public GameController(GameApp app) {
        this.app = app;
    }

    public BorderPane createRoot() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a2e;");
        
        // Верхняя панель с заголовком и управлением
        headerPanel = new HeaderPanel(app);
        root.setTop(headerPanel.createHeader());
        
        // Центральная область: визуализация магазина
        storeView = new StoreView(app);
        root.setCenter(storeView.createStoreView());
        
        // Правая панель: статистика
        statsPanel = new StatsPanel(app);
        root.setRight(statsPanel.createStatsPanel());
        
        // Нижняя панель: лог
        logPanel = new LogPanel(app);
        root.setBottom(logPanel.createLogPanel());
        
        // Левая панель: управление
        controlPanel = new ControlPanel(app);
        root.setLeft(controlPanel.createControlPanel());
        
        return root;
    }

    public void update() {
        if (storeView != null) {
            storeView.update();
        }
        if (statsPanel != null) {
            statsPanel.update();
        }
        if (headerPanel != null) {
            headerPanel.update();
        }
    }

    public void setRunning(boolean running) {
        if (controlPanel != null) {
            controlPanel.setRunning(running);
        }
    }

    public void log(String message) {
        if (logPanel != null) {
            logPanel.log(message);
        }
    }

    public void clearLog() {
        if (logPanel != null) {
            logPanel.clear();
        }
    }

    public void animateEvent(String eventType) {
        if (storeView != null) {
            storeView.animateEvent(eventType);
        }
    }
    
    public void setParentStage(javafx.stage.Stage stage) {
        if (storeView != null) {
            storeView.setParentStage(stage);
        }
    }
}

