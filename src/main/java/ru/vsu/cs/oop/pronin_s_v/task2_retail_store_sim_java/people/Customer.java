package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.people;

import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core.RandomEx;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.MeasureType;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.Product;

import java.util.*;

public class Customer {
    public final int id;
    public final double budget;  // руб
    public final boolean lovesDiscounts;
    private final Set<String> favCategories;

    public Customer(int id, RandomEx rnd, List<String> allCategories) {
        this.id = id;
        this.budget = rnd.range(300, 1500);
        this.lovesDiscounts = rnd.chance(0.5);
        // выберем 2 любимые категории
        Set<String> pick = new HashSet<>();
        while (pick.size() < Math.min(2, allCategories.size())) {
            pick.add(allCategories.get(rnd.range(0, allCategories.size()-1)));
        }
        this.favCategories = pick;
    }

    public boolean likes(Product p) {
        return favCategories.contains(p.category());
    }

    public double desiredQty(Product p, RandomEx rnd) {
        return p.measure() == MeasureType.UNIT ? rnd.range(1, 4) : rnd.range(0.3, 1.2);
    }
}