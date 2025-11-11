package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain;

import java.time.LocalDate;
import java.util.*;

public class Inventory {

    // Остатки по локациям: продукт -> список партий (сортируем FEFO)
    private final Map<Location, Map<Product, Deque<Lot>>> storage = new EnumMap<>(Location.class);

    public Inventory() {
        for (Location loc : Location.values()) {
            storage.put(loc, new HashMap<>());
        }
    }

    public void add(Location loc, Lot lot) {
        var byProduct = storage.get(loc).computeIfAbsent(lot.product(), p -> new ArrayDeque<>());
        // Вставим FEFO: партия с ближайшим expiry должна идти первой
        if ( byProduct.isEmpty() ) {
            byProduct.add(lot);
            return;
        }
        // вставка по месту
        List<Lot> tmp = new ArrayList<>(byProduct);
        int idx = 0;
        while (idx < tmp.size()) {
            LocalDate a = lot.expiry();
            LocalDate b = tmp.get(idx).expiry();
            int cmp;
            if ( a == null && b == null ) cmp = 0;
            else if ( a == null ) cmp = 1;      // null (нескоропорт) идёт после скоропортов
            else if ( b == null ) cmp = -1;
            else cmp = a.compareTo(b);        // ближе срок -> раньше
            if ( cmp <= 0 ) break;
            idx++;
        }
        tmp.add(idx, lot);
        byProduct.clear();
        byProduct.addAll(tmp);
    }

    // Перемещение между локациями кол-вом (снимаем партиями FEFO)
    public double move(Location from, Location to, Product p, double amount) {
        if ( amount <= 0 ) return 0;
        var byProduct = storage.get(from).get(p);
        if ( byProduct == null || byProduct.isEmpty() ) return 0;

        double remaining = amount;
        while (remaining > 1e-9 && !byProduct.isEmpty()) {
            Lot head = byProduct.peekFirst();
            double take = Math.min(remaining, head.qty());
            // создаём «срез» партии для приёмника
            Lot movedPiece = new Lot(p, take, head.expiry());
            add(to, movedPiece);
            head.subtract(take);
            remaining -= take;
            if ( head.isEmpty() ) byProduct.removeFirst();
        }
        double moved = amount - remaining;
        // чистим пустые карты
        if ( byProduct != null && byProduct.isEmpty() ) storage.get(from).remove(p);
        return moved;
    }

    // Списание с продажи из ЗАЛА (FLOOR), FEFO
    public double deductFromFloor(Product p, double amount) {
        return deduct(Location.FLOOR, p, amount);
    }

    private double deduct(Location loc, Product p, double amount) {
        if ( amount <= 0 ) return 0;
        var byProduct = storage.get(loc).get(p);
        if ( byProduct == null || byProduct.isEmpty() ) return 0;

        double remaining = amount;
        while (remaining > 1e-9 && !byProduct.isEmpty()) {
            Lot head = byProduct.peekFirst();
            double take = Math.min(remaining, head.qty());
            head.subtract(take);
            remaining -= take;
            if ( head.isEmpty() ) byProduct.removeFirst();
        }
        double deducted = amount - remaining;
        if ( byProduct != null && byProduct.isEmpty() ) storage.get(loc).remove(p);
        return deducted;
    }

    // Удаление просрочки по всем продуктам/партиям в локации
    public int removeExpired(Location loc, LocalDate today) {
        int removedLots = 0;
        var map = storage.get(loc);
        for (var it = map.entrySet().iterator(); it.hasNext(); ) {
            var e = it.next();
            Deque<Lot> q = e.getValue();
            List<Lot> keep = new ArrayList<>();
            for (Lot lot : q) {
                if ( lot.expiry() != null && lot.expiry().isBefore(today) ) {
                    removedLots++;
                } else {
                    keep.add(lot);
                }
            }
            if ( keep.isEmpty() ) it.remove();
            else {
                q.clear();
                q.addAll(keep);
            }
        }
        return removedLots;
    }

    public Map<Product, Double> totalByLocation(Location loc) {
        Map<Product, Double> res = new HashMap<>();
        var map = storage.get(loc);
        for (var e : map.entrySet()) {
            double sum = 0;
            for (Lot lot : e.getValue()) sum += lot.qty();
            res.put(e.getKey(), sum);
        }
        return res;
    }
}