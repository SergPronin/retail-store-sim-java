package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core;

import java.util.Random;

/**
 * Расширение стандартного {@link Random} для удобства симуляции.
 * <p>
 * Добавляет методы:
 * <ul>
 *     <li>{@link #chance(double)} — выпадение вероятности</li>
 *     <li>{@link #range(int, int)} — диапазон целых</li>
 *     <li>{@link #range(double, double)} — диапазон вещественных</li>
 * </ul>
 */
public class RandomEx extends Random {

    /**
     * Создаёт генератор с фиксированным seed, чтобы симуляция была детерминированной.
     *
     * @param seed зерно случайности
     */
    public RandomEx(long seed) {
        super(seed);
    }

    /**
     * Возвращает true с вероятностью {@code p}.
     *
     * @param p вероятность от 0.0 до 1.0
     * @return true, если событие произошло
     */
    public boolean chance(double p) {
        return nextDouble() < p;
    }

    /**
     * Возвращает случайное целое число в диапазоне [minIncl..maxIncl].
     *
     * @throws IllegalArgumentException если границы некорректны
     */
    public int range(int minIncl, int maxIncl) {
        if (maxIncl < minIncl) throw new IllegalArgumentException("Диапазон некорректен");
        return minIncl + nextInt(maxIncl - minIncl + 1);
    }

    /**
     * Возвращает случайное вещественное число в диапазоне [minIncl..maxIncl].
     *
     * @throws IllegalArgumentException если границы некорректны
     */
    public double range(double minIncl, double maxIncl) {
        if (maxIncl < minIncl) throw new IllegalArgumentException("Диапазон некорректен");
        return minIncl + nextDouble() * (maxIncl - minIncl);
    }
}