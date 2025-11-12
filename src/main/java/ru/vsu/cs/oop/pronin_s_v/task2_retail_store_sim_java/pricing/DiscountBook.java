package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.pricing;

import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.Product;

import java.time.LocalDate;
import java.util.*;

/**
 * Хранилище всех активных скидочных правил.
 * <p>
 * Поддерживает два типа скидок:
 * <ul>
 *     <li>на конкретный товар (product code);</li>
 *     <li>на категорию товаров.</li>
 * </ul>
 *
 * Внутри используются две карты:
 * <pre>
 * productRules:  productCode → [list of rules]
 * categoryRules: category    → [list of rules]
 * </pre>
 *
 * Для товара одновременно могут действовать:
 * <ul>
 *     <li>несколько товарных скидок,</li>
 *     <li>несколько категорийных скидок.</li>
 * </ul>
 * Итоговая скидка — максимальная из всех возможных.
 */
public class DiscountBook {

    /** Скидки на конкретные товары: productCode → список правил. */
    private final Map<String, List<DiscountRule>> productRules = new HashMap<>();

    /** Скидки на категории товаров: category → список правил. */
    private final Map<String, List<DiscountRule>> categoryRules = new HashMap<>();

    /**
     * Добавляет новое скидочное правило.
     *
     * <p>Правило не заменяет старые — скидки могут накапливаться.
     * Итоговая скидка при расчёте определяется как максимальная.</p>
     *
     * @param rule правило скидки
     */
    public void add(DiscountRule rule) {
        Map<String, List<DiscountRule>> target =
                rule.scope() == DiscountRule.Scope.PRODUCT ? productRules : categoryRules;

        target.computeIfAbsent(rule.key(), k -> new ArrayList<>()).add(rule);
    }

    /**
     * Возвращает максимальную скидку (0..1) для товара {@code p} на дату {@code d}.
     * <p>
     * Проверяются:
     * <ul>
     *     <li>все скидки по коду товара;</li>
     *     <li>все скидки по категории товара.</li>
     * </ul>
     */
    public double maxDiscountFor(Product p, LocalDate d) {
        double best = 0.0;

        // товарные правила
        var listP = productRules.getOrDefault(p.code(), Collections.emptyList());
        for (var r : listP) {
            if (r.activeOn(d)) best = Math.max(best, r.percent());
        }

        // категорийные правила
        var listC = categoryRules.getOrDefault(p.category(), Collections.emptyList());
        for (var r : listC) {
            if (r.activeOn(d)) best = Math.max(best, r.percent());
        }

        return best;
    }

    /**
     * Удаляет все скидки, срок действия которых закончился.
     *
     * @param d текущая дата
     * @return количество удалённых правил
     */
    public int purgeExpired(LocalDate d) {
        return purgeMap(productRules, d) + purgeMap(categoryRules, d);
    }

    /** Вспомогательный метод очистки карты скидок. */
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

        // удаляем пустые ключи
        map.entrySet().removeIf(en -> en.getValue().isEmpty());

        return removed;
    }
}