package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.pricing;

import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.Product;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Правило скидки.
 * <p>
 * Может относиться к:
 * <ul>
 *     <li>конкретному товару (scope=PRODUCT);</li>
 *     <li>категории товаров (scope=CATEGORY).</li>
 * </ul>
 *
 * Каждое правило содержит:
 * <ul>
 *     <li>{@code percent} — скидка (0..1),</li>
 *     <li>{@code until} — дата окончания действия (включительно),</li>
 *     <li>{@code key} — код товара или категорию.</li>
 * </ul>
 *
 * Правила создаются через фабрики:
 * <pre>
 * forProduct(p, 0.20, date);
 * forCategory("молочные", 0.15, date);
 * </pre>
 */
public final class DiscountRule {

    /** Область применения скидки. */
    public enum Scope { PRODUCT, CATEGORY }

    private final Scope scope;
    private final String key;
    private final double percent;
    private final LocalDate until;

    /** Скидка на конкретный товар. */
    public static DiscountRule forProduct(Product p, double percent, LocalDate until) {
        return new DiscountRule(Scope.PRODUCT, p.code(), percent, until);
    }

    /** Скидка на категорию товаров. */
    public static DiscountRule forCategory(String category, double percent, LocalDate until) {
        return new DiscountRule(Scope.CATEGORY, category, percent, until);
    }

    private DiscountRule(Scope scope, String key, double percent, LocalDate until) {
        if (percent < 0 || percent > 0.9)
            throw new IllegalArgumentException("Процент скидки вне диапазона 0–0.9");

        this.scope = Objects.requireNonNull(scope);
        this.key = Objects.requireNonNull(key);
        this.percent = percent;
        this.until = Objects.requireNonNull(until);
    }

    /** Возвращает тип скидки: товарная или категорийная. */
    public Scope scope() {
        return scope;
    }

    /** Возвращает код товара или название категории. */
    public String key() {
        return key;
    }

    /** Возвращает процент скидки (0..1). */
    public double percent() {
        return percent;
    }

    /** Дата окончания действия (включительно). */
    public LocalDate until() {
        return until;
    }

    /**
     * Проверяет, активна ли скидка на дату {@code d}.
     */
    public boolean activeOn(LocalDate d) {
        return !d.isAfter(until);
    }
}