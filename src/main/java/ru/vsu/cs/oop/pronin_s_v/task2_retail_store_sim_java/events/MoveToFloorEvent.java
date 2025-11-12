package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.events;

import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.AppContext;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core.Event;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core.RandomEx;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.*;

import java.time.LocalDate;
import java.util.Map;

/** Событие перемещения товаров со склада в торговый зал. */
public class MoveToFloorEvent extends Event {
    private final RandomEx rnd;

    public MoveToFloorEvent(LocalDate when, RandomEx rnd) {
        super(when);
        this.rnd = rnd;
    }

    @Override
    public void apply() {
        Map<Product, Double> warehouseStock = AppContext.inventory.totalByLocation(Location.WAREHOUSE);
        if (warehouseStock.isEmpty()) {
            System.out.printf("[%s] Выкладка: на складе пусто%n", when);
            return;
        }

        Map<Product, Double> floorStock = AppContext.inventory.totalByLocation(Location.FLOOR);

        for (var entry : warehouseStock.entrySet()) {
            Product product = entry.getKey();
            double target = (product.measure() == MeasureType.UNIT)
                    ? rnd.range(10, 30)
                    : rnd.range(5.0, 15.0);
            double onFloor = floorStock.getOrDefault(product, 0.0);
            double need = Math.max(0, target - onFloor);

            if (need > 0) {
                double moved = AppContext.inventory.move(Location.WAREHOUSE, Location.FLOOR, product, need);
                if (moved > 0) {
                    System.out.printf("[%s] Выкладка: %-12s moved=%6.2f -> ЗАЛ%n", when, product.name(), moved);
                }
            }
        }
    }
}