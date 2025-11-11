package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core;

import java.time.LocalDate;

/**
 * Дискретные сутки. Управляет текущей датой симуляции.
 */
public class Clock {
    private LocalDate today;

    public Clock(LocalDate start) {
        this.today = start;
    }

    /** Текущая дата симуляции. */
    public LocalDate today() {
        return today;
    }

    /** Переход к следующему дню. */
    public void nextDay() {
        today = today.plusDays(1);
    }
}