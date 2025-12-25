package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain;

import java.util.Objects;

/**
 * Описание товара в каталоге супермаркета.
 * <p>
 * Хранит неизменяемые свойства продукта:
 * <ul>
 *     <li>артикул ({@link #code});</li>
 *     <li>название ({@link #name});</li>
 *     <li>категорию (мясо, молочка, хлебобулочные и т.д., {@link #category});</li>
 *     <li>тип измерения ({@link #measure});</li>
 *     <li>признак скоропортящегося товара ({@link #perishable});</li>
 *     <li>базовую цену ({@link #basePrice}) за единицу или кг.</li>
 * </ul>
 * Логика скидок и акций реализована отдельно в слое ценообразования.
 */
public class Product {

    private final String code;
    private final String name;
    private final String category;
    private final MeasureType measure;
    private final boolean perishable;
    private final double basePrice;

    /**
     * Создаёт новый товар.
     *
     * @param code       артикул товара
     * @param name       отображаемое название
     * @param category   категория (для группировки и акций)
     * @param measure    тип измерения (штучный/весовой)
     * @param perishable {@code true}, если товар скоропортящийся
     * @param basePrice  базовая цена за единицу или килограмм (должна быть ≥ 0)
     */
    public Product(String code, String name, String category,
                   MeasureType measure, boolean perishable, double basePrice) {
        if (basePrice < 0) throw new IllegalArgumentException("Цена не может быть отрицательной");
        this.code = Objects.requireNonNull(code);
        this.name = Objects.requireNonNull(name);
        this.category = Objects.requireNonNull(category);
        this.measure = Objects.requireNonNull(measure);
        this.perishable = perishable;
        this.basePrice = basePrice;
    }

    /** @return артикул товара */
    public String code() {
        return code;
    }

    /** @return название товара */
    public String name() {
        return name;
    }

    /** @return категория товара (используется в предпочтениях и акциях) */
    public String category() {
        return category;
    }

    /** @return тип измерения товара (штучный или весовой) */
    public MeasureType measure() {
        return measure;
    }

    /** @return {@code true}, если товар скоропортящийся */
    public boolean perishable() {
        return perishable;
    }

    /** @return базовая цена без учёта скидок */
    public double basePrice() {
        return basePrice;
    }

    @Override
    public String toString() {
        return code + " " + name + " (" + category + ")";
    }
}