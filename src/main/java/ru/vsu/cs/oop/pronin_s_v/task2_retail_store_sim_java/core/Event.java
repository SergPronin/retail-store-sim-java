package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core;

import java.time.LocalDate;

/**
 * Базовый класс события симуляции: привязан к конкретному дню.
 * Comparable — чтобы события дня упорядочивались предсказуемо.
 */
public abstract class Event implements Comparable<Event> {
    public final LocalDate when;

    protected Event(LocalDate when) {
        this.when = when;
    }

    /**
     * Логика применения события.
     */
    public abstract void apply();

    @Override
    public int compareTo(Event o) {
        int c = when.compareTo(o.when);
        if (c != 0) return c;
        // Вторичный ключ — имя класса, чтобы порядок внутри дня был стабильным
        return getClass().getName().compareTo(o.getClass().getName());
    }
}