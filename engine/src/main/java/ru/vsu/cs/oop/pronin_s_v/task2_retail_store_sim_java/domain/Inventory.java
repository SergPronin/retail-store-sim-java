package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain;

import java.time.LocalDate;
import java.util.*;

/**
 * Инвентарь магазина.
 * <p>
 * Хранит все партии товаров по локациям ({@link Location}) и продуктам ({@link Product}).
 * Для каждой пары (локация, товар) поддерживается упорядоченный список партий ({@link Lot})
 * в порядке FEFO (First-Expired-First-Out): партия с ближайшей датой истечения
 * срока годности идёт первой.
 * <p>
 * Основные операции:
 * <ul>
 *     <li>добавление партии ({@link #add(Location, Lot)});</li>
 *     <li>перемещение товаров между локациями с учётом партий ({@link #move(Location, Location, Product, double)});</li>
 *     <li>списание из конкретной локации, обычно из зала ({@link #deductFromFloor(Product, double)});</li>
 *     <li>удаление просроченных партий ({@link #removeExpired(Location, LocalDate)});</li>
 *     <li>агрегация остатков по продуктам ({@link #totalByLocation(Location)}).</li>
 * </ul>
 */
public class Inventory {

    /**
     * Остатки по локациям:
     * <pre>
     *   Location -> (Product -> очередь партий Lot в FEFO-порядке)
     * </pre>
     */
    private final Map<Location, Map<Product, Deque<Lot>>> storage = new EnumMap<>(Location.class);

    /**
     * Создаёт пустой инвентарь с подготовленными мапами для всех локаций.
     */
    public Inventory() {
        for (Location loc : Location.values()) {
            storage.put(loc, new HashMap<>());
        }
    }

    /**
     * Добавляет партию товара в указанную локацию с учётом FEFO-порядка.
     * <p>
     * Новая партия вставляется в такую позицию, чтобы в очереди партии шли
     * по возрастанию срока годности (скоропортящиеся раньше нескоропортящих).
     *
     * @param loc локация (склад или зал)
     * @param lot партия товара
     */
    public void add(Location loc, Lot lot) {
        var byProduct = storage.get(loc).computeIfAbsent(lot.product(), p -> new ArrayDeque<>());
        if (byProduct.isEmpty()) {
            byProduct.add(lot);
            return;
        }

        // Вставка по месту с учётом даты истечения: ближе дата -> раньше
        List<Lot> tmp = new ArrayList<>(byProduct);
        int idx = 0;
        while (idx < tmp.size()) {
            LocalDate a = lot.expiry();
            LocalDate b = tmp.get(idx).expiry();
            int cmp;
            if (a == null && b == null) {
                cmp = 0;
            } else if (a == null) {
                // null (нескоропорт) идёт после скоропортящихся
                cmp = 1;
            } else if (b == null) {
                cmp = -1;
            } else {
                cmp = a.compareTo(b);
            }
            if (cmp <= 0) break;
            idx++;
        }
        tmp.add(idx, lot);
        byProduct.clear();
        byProduct.addAll(tmp);
    }

    /**
     * Перемещает товар между локациями, снимая количество партиями в FEFO-порядке.
     * <p>
     * Для каждой затронутой партии создаётся «кусочек» партии в целевой локации
     * с тем же товаром и сроком годности.
     *
     * @param from   исходная локация
     * @param to     целевая локация
     * @param p      товар
     * @param amount требуемое количество
     * @return фактически перемещённое количество
     */
    public double move(Location from, Location to, Product p, double amount) {
        if (amount <= 0) return 0;
        var byProduct = storage.get(from).get(p);
        if (byProduct == null || byProduct.isEmpty()) return 0;

        double remaining = amount;
        while (remaining > 1e-9 && !byProduct.isEmpty()) {
            Lot head = byProduct.peekFirst();
            double take = Math.min(remaining, head.qty());
            Lot movedPiece = new Lot(p, take, head.expiry());
            add(to, movedPiece);
            head.subtract(take);
            remaining -= take;
            if (head.isEmpty()) byProduct.removeFirst();
        }
        double moved = amount - remaining;
        if (byProduct != null && byProduct.isEmpty()) {
            storage.get(from).remove(p);
        }
        return moved;
    }

    /**
     * Списание товара из торгового зала ({@link Location#FLOOR}) по FEFO.
     *
     * @param p      товар
     * @param amount запрашиваемое количество
     * @return фактически списанное количество
     */
    public double deductFromFloor(Product p, double amount) {
        return deduct(Location.FLOOR, p, amount);
    }

    /**
     * Внутренний метод списания товара из указанной локации по FEFO-порядку.
     *
     * @param loc    локация
     * @param p      товар
     * @param amount запрашиваемое количество
     * @return фактически списанное количество
     */
    private double deduct(Location loc, Product p, double amount) {
        if (amount <= 0) return 0;
        var byProduct = storage.get(loc).get(p);
        if (byProduct == null || byProduct.isEmpty()) return 0;

        double remaining = amount;
        while (remaining > 1e-9 && !byProduct.isEmpty()) {
            Lot head = byProduct.peekFirst();
            double take = Math.min(remaining, head.qty());
            head.subtract(take);
            remaining -= take;
            if (head.isEmpty()) byProduct.removeFirst();
        }
        double deducted = amount - remaining;
        if (byProduct != null && byProduct.isEmpty()) {
            storage.get(loc).remove(p);
        }
        return deducted;
    }

    /**
     * Удаляет просроченные партии в локации.
     * <p>
     * Партия считается просроченной, если её {@link Lot#expiry()} не null
     * и строго раньше даты {@code today}.
     *
     * @param loc   локация, в которой проводим чистку
     * @param today текущая дата симуляции
     * @return количество удалённых партий
     */
    public int removeExpired(Location loc, LocalDate today) {
        int removedLots = 0;
        var map = storage.get(loc);
        for (var it = map.entrySet().iterator(); it.hasNext(); ) {
            var e = it.next();
            Deque<Lot> q = e.getValue();
            List<Lot> keep = new ArrayList<>();
            for (Lot lot : q) {
                if (lot.expiry() != null && lot.expiry().isBefore(today)) {
                    removedLots++;
                } else {
                    keep.add(lot);
                }
            }
            if (keep.isEmpty()) {
                it.remove();
            } else {
                q.clear();
                q.addAll(keep);
            }
        }
        return removedLots;
    }

    /**
     * Возвращает агрегированные остатки по продуктам для указанной локации.
     *
     * @param loc локация
     * @return мапа: товар → суммарное количество (сумма по всем партиям)
     */
    public Map<Product, Double> totalByLocation(Location loc) {
        Map<Product, Double> res = new HashMap<>();
        var map = storage.get(loc);
        for (var e : map.entrySet()) {
            double sum = 0;
            for (Lot lot : e.getValue()) {
                sum += lot.qty();
            }
            res.put(e.getKey(), sum);
        }
        return res;
    }
}