package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.reporting;

import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.Product;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Ежедневная статистика магазина.
 * <p>
 * Этот объект создаётся один раз (в {@link ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.AppContext}),
 * затем заполняется по мере выполнения событий в течение дня
 * и сбрасывается после генерации отчёта.
 *
 * <p>Статистика включает:
 * <ul>
 *     <li>выручку за день;</li>
 *     <li>количество чеков;</li>
 *     <li>количество товарных позиций в чеках;</li>
 *     <li>количество списанных просроченных партий;</li>
 *     <li>учёт проданного количества по каждому товару.</li>
 * </ul>
 */
public class DayStats {

    /** Общая выручка за день. */
    private BigDecimal revenue = BigDecimal.ZERO;

    /** Количество чеков за день. */
    private int receipts = 0;

    /** Суммарное количество позиций во всех чеках. */
    private int positionsTotal = 0;

    /** Сколько партий было удалено как просроченные. */
    private int expiredLotsRemoved = 0;

    /**
     * Сколько каждого товара было продано.
     * Ключи — товары, значения — проданное количество.
     * <p>LinkedHashMap сохраняет порядок добавления.</p>
     */
    private final Map<Product, Double> soldQty = new LinkedHashMap<>();

    /**
     * Добавляет чек, увеличивая выручку и статистику позиций.
     *
     * @param total     сумма чека
     * @param positions количество строк в чеке
     */
    public void addReceipt(BigDecimal total, int positions) {
        if (total != null) revenue = revenue.add(total);
        receipts++;
        positionsTotal += Math.max(0, positions);
    }

    /**
     * Добавляет строку продажи конкретного товара.
     *
     * @param p       товар
     * @param qty     количество
     * @param lineSum сумма по строке
     */
    public void addSale(Product p, double qty, BigDecimal lineSum) {
        soldQty.merge(p, qty, Double::sum);
        if (lineSum != null) revenue = revenue.add(lineSum);
    }

    /**
     * Увеличивает счётчик списанных партий просрочки.
     */
    public void addExpiredLots(int lots) {
        expiredLotsRemoved += Math.max(0, lots);
    }

    /** @return общая выручка за день */
    public BigDecimal revenue() { return revenue; }

    public int receipts() { return receipts; }

    public int positionsTotal() { return positionsTotal; }

    public int expiredLotsRemoved() { return expiredLotsRemoved; }

    /**
     * @return неизменяемое представление карты продаж
     */
    public Map<Product, Double> soldQty() { return soldQty; }

    /**
     * Сбрасывает статистику к начальному состоянию.
     * Используется в конце каждого дня.
     */
    public void reset() {
        revenue = BigDecimal.ZERO;
        receipts = 0;
        positionsTotal = 0;
        expiredLotsRemoved = 0;
        soldQty.clear();
    }
}