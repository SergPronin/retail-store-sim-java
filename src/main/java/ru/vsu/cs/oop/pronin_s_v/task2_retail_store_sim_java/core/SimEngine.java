package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core;

import java.time.LocalDate;

import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.app.Config;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.events.DeliveryEvent;      // ← добавь
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.events.MoveToFloorEvent;   // ← добавь
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.events.RemoveExpiredEvent; // ← добавь
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.events.PurchaseEvent;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.pricing.PriceService;

/** Движок: идём по дням, планируем и выполняем события. */
public class SimEngine {
    private final Clock clock;
    private final EventQueue queue;
    private final RandomEx rnd;

    // сервис цен/скидок — один на весь движок
    private final PriceService priceService =
            new PriceService(Config.PERISHABLE_HORIZON_DAYS, Config.PERISHABLE_DISCOUNT);

    public SimEngine(Clock clock, EventQueue queue, RandomEx rnd) {
        this.clock = clock;
        this.queue = queue;
        this.rnd = rnd;
    }

    /** Планирование событий на конкретный день. */
    public void seedDay(LocalDate d) {
        // 1) Чистка просрочки — почти всегда в начале дня
        if (rnd.chance(0.9)) {
            queue.add(new RemoveExpiredEvent(d));
        }
        // 2) Поставка на склад
        if (rnd.chance(0.6)) {
            queue.add(new DeliveryEvent(d, rnd));
        }
        // 3) Выкладка в зал
        if (rnd.chance(0.7)) {
            queue.add(new MoveToFloorEvent(d, rnd));
        }
        // 4) Покупатели (после выкладки, чтобы было что покупать)
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

            clock.nextDay();
        }
    }
}