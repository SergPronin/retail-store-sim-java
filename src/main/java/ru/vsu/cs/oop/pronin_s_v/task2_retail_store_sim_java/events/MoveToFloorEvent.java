package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.events;

import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.AppContext;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core.Event;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core.RandomEx;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.*;

import java.time.LocalDate;
import java.util.Map;

public class MoveToFloorEvent extends Event {
    private final RandomEx rnd;

    public MoveToFloorEvent(LocalDate when, RandomEx rnd) {
        super(when);
        this.rnd = rnd;
    }

    @Override
    public void apply() {
        Map<Product, Double> wh = AppContext.inventory.totalByLocation(Location.WAREHOUSE);
        if (wh.isEmpty()) {
            System.out.printf("[%s] Выкладка: на складе пусто%n", when);
            return;
        }
        Map<Product, Double> floor = AppContext.inventory.totalByLocation(Location.FLOOR);

        for (var e : wh.entrySet()) {
            Product p = e.getKey();
            double target = (p.measure() == MeasureType.UNIT) ? rnd.range(10, 30) : rnd.range(5.0, 15.0);
            double onFloor = floor.getOrDefault(p, 0.0);
            double need = Math.max(0, target - onFloor);
            if (need > 0) {
                double moved = AppContext.inventory.move(Location.WAREHOUSE, Location.FLOOR, p, need);
                if ( moved > 0 ) {
                    System.out.printf("[%s] Выкладка: %-12s moved=%6.2f -> ЗАЛ%n", when, p.name(), moved);
                }
            }
        }
    }
}