package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java;

import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.Inventory;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.reporting.DayStats;

public class AppContext {
    public static final Inventory inventory = new Inventory();
    public static final DayStats dayStats = new DayStats();
    private AppContext() { }
}