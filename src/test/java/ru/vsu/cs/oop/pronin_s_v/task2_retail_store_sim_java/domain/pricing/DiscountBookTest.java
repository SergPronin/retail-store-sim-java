package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.pricing;

import org.junit.jupiter.api.Test;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.MeasureType;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.Product;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.pricing.DiscountBook;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.pricing.DiscountRule;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты DiscountBook:
 * - комбинирование товарных и категорийных скидок
 * - очистка просроченных правил
 */
class DiscountBookTest {

    private final Product MILK = new Product(
            "MILK01", "Молоко 1л", "молочные", MeasureType.UNIT, true, 70
    );

    @Test
    void maxDiscountFor_shouldReturnMaxOfProductAndCategoryRules() {
        DiscountBook book = new DiscountBook();
        LocalDate today = LocalDate.of(2025, 11, 13);

        // 10% скидка на конкретное молоко
        book.add(DiscountRule.forProduct(MILK, 0.10, today.plusDays(5)));
        // 20% скидка на категорию "молочные"
        book.add(DiscountRule.forCategory("молочные", 0.20, today.plusDays(5)));

        double disc = book.maxDiscountFor(MILK, today);
        assertEquals(0.20, disc, 1e-9);
    }

    @Test
    void purgeExpired_shouldRemoveOldRules() {
        DiscountBook book = new DiscountBook();
        LocalDate today = LocalDate.of(2025, 11, 13);

        // просроченное правило
        book.add(DiscountRule.forProduct(MILK, 0.10, today.minusDays(1)));
        // актуальное правило
        book.add(DiscountRule.forCategory("молочные", 0.25, today.plusDays(3)));

        int removed = book.purgeExpired(today);
        assertEquals(1, removed);

        double disc = book.maxDiscountFor(MILK, today);
        assertEquals(0.25, disc, 1e-9);
    }
}