package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.people;

import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core.RandomEx;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.MeasureType;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.Product;

import java.util.*;

/**
 * Модель покупателя в супермаркете.
 * <p>
 * Каждый покупатель характеризуется:
 * <ul>
 *     <li>{@code id} — уникальный идентификатор покупателя;</li>
 *     <li>{@code budget} — случайно сгенерированный бюджет (300–1500 руб.);</li>
 *     <li>{@code lovesDiscounts} — склонность покупать товары со скидками;</li>
 *     <li>{@code favCategories} — набор из 1–2 любимых категорий товаров.</li>
 * </ul>
 *
 * Модель используется в {@link ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.events.PurchaseEvent}
 * для генерации поведения покупателя при покупке:
 * <ul>
 *     <li>какие товары он предпочитает;</li>
 *     <li>какое количество товара готов взять;</li>
 *     <li>вписывается ли покупка в бюджет.</li>
 * </ul>
 */
public class Customer {

    /** Уникальный код покупателя. */
    public final int id;

    /** Бюджет покупателя (в рублях). Генерируется случайно. */
    public final double budget;

    /**
     * Флаг: покупатель любит скидки.
     * <p>
     * Пока логика скидок в Customer не используется напрямую,
     * но заложена для расширения (например: выбирать товары со скидкой в приоритете).
     */
    public final boolean lovesDiscounts;

    /** Любимые категории покупателя (обычно 1–2). */
    private final Set<String> favCategories;

    /**
     * Создаёт нового покупателя со случайными характеристиками.
     *
     * @param id            идентификатор покупателя
     * @param rnd           генератор случайных значений
     * @param allCategories список всех категорий товаров из каталога
     */
    public Customer(int id, RandomEx rnd, List<String> allCategories) {
        this.id = id;

        // Покупатель имеет бюджет от 300 до 1500 рублей
        this.budget = rnd.range(300, 1500);

        // 50% шанс, что покупатель любит товары со скидками
        this.lovesDiscounts = rnd.chance(0.5);

        // Выбираем 1–2 любимые категории
        Set<String> pick = new HashSet<>();
        while (pick.size() < Math.min(2, allCategories.size())) {
            pick.add(allCategories.get(rnd.range(0, allCategories.size() - 1)));
        }
        this.favCategories = pick;
    }

    /**
     * Проверяет, нравится ли покупателю данный товар.
     *
     * @param p товар
     * @return {@code true}, если категория товара входит в любимые категории покупателя
     */
    public boolean likes(Product p) {
        return favCategories.contains(p.category());
    }

    /**
     * Возвращает желаемое количество товара, которое покупатель хочет приобрести.
     * <p>
     * Завязано на тип измерения:
     * <ul>
     *     <li>штучные товары — 1…4 шт</li>
     *     <li>весовые товары — 0.3…1.2 кг</li>
     * </ul>
     *
     * @param p   товар
     * @param rnd генератор случайных чисел
     * @return количество товара (шт или кг)
     */
    public double desiredQty(Product p, RandomEx rnd) {
        return p.measure() == MeasureType.UNIT
                ? rnd.range(1, 4)
                : rnd.range(0.3, 1.2);
    }
}