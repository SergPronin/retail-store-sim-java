package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core;

import java.time.LocalDate;

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

    /** Накидываем события на конкретный день (пока заглушка-лог). */
    public void seedDay(LocalDate d) {
        // Здесь позже: RemoveExpired, Delivery, MoveToFloor, Discounts, Purchases и т.п.
        System.out.printf("[%s] Планировщик: событий пока нет (каркас)%n", d);
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