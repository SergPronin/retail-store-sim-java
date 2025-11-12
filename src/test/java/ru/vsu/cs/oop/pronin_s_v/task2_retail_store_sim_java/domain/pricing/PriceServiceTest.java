package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.pricing;

import org.junit.jupiter.api.Test;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.MeasureType;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.Product;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.pricing.DiscountBook;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.pricing.DiscountRule;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.pricing.PriceService;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты PriceService:
 * - базовая цена без скидок
 * - автоматическая скидка на скоропорт
 * - промо-скидка, которая перекрывает авто-скидку
 */
class PriceServiceTest {

    private final Product MILK = new Product(
            "MILK01", "Молоко 1л", "молочные", MeasureType.UNIT, true, 70
    );
    private final Product SOAP = new Product(
            "SOAP01", "Мыло", "бытовая химия", MeasureType.UNIT, false, 50
    );

    @Test
    void retailPrice_nonPerishableWithoutDiscounts_shouldBeBasePrice() {
        PriceService service = new PriceService(2, 0.3, null);
        LocalDate today = LocalDate.of(2025, 11, 13);

        BigDecimal price = service.retailPrice(SOAP, today, null);
        assertEquals(BigDecimal.valueOf(50.00).setScale(2), price);
    }

    @Test
    void retailPrice_perishableNearExpiry_shouldApplyAutoDiscount() {
        DiscountBook book = new DiscountBook(); // без промо-скидок
        PriceService service = new PriceService(2, 0.3, book);
        LocalDate today = LocalDate.of(2025, 11, 13);
        LocalDate expiry = today.plusDays(1); // в горизонте

        BigDecimal price = service.retailPrice(MILK, today, expiry);
        // base=70, авто-скидка 30% → 49.00
        assertEquals(BigDecimal.valueOf(49.00).setScale(2), price);
    }

    @Test
    void retailPrice_promoDiscountHigherThanAuto_shouldUsePromo() {
        DiscountBook book = new DiscountBook();
        LocalDate today = LocalDate.of(2025, 11, 13);
        LocalDate expiry = today.plusDays(1); // триггерим авто-скидку

        // авто-скидка 30%
        PriceService service = new PriceService(2, 0.3, book);

        // промо-скидка 50% по категории
        book.add(DiscountRule.forCategory("молочные", 0.5, today.plusDays(3)));

        BigDecimal price = service.retailPrice(MILK, today, expiry);
        // base=70, промо 50% > авто 30% → 35.00
        assertEquals(BigDecimal.valueOf(35.00).setScale(2), price);
    }
}