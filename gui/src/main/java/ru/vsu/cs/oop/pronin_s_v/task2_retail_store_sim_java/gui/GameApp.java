package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.app.Config;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core.Clock;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core.EventQueue;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core.RandomEx;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core.SimEngine;

import java.time.LocalDate;

/**
 * Главное игровое приложение на JavaFX.
 */
public class GameApp extends Application {

    private SimEngine engine;
    private Clock clock;
    private EventQueue queue;
    private RandomEx rnd;
    
    private GameController controller;
    private boolean isRunning = false;
    private int daysToSimulate = Config.SIM_DAYS_DEFAULT;
    private int daysSimulated = 0;
    private long seed = 42L;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("🏪 Симуляция Супермаркета - Игровой режим");
        
        initializeEngine();
        
        controller = new GameController(this);
        BorderPane root = controller.createRoot();
        
        // Передаем Stage в StoreView для модальных окон
        controller.setParentStage(primaryStage);
        
        Scene scene = new Scene(root, 1600, 1000);
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception e) {
            // CSS файл не найден, продолжаем без него
            System.out.println("CSS file not found, continuing without styles");
        }
        
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(800);
        primaryStage.setOnCloseRequest(e -> {
            stopSimulation();
            Platform.exit();
        });
        
        primaryStage.show();
        
        // Обновляем начальное состояние
        Platform.runLater(() -> controller.update());
    }

    private void initializeEngine() {
        clock = new Clock(LocalDate.now());
        queue = new EventQueue();
        rnd = new RandomEx(seed);
        engine = new SimEngine(clock, queue, rnd);
    }

    public void startSimulation() {
        if (isRunning) {
            return;
        }
        
        isRunning = true;
        daysSimulated = 0;
        controller.setRunning(true);
        controller.log("=== 🚀 Начало симуляции ===");
        controller.log(String.format("📅 Дни: %d | 🎲 Seed: %d", daysToSimulate, seed));
        
        // Запускаем симуляцию в отдельном потоке
        new Thread(() -> {
            while (isRunning && daysSimulated < daysToSimulate) {
                try {
                    runOneDay();
                    daysSimulated++;
                    
                    if (daysSimulated >= daysToSimulate) {
                        Platform.runLater(() -> {
                            stopSimulation();
                            controller.log("=== ✅ Симуляция завершена ===");
                        });
                        break;
                    }
                    
                    // Пауза между днями (можно настроить скорость)
                    Thread.sleep(2000);
                } catch (Exception ex) {
                    Platform.runLater(() -> {
                        controller.log("❌ Ошибка: " + ex.getMessage());
                        stopSimulation();
                    });
                    ex.printStackTrace();
                    break;
                }
            }
        }).start();
    }

    public void stopSimulation() {
        isRunning = false;
        controller.setRunning(false);
        controller.log("=== ⏸ Симуляция остановлена ===");
    }

    public void resetSimulation() {
        stopSimulation();
        daysSimulated = 0;
        initializeEngine();
        Platform.runLater(() -> {
            controller.update();
            controller.clearLog();
            controller.log("=== 🔄 Симуляция сброшена ===");
        });
    }

    private void runOneDay() {
        LocalDate today = clock.today();
        
        Platform.runLater(() -> {
            controller.log(String.format("=== 📆 День %s ===", today));
        });
        
        engine.seedDay(today);
        
        int eventsProcessed = 0;
        while (true) {
            var event = queue.poll();
            if (event == null) break;
            if (!event.when.equals(today)) {
                queue.add(event);
                break;
            }
            
            // Визуализация события
            final String eventType = event.getClass().getSimpleName();
            Platform.runLater(() -> {
                controller.animateEvent(eventType);
            });
            
            event.apply();
            eventsProcessed++;
            
            // Небольшая пауза для визуализации
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        final int eventsCount = eventsProcessed;
        Platform.runLater(() -> {
            controller.log(String.format("📊 Обработано событий: %d", eventsCount));
            controller.update();
        });
        
        clock.nextDay();
    }

    public void setDaysToSimulate(int days) {
        this.daysToSimulate = days;
    }

    public void setSeed(long seed) {
        this.seed = seed;
        if (!isRunning) {
            initializeEngine();
        }
    }

    public SimEngine getEngine() {
        return engine;
    }

    public Clock getClock() {
        return clock;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

