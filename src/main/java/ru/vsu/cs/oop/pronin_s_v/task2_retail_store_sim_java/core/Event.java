package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core;

import java.time.LocalDate;

public abstract class Event implements Comparable<Event> {
    public final LocalDate when;

    protected Event(LocalDate when) {
        this.when = when;
    }

    public int priority() {
        return 100;
    } // значение по умолчанию

    public abstract void apply();

    @Override
    public int compareTo(Event o) {
        int c = this.when.compareTo(o.when);
        if (c != 0) return c;
        // сначала по приоритету, потом чтобы порядок был стабильным — по имени класса
        c = Integer.compare(this.priority(), o.priority());
        if (c != 0) return c;
        return this.getClass().getName().compareTo(o.getClass().getName());
    }
}