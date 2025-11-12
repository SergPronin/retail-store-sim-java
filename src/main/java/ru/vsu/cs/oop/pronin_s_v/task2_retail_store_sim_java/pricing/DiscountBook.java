package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.pricing;

import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.Product;

import java.time.LocalDate;
import java.util.*;

/**
 * Хранилище активных скидок (на товар и/или категорию).
 */
public class DiscountBook {
    private final Map<String, List<DiscountRule>> productRules = new HashMap<>();
    private final Map<String, List<DiscountRule>> categoryRules = new HashMap<>();

    /**
     * Добавить правило; новое не затирает старые — одновременно могут действовать несколько.
     */
    public void add(DiscountRule rule) {
        Map<String, List<DiscountRule>> target = rule.scope() == DiscountRule.Scope.PRODUCT ? productRules : categoryRules;
        target.computeIfAbsent(rule.key(), k -> new ArrayList<>()).add(rule);
    }

    /**
     * Вернуть максимальную скидку (0..1) для товара p на дату d.
     */
    public double maxDiscountFor(Product p, LocalDate d) {
        double best = 0.0;

        var listP = productRules.getOrDefault(p.code(), Collections.emptyList());
        for (var r : listP) if ( r.activeOn(d) ) best = Math.max(best, r.percent());

        var listC = categoryRules.getOrDefault(p.category(), Collections.emptyList());
        for (var r : listC) if ( r.activeOn(d) ) best = Math.max(best, r.percent());

        return best;
    }

    /**
     * Удалить неактуальные правила (по дате). Возвращает сколько удалено.
     */
    public int purgeExpired(LocalDate d) {
        return purgeMap(productRules, d) + purgeMap(categoryRules, d);
    }

    private int purgeMap(Map<String, List<DiscountRule>> map, LocalDate d) {
        int removed = 0;
        for (var e : map.entrySet()) {
            var it = e.getValue().iterator();
            while (it.hasNext()) {
                var r = it.next();
                if (!r.activeOn(d)) {
                    it.remove();
                    removed++;
                }
            }
        }
        // чистим пустые ключи
        map.entrySet().removeIf(en -> en.getValue().isEmpty());
        return removed;
    }
}