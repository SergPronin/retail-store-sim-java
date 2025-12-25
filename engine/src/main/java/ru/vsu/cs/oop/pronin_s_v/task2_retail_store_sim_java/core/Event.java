package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core;

import java.time.LocalDate;

/**
 * Базовый класс всех событий симуляции.
 * <p>
 * Каждое событие имеет:
 * <ul>
 *     <li>дату выполнения ({@link #when})</li>
 *     <li>приоритет среди событий одного дня ({@link #priority()})</li>
 *     <li>действие, выполняемое методом {@link #apply()}</li>
 * </ul>
 * События сортируются в {@link EventQueue} по дате, затем по приоритету.
 */
public abstract class Event implements Comparable<Event> {

    /** Дата, на которую запланировано событие. */
    public final LocalDate when;

    /**
     * Создаёт событие, назначенное на конкретный день.
     *
     * @param when день выполнения события
     */
    protected Event(LocalDate when) {
        this.when = when;
    }

    /**
     * Приоритет события среди событий одного дня.
     * <p>
     * Меньшее число означает более высокий приоритет.
     * По умолчанию приоритет — средний (100).
     * Дочерние классы могут переопределять.
     *
     * @return приоритет
     */
    public int priority() {
        return 100;
    }

    /**
     * Логика выполнения события.
     * <p>
     * Реализуется каждой конкретной сущностью (поставка, выкладка,
     * покупка, удаление просрочки, установка скидки).
     */
    public abstract void apply();

    @Override
    public int compareTo(Event o) {
        int c = this.when.compareTo(o.when);
        if (c != 0) return c;

        c = Integer.compare(this.priority(), o.priority());
        if (c != 0) return c;

        // стабильный порядок при одинаковом приоритете
        return this.getClass().getName().compareTo(o.getClass().getName());
    }
}