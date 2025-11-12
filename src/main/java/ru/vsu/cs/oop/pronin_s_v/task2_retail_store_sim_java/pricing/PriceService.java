package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.pricing;

import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PriceService {
    private final int perishableHorizonDays;
    private final double perishableDiscount; // 0..1

    public PriceService(int horizonDays, double discount) {
        if (discount < 0 || discount > 0.7) throw new IllegalArgumentException("Скидка вне диапазона");
        this.perishableHorizonDays = horizonDays;
        this.perishableDiscount = discount;
    }

    public BigDecimal retailPrice(Product p, LocalDate today, LocalDate expiryOrNull) {
        double base = p.basePrice();
        double discount = 0.0;
        if (p.perishable() && expiryOrNull != null) {
            long days = ChronoUnit.DAYS.between(today, expiryOrNull);
            if (days <= perishableHorizonDays) discount = perishableDiscount;
        }
        double price = base * (1.0 - discount);
        return BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP);
    }
}