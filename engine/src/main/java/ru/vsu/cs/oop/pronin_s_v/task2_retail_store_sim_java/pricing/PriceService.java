package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.pricing;

import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Сервис расчёта конечной розничной цены товара.
 *
 * <p>Учитывает два типа скидок:
 * <ul>
 *     <li><b>автоматическая скидка</b> — для скоропортящихся товаров,
 *         срок годности которых подходит к концу;</li>
 *     <li><b>промо-скидки</b> — на товар или категорию (из {@link DiscountBook}).</li>
 * </ul>
 *
 * Итоговая скидка = максимум(автоматическая, промо).
 */
public class PriceService {

    private final int perishableHorizonDays;
    private final double perishableDiscount; // 0..1
    private final DiscountBook discountBook;

    /**
     * @param horizonDays      порог «скоро истечёт» для скоропортящихся товаров
     * @param discount         автоматическая скидка (0..0.7)
     * @param book             книга скидок (может быть null)
     */
    public PriceService(int horizonDays, double discount, DiscountBook book) {
        if (discount < 0 || discount > 0.7)
            throw new IllegalArgumentException("Автоскидка вне диапазона 0–0.7");

        this.perishableHorizonDays = horizonDays;
        this.perishableDiscount = discount;
        this.discountBook = book;
    }

    /**
     * Рассчитывает розничную цену товара с учётом скидок.
     *
     * @param p             товар
     * @param today         текущая дата
     * @param expiryOrNull  срок годности или null
     * @return цена с округлением до 2 знаков
     */
    public BigDecimal retailPrice(Product p, LocalDate today, LocalDate expiryOrNull) {

        double base = p.basePrice();

        // автоматическая скидка
        double autoDisc = 0.0;
        if (p.perishable() && expiryOrNull != null) {
            long days = ChronoUnit.DAYS.between(today, expiryOrNull);
            if (days <= perishableHorizonDays) {
                autoDisc = perishableDiscount;
            }
        }

        // промо-скидки
        double promoDisc =
                discountBook != null ? discountBook.maxDiscountFor(p, today) : 0.0;

        // итоговая максимальная
        double finalDisc = Math.max(autoDisc, promoDisc);

        double price = base * (1.0 - finalDisc);

        return BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP);
    }
}