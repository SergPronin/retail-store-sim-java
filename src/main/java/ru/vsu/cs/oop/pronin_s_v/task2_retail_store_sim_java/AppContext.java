package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java;

import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.Inventory;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.reporting.DayStats;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.pricing.DiscountBook;

/**
 * Глобальный контекст приложения.
 *
 * <p>Хранит единые для всей симуляции объекты:</p>
 * <ul>
 *     <li>{@link Inventory} — состояние склада и торгового зала;</li>
 *     <li>{@link DayStats} — статистика текущего дня;</li>
 *     <li>{@link DiscountBook} — активные скидки;</li>
 * </ul>
 *
 * <p>Все объекты — синглтоны, создаются один раз и используются всеми событиями.</p>
 *
 * <p>Класс не предназначен для инстанцирования — конструктор приватен.</p>
 */
public class AppContext {

    /** Глобальный инвентарь магазина. */
    public static final Inventory inventory = new Inventory();

    /** Статистика текущего дня (обнуляется после отчёта). */
    public static final DayStats dayStats = new DayStats();

    /** Книга активных скидок. */
    public static final DiscountBook discountBook = new DiscountBook();

    private AppContext() { }
}