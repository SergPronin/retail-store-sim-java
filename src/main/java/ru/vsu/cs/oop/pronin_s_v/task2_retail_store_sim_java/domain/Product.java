package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain;

import java.util.Objects;

public class Product {
    private final String code;      // артикул/ко д
    private final String name;      // название
    private final String category;  // категория (мясо, молочка и т.д.)
    private final MeasureType measure; // единица измерения
    private final boolean perishable;   // скоропорт
    private final double basePrice;     // базовая цена за шт/кг

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

    public String code() {
        return code;
    }

    public String name() {
        return name;
    }

    public String category() {
        return category;
    }

    public MeasureType measure() {
        return measure;
    }

    public boolean perishable() {
        return perishable;
    }

    public double basePrice() {
        return basePrice;
    }

    @Override
    public String toString() {
        return code + " " + name + " (" + category + ")";
    }
}