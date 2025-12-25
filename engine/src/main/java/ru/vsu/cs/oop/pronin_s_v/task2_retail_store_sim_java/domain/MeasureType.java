package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain;

/**
 * Тип измерения товара.
 * <ul>
 *     <li>{@link #UNIT} — штучный товар (хлеб, бутылка, пачка);</li>
 *     <li>{@link #WEIGHT} — весовой товар (кг, граммы — интерпретируются как double).</li>
 * </ul>
 */
public enum MeasureType {

    /** Штучный товар. */
    UNIT,

    /** Весовой товар (количество хранится как double). */
    WEIGHT
}