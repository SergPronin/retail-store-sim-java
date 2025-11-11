package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core;

import java.util.Random;

/**
 * Утилиты поверх Random: шанс и диапазоны.
 */
public class RandomEx extends Random {
    public RandomEx(long seed) {
        super(seed);
    }

    /** true с вероятностью p (0..1). */
    public boolean chance(double p) {
        return nextDouble() < p;
    }

    /** Целочисленный диапазон [minIncl..maxIncl]. */
    public int range(int minIncl, int maxIncl) {
        if (maxIncl < minIncl) throw new IllegalArgumentException("Диапазон некорректен");
        return minIncl + nextInt(maxIncl - minIncl + 1);
    }

    /** Вещественный диапазон [minIncl..maxIncl]. */
    public double range(double minIncl, double maxIncl) {
        if (maxIncl < minIncl) throw new IllegalArgumentException("Диапазон некорректен");
        return minIncl + nextDouble() * (maxIncl - minIncl);
    }
}