package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain;

/**
 * Локация партии товара.
 * <ul>
 *     <li>{@link #WAREHOUSE} — склад (закрытая зона хранения);</li>
 *     <li>{@link #FLOOR} — торговый зал (доступно для покупателей).</li>
 * </ul>
 */
public enum Location {
    /** Склад магазина. */
    WAREHOUSE,

    /** Торговый зал. */
    FLOOR
}