package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.pricing;

import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.Product;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Правило скидки: либо на конкретный товар, либо на категорию.
 */
public final class DiscountRule {
    public enum Scope {PRODUCT, CATEGORY}

    private final Scope scope;
    private final String key;        // product.code() или category
    private final double percent;    // 0..1
    private final LocalDate until;   // включительно

    public static DiscountRule forProduct(Product p, double percent, LocalDate until) {
        return new DiscountRule(Scope.PRODUCT, p.code(), percent, until);
    }

    public static DiscountRule forCategory(String category, double percent, LocalDate until) {
        return new DiscountRule(Scope.CATEGORY, category, percent, until);
    }

    private DiscountRule(Scope scope, String key, double percent, LocalDate until) {
        if (percent < 0 || percent > 0.9) throw new IllegalArgumentException("Процент скидки вне диапазона");
        this.scope = Objects.requireNonNull(scope);
        this.key = Objects.requireNonNull(key);
        this.percent = percent;
        this.until = Objects.requireNonNull(until);
    }

    public Scope scope() {
        return scope;
    }

    public String key() {
        return key;
    }

    public double percent() {
        return percent;
    }

    public LocalDate until() {
        return until;
    }

    /**
     * Скидка активна на дату d?
     */
    public boolean activeOn(LocalDate d) {
        return !d.isAfter(until);
    }
}