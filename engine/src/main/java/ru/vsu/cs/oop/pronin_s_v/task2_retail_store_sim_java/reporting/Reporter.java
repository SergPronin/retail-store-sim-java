package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.reporting;

import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.AppContext;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.Location;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.Product;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Утилита для форматированного вывода итогового отчёта за день.
 * <p>
 * Используется движком симуляции после выполнения всех событий за текущую дату.
 */
public class Reporter {

    /**
     * Печатает полный отчёт по итогам дня:
     * <ul>
     *     <li>общая выручка;</li>
     *     <li>количество чеков и позиций;</li>
     *     <li>количество списанных партий;</li>
     *     <li>топ-3 товаров по количеству продаж;</li>
     *     <li>остатки на складе;</li>
     *     <li>остатки в зале;</li>
     * </ul>
     *
     * @param day   дата
     * @param stats ежедневная статистика
     */
    public static void printEndOfDay(LocalDate day, DayStats stats) {
        System.out.println();
        System.out.printf("--- Отчёт за день %s ---%n", day);

        System.out.printf(
                "Выручка: %.2f; чеков: %d; позиций в чеках: %d; удалено просроченных партий: %d%n",
                stats.revenue().doubleValue(),
                stats.receipts(),
                stats.positionsTotal(),
                stats.expiredLotsRemoved()
        );

        // Топ-3 товаров по количеству проданного
        var top3 = stats.soldQty().entrySet().stream()
                .sorted(Map.Entry.<Product, Double>comparingByValue(Comparator.reverseOrder()))
                .limit(3)
                .collect(Collectors.toList());

        if (!top3.isEmpty()) {
            System.out.println("Топ продаж (по количеству):");
            for (var e : top3) {
                System.out.printf("  • %s — %.2f%n", e.getKey().name(), e.getValue());
            }
        } else {
            System.out.println("Топ продаж: нет продаж");
        }

        // --------------------- ОСТАТКИ -------------------------

        var wh = AppContext.inventory.totalByLocation(Location.WAREHOUSE);
        var fl = AppContext.inventory.totalByLocation(Location.FLOOR);

        System.out.println("Остатки на складе (топ-5):");
        wh.entrySet().stream()
                .sorted(Map.Entry.<Product, Double>comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .forEach(e ->
                        System.out.printf("  • %-12s = %.2f%n", e.getKey().name(), e.getValue())
                );

        System.out.println("Остатки в зале (топ-5):");
        fl.entrySet().stream()
                .sorted(Map.Entry.<Product, Double>comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .forEach(e ->
                        System.out.printf("  • %-12s = %.2f%n", e.getKey().name(), e.getValue())
                );

        System.out.println("--- Конец отчёта ---\n");
    }
}