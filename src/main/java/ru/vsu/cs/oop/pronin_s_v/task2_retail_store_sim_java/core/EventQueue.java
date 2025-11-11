package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core;

import java.util.PriorityQueue;

/**
 * Очередь событий по дате (и типу) — минимально необходимая реализация.
 */
public class EventQueue {
    private final PriorityQueue<Event> pq = new PriorityQueue<>();

    public void add(Event e) {
        pq.add(e);
    }

    public Event poll() {
        return pq.poll();
    }

    public boolean isEmpty() {
        return pq.isEmpty();
    }
}