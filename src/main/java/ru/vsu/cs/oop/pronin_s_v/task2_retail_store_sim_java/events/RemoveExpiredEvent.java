package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.events;

import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.AppContext;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core.Event;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.Location;

import java.time.LocalDate;

public class RemoveExpiredEvent extends Event {
    public RemoveExpiredEvent(LocalDate when) {
        super(when);
    }

    @Override
    public void apply() {
        int a = AppContext.inventory.removeExpired(Location.WAREHOUSE, when);
        int b = AppContext.inventory.removeExpired(Location.FLOOR, when);
        System.out.printf("[%s] Просрочка удалена: склад=%d, зал=%d%n", when, a, b);
    }
}