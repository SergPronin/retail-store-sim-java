package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core;

import java.time.LocalDate;

/**
 * Модель дискретного времени в симуляции.
 * <p>
 * Класс инкапсулирует текущие «симуляционные» сутки и позволяет
 * пошагово переводить систему вперёд. Каждое событие выполняется
 * строго в рамках одной даты, а {@link SimEngine} двигает время через
 * {@link #nextDay()}.
 */
public class Clock {

    /** Текущая дата симуляции. */
    private LocalDate today;

    /**
     * Создаёт часы, начинающиеся с указанной даты.
     *
     * @param start дата начала симуляции
     */
    public Clock(LocalDate start) {
        this.today = start;
    }

    /**
     * Возвращает текущие сутки в модели.
     *
     * @return текущая дата симуляции
     */
    public LocalDate today() {
        return today;
    }

    /**
     * Переходит к следующим суткам.
     * <p>
     * Вся симуляция построена вокруг дискретных дней, поэтому
     * именно здесь происходит движение времени.
     */
    public void nextDay() {
        today = today.plusDays(1);
    }
}