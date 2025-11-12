package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.events;

import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.AppContext;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.app.DemoCatalog;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core.Event;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core.RandomEx;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.Location;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.Product;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.people.Customer;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.pricing.PriceService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class PurchaseEvent extends Event {
    private static int NEXT_ID = 1;
    private final RandomEx rnd;
    private final PriceService prices;

    public PurchaseEvent(LocalDate when, RandomEx rnd, PriceService prices) {
        super(when);
        this.rnd = rnd; this.prices = prices;
    }

    @Override
    public void apply() {
        var prods = DemoCatalog.products();
        var cats = prods.stream().map(Product::category).distinct().collect(Collectors.toList());
        var customer = new Customer(NEXT_ID++, rnd, cats);

        // Сформируем список кандидатов из ассортимента, даём приоритет любимым категориям
        List<Product> floorProducts = new ArrayList<>(AppContext.inventory.totalByLocation(Location.FLOOR).keySet());
        if (floorProducts.isEmpty()) {
            System.out.printf("[%s] Покупатель #%d: в зале пусто, покупка отменена%n", when, customer.id);
            return;
        }
        floorProducts.sort((a,b) -> Boolean.compare(customer.likes(b), customer.likes(a)));

        int want = rnd.range(2, Math.min(5, floorProducts.size()));
        BigDecimal total = BigDecimal.ZERO;
        Map<Product, Double> purchased = new LinkedHashMap<>();

        for (int i = 0; i < want; i++) {
            Product p = floorProducts.get(i);
            double qty = customer.desiredQty(p, rnd);

            // Цена берётся с учётом скидки по ближайшей партии на зале (приближённо)
            // (Для простоты: берём expiry ближайшей партии как ориентир скидки)
            LocalDate expiry = null; // нет публичного доступа к партиям — считаем, что скидка определяется политикой по today+expiry≈не знаем → ок
            var unitPrice = prices.retailPrice(p, when, expiry);
            BigDecimal cost = unitPrice.multiply(BigDecimal.valueOf(qty));

            if (total.add(cost).doubleValue() > customer.budget) continue; // не влезает — пропускаем

            double deducted = AppContext.inventory.deductFromFloor(p, qty);
            if (deducted > 0) {
                purchased.put(p, deducted);
                total = total.add(unitPrice.multiply(BigDecimal.valueOf(deducted)));
            }
        }

        if (purchased.isEmpty()) {
            System.out.printf("[%s] Покупатель #%d: ничего не куплено (бюджет=%.2f)%n",
                    when, customer.id, customer.budget);
            return;
        }

        // лог чека
        System.out.printf("[%s] Покупатель #%d: чек=%.2f, позиций=%d (бюджет=%.2f)%n",
                when, customer.id, total.doubleValue(), purchased.size(), customer.budget);
        for (var e : purchased.entrySet()) {
            System.out.printf("   - %s x %.2f%n", e.getKey().name(), e.getValue());
        }
    }
}