package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain;

import java.time.LocalDate;

public class Lot {
    private final Product product;
    private double qty;            // для UNIT — целые, для WEIGHT — дробные кг
    private final LocalDate expiry; // может быть null для нескоропортов

    public Lot(Product product, double qty, LocalDate expiry) {
        if (qty <= 0) throw new IllegalArgumentException("Количество должно быть > 0");
        this.product = product;
        this.qty = qty;
        this.expiry = expiry;
    }

    public Product product() {
        return product;
    }

    public double qty() {
        return qty;
    }

    public void subtract(double amount) {
        if ( amount < 0 ) throw new IllegalArgumentException("Нельзя списать отрицательное");
        if ( amount > qty ) throw new IllegalArgumentException("Нельзя списать больше, чем в партии");
        qty -= amount;
    }

    public boolean isEmpty() {
        return qty <= 1e-9;
    }

    public LocalDate expiry() {
        return expiry;
    }
}