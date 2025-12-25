package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core;

import java.time.LocalDate;

import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.app.Config;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.events.*;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.pricing.PriceService;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.reporting.Reporter;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.AppContext;

/**
 * Главный движок симуляции.
 * <p>
 * Выполняет цикл:
 * <ol>
 *     <li>Планирование событий на день ({@link #seedDay(LocalDate)})</li>
 *     <li>Выполнение событий в корректном порядке ({@link EventQueue})</li>
 *     <li>Отчёт за день ({@link Reporter})</li>
 *     <li>Переход к следующему дню ({@link Clock#nextDay()})</li>
 * </ol>
 * Движок детерминирован при одинаковом seed.
 */
public class SimEngine {

    private final Clock clock;
    private final EventQueue queue;
    private final RandomEx rnd;

    /** Единый сервис расчёта цен и скидок. */
    private final PriceService priceService =
            new PriceService(Config.PERISHABLE_HORIZON_DAYS, Config.PERISHABLE_DISCOUNT, AppContext.discountBook);

    /**
     * @param clock источник текущей даты
     * @param queue очередь событий
     * @param rnd   детерминированный генератор случайностей
     */
    public SimEngine(Clock clock, EventQueue queue, RandomEx rnd) {
        this.clock = clock;
        this.queue = queue;
        this.rnd = rnd;
    }

    /**
     * Планирует события на конкретный день.
     * <p>
     * Возможные события:
     * <ul>
     *     <li>удаление просрочки</li>
     *     <li>установка скидки</li>
     *     <li>поставка товаров</li>
     *     <li>выкладка в торговый зал</li>
     *     <li>покупатели</li>
     * </ul>
     *
     * @param d дата
     */
    public void seedDay(LocalDate d) {

        if (rnd.chance(Config.P_REMOVE_EXPIRED)) {
            queue.add(new RemoveExpiredEvent(d));
        }

        if (rnd.chance(Config.DISCOUNT_EVENT_PROB)) {
            queue.add(new SetDiscountEvent(d, rnd, AppContext.discountBook));
            if (rnd.chance(0.2)) {
                queue.add(new SetDiscountEvent(d, rnd, AppContext.discountBook));
            }
        }

        if (rnd.chance(Config.P_DELIVERY)) {
            queue.add(new DeliveryEvent(d, rnd));
        }

        if (rnd.chance(Config.P_MOVE_TO_FLOOR)) {
            queue.add(new MoveToFloorEvent(d, rnd));
        }

        int buyers = rnd.range(Config.CUSTOMERS_PER_DAY_MIN, Config.CUSTOMERS_PER_DAY_MAX);
        for (int i = 0; i < buyers; i++) {
            queue.add(new PurchaseEvent(d, rnd, priceService));
        }
    }

    /**
     * Запускает симуляцию на указанное количество дней.
     * <p>
     * Для каждого дня:
     * <ul>
     *     <li>планируются события</li>
     *     <li>выполняются все события этого дня</li>
     *     <li>печатается дневной отчёт</li>
     * </ul>
     *
     * @param days число дней симуляции
     */
    public void runDays(int days) {
        for (int i = 0; i < days; i++) {

            LocalDate d = clock.today();
            System.out.printf("=== День %s ===%n", d);

            seedDay(d);

            while (true) {
                Event e = queue.poll();
                if (e == null) break;
                if (!e.when.equals(d)) {
                    queue.add(e);
                    break;
                }
                e.apply();
            }

            Reporter.printEndOfDay(d, AppContext.dayStats);
            AppContext.dayStats.reset();

            clock.nextDay();
        }
    }
}