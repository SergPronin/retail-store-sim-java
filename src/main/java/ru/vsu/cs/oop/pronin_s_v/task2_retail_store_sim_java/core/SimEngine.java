package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core;

import java.time.LocalDate;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.events.DeliveryEvent;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.events.MoveToFloorEvent;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.events.RemoveExpiredEvent;


/**
 * Движок: идём по дням, сеем события (пока пусто) и выполняем их.
 */
public class SimEngine {
    private final Clock clock;
    private final EventQueue queue;
    private final RandomEx rnd;

    public SimEngine(Clock clock, EventQueue queue, RandomEx rnd) {
        this.clock = clock;
        this.queue = queue;
        this.rnd = rnd;
    }

    public void seedDay(LocalDate d) {
        // 1) почти каждый день чистим просрочку в начале
        if (rnd.chance(0.9)) {
            queue.add(new RemoveExpiredEvent(d));
        }
        // 2) поставка на склад
        if (rnd.chance(0.6)) {
            queue.add(new DeliveryEvent(d, rnd));
        }
        // 3) выкладка в зал
        if (rnd.chance(0.7)) {
            queue.add(new MoveToFloorEvent(d, rnd));
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