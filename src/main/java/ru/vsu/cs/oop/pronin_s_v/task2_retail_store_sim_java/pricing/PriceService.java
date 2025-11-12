// src/main/java/.../pricing/PriceService.java
package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.pricing;

import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/** Сервис цен: basePrice -> применяем скидки (переборка + активные акции). */
public class PriceService {
    private final int perishableHorizonDays;
    private final double perishableDiscount; // 0..1
    private final DiscountBook discountBook;

    public PriceService(int horizonDays, double discount, DiscountBook book) {
        if (discount < 0 || discount > 0.7) throw new IllegalArgumentException("Скидка вне диапазона");
        this.perishableHorizonDays = horizonDays;
        this.perishableDiscount = discount;
        this.discountBook = book;
    }

    public BigDecimal retailPrice(Product p, LocalDate today, LocalDate expiryOrNull) {
        double base = p.basePrice();
        double autoDisc = 0.0;

        if (p.perishable() && expiryOrNull != null) {
            long days = ChronoUnit.DAYS.between(today, expiryOrNull);
            if (days <= perishableHorizonDays) autoDisc = perishableDiscount;
        }

        double promoDisc = discountBook != null ? discountBook.maxDiscountFor(p, today) : 0.0;
        double finalDisc = Math.max(autoDisc, promoDisc);
        double price = base * (1.0 - finalDisc);

        return BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP);
    }
}