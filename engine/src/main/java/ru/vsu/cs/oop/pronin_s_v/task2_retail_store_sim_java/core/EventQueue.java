package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core;

import java.util.PriorityQueue;

/**
 * Приоритетная очередь событий.
 * <p>
 * События упорядочиваются по дате, приоритету и типу.
 * Движок ({@link SimEngine}) получает события из неё в корректном порядке,
 * чтобы симуляция выполнялась последовательно и детерминированно.
 */
public class EventQueue {

    /** Очередь с естественным порядком событий по compareTo. */
    private final PriorityQueue<Event> pq = new PriorityQueue<>();

    /**
     * Добавляет событие в очередь.
     *
     * @param e событие
     */
    public void add(Event e) {
        pq.add(e);
    }

    /**
     * Извлекает следующее по приоритету событие.
     *
     * @return событие или null, если очередь пуста
     */
    public Event poll() {
        return pq.poll();
    }

    /**
     * Проверяет, пуста ли очередь.
     *
     * @return true, если нет событий
     */
    public boolean isEmpty() {
        return pq.isEmpty();
    }
}