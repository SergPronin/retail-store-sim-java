package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.events;

import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.AppContext;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.app.DemoCatalog;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core.Event;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core.RandomEx;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.Product;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.pricing.DiscountBook;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.pricing.DiscountRule;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/** Событие: установка скидки на товар или категорию. */
public class SetDiscountEvent extends Event {
    private final RandomEx rnd;
    private final DiscountBook book;

    public SetDiscountEvent(LocalDate when, RandomEx rnd, DiscountBook book) {
        super(when);
        this.rnd = rnd;
        this.book = book;
    }

    @Override
    public int priority() { return 5; } // после RemoveExpired(0), но до Delivery(10)

    @Override
    public void apply() {
        // С вероятностью 50% — скидка на категорию, иначе — на конкретный товар
        List<Product> all = DemoCatalog.products();
        boolean byCategory = rnd.chance(0.5);

        double percent = rnd.range(0.1, 0.35); // 10%..35%
        int durationDays = rnd.range(2, 5);    // 2..5 дней
        LocalDate until = when.plusDays(durationDays);

        DiscountRule rule;
        if (byCategory) {
            var cats = all.stream().map(Product::category).distinct().collect(Collectors.toList());
            String cat = cats.get(rnd.range(0, cats.size() - 1));
            rule = DiscountRule.forCategory(cat, percent, until);
            book.add(rule);
            System.out.printf("[%s] Акция: категория '%s' скидка %.0f%% до %s%n",
                    when, cat, percent * 100, until);
        } else {
            Product p = all.get(rnd.range(0, all.size() - 1));
            rule = DiscountRule.forProduct(p, percent, until);
            book.add(rule);
            System.out.printf("[%s] Акция: товар '%s' скидка %.0f%% до %s%n",
                    when, p.name(), percent * 100, until);
        }

        // Параллельно чистим истёкшие правила (чтобы не зарастало)
        int purged = AppContext.discountBook.purgeExpired(when);
        if (purged > 0) {
            System.out.printf("[%s] Акции: очищено просроченных правил: %d%n", when, purged);
        }
    }
}