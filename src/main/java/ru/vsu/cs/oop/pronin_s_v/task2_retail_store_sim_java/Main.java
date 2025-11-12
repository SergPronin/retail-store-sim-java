package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java;

import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.app.Config;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core.Clock;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core.EventQueue;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core.RandomEx;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core.SimEngine;

import java.time.LocalDate;

/**
 * Точка входа CLI-приложения.
 *
 * Формат запуска:
 * <pre>
 *     java -jar retail-store-sim.jar [days] [seed]
 * </pre>
 *
 * <p>
 * Параметры:
 * <ul>
 *     <li><b>days</b> — количество дней симуляции (по умолчанию {@link Config#SIM_DAYS_DEFAULT})</li>
 *     <li><b>seed</b> — сид генератора случайных чисел (по умолчанию 42)</li>
 * </ul>
 * </p>
 */
public class Main {

    public static void main(String[] args) {

        int days = (args.length >= 1)
                ? Integer.parseInt(args[0])
                : Config.SIM_DAYS_DEFAULT;

        long seed = (args.length >= 2)
                ? Long.parseLong(args[1])
                : 42L;

        var clock = new Clock(LocalDate.now());
        var queue = new EventQueue();
        var rnd   = new RandomEx(seed);

        var engine = new SimEngine(clock, queue, rnd);

        System.out.printf("Старт симуляции: days=%d, seed=%d%n", days, seed);
        engine.runDays(days);
        System.out.println("Симуляция завершена.");
    }
}