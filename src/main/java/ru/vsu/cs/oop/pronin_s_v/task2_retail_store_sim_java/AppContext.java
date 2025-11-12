package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java;

import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.Inventory;

/**
 * Простой контекст приложения: содержит общий инвентарь.
 * Это аналог DI-контейнера для нашей CLI-версии.
 */
public class AppContext {
    public static final Inventory inventory = new Inventory();

    private AppContext() { }
}