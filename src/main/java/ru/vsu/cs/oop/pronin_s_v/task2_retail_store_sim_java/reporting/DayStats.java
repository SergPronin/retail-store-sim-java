package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.reporting;

import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.Product;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class DayStats {
    private BigDecimal revenue = BigDecimal.ZERO;   // выручка за день
    private int receipts = 0;                       // количество чеков
    private int positionsTotal = 0;                 // суммарно строк в чеках
    private int expiredLotsRemoved = 0;             // удалено партий (штук) просрочки

    // продано: товар -> кол-во (в его единицах измерения)
    private final Map<Product, Double> soldQty = new LinkedHashMap<>();

    public void addReceipt(BigDecimal total, int positions) {
        if (total != null) revenue = revenue.add(total);
        receipts += 1;
        positionsTotal += Math.max(0, positions);
    }

    public void addSale(Product p, double qty, BigDecimal lineSum) {
        soldQty.merge(p, qty, Double::sum);
        if (lineSum != null) revenue = revenue.add(lineSum);
    }

    public void addExpiredLots(int lots) {
        expiredLotsRemoved += Math.max(0, lots);
    }

    public BigDecimal revenue() { return revenue; }
    public int receipts() { return receipts; }
    public int positionsTotal() { return positionsTotal; }
    public int expiredLotsRemoved() { return expiredLotsRemoved; }
    public Map<Product, Double> soldQty() { return soldQty; }

    public void reset() {
        revenue = BigDecimal.ZERO;
        receipts = 0;
        positionsTotal = 0;
        expiredLotsRemoved = 0;
        soldQty.clear();
    }
}