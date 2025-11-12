package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core;

import java.time.LocalDate;

import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.app.Config;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.events.DeliveryEvent;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.events.MoveToFloorEvent;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.events.RemoveExpiredEvent;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.events.PurchaseEvent;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.pricing.PriceService;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.reporting.Reporter;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.AppContext;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.events.SetDiscountEvent;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.AppContext;



/** Движок: идём по дням, планируем и выполняем события. */
public class SimEngine {
    private final Clock clock;
    private final EventQueue queue;
    private final RandomEx rnd;

    // сервис цен/скидок — один на весь движок
    private final PriceService priceService =
            new PriceService(Config.PERISHABLE_HORIZON_DAYS, Config.PERISHABLE_DISCOUNT, AppContext.discountBook);

    public SimEngine(Clock clock, EventQueue queue, RandomEx rnd) {
        this.clock = clock;
        this.queue = queue;
        this.rnd = rnd;
    }

    /** Планирование событий на конкретный день. */
    public void seedDay(LocalDate d) {
        // 1) Чистка просрочки — в начале дня
        if (rnd.chance(0.9)) {
            queue.add(new RemoveExpiredEvent(d));
        }

        if (rnd.chance(Config.DISCOUNT_EVENT_PROB)) { // шанс появления акции сегодня
            queue.add(new SetDiscountEvent(d, rnd, AppContext.discountBook));
            // можно создать 1-2 события
            if (rnd.chance(0.2)) {
                queue.add(new SetDiscountEvent(d, rnd, AppContext.discountBook));
            }
        }
        // 2) Поставка на склад
        if (rnd.chance(0.6)) {
            queue.add(new DeliveryEvent(d, rnd));
        }
        // 3) Выкладка в зал
        if (rnd.chance(0.7)) {
            queue.add(new MoveToFloorEvent(d, rnd));
        }
        // 4) Покупатели (после выкладки)
        int buyers = rnd.range(Config.CUSTOMERS_PER_DAY_MIN, Config.CUSTOMERS_PER_DAY_MAX);
        for (int i = 0; i < buyers; i++) {
            queue.add(new PurchaseEvent(d, rnd, priceService));
        }
    }

    /** Выполнить N дней симуляции: seed -> run -> nextDay. */
    public void runDays(int days) {
        for (int i = 0; i < days; i++) {
            LocalDate d = clock.today();

            System.out.printf("=== День %s ===%n", d);
            seedDay(d);

            // Выполняем все события, добавленные на сегодня
            while (true) {
                Event e = queue.poll();
                if (e == null) break;
                if (!e.when.equals(d)) { // не наш день — вернём назад и выйдем
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