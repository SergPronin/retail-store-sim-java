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

/**
 * Событие: покупка товаров одним покупателем.
 * <p>
 * Логика:
 * <ol>
 *     <li>Создаётся {@link Customer} c бюджетом и предпочтениями по категориям.</li>
 *     <li>Смотрим ассортимент торгового зала ({@link Location#FLOOR}).</li>
 *     <li>Сортируем товары по тому, насколько они нравятся покупателю.</li>
 *     <li>Пытаемся набрать несколько позиций, не выходя за границы бюджета.</li>
 *     <li>При успешной покупке:
 *          <ul>
 *              <li>списываем товар из инвентаря,</li>
 *              <li>считаем стоимость с учётом скидок ({@link PriceService}),</li>
 *              <li>фиксируем статистику продаж в {@link ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.reporting.DayStats}.</li>
 *          </ul>
 *     </li>
 * </ol>
 */
public class PurchaseEvent extends Event {

    private static int NEXT_ID = 1;

    private final RandomEx rnd;
    private final PriceService prices;

    /**
     * @param when   день покупки
     * @param rnd    генератор случайных чисел
     * @param prices сервис расчёта цен и скидок
     */
    public PurchaseEvent(LocalDate when, RandomEx rnd, PriceService prices) {
        super(when);
        this.rnd = rnd;
        this.prices = prices;
    }

    @Override
    public void apply() {
        var prods = DemoCatalog.products();
        var cats = prods.stream()
                .map(Product::category)
                .distinct()
                .collect(Collectors.toList());

        var customer = new Customer(NEXT_ID++, rnd, cats);

        // Ассортимент торгового зала
        List<Product> floorProducts =
                new ArrayList<>(AppContext.inventory.totalByLocation(Location.FLOOR).keySet());

        if (floorProducts.isEmpty()) {
            System.out.printf("[%s] Покупатель #%d: в зале пусто, покупка отменена%n",
                    when, customer.id);
            return;
        }

        // Сначала любимые категории покупателя
        floorProducts.sort((a, b) -> Boolean.compare(customer.likes(b), customer.likes(a)));

        int max = Math.min(5, floorProducts.size());
        int min = Math.min(2, max); // если всего 1 товар — min будет 1
        int want = rnd.range(min, max);

        BigDecimal total = BigDecimal.ZERO;
        Map<Product, Double> purchased = new LinkedHashMap<>();

        for (int i = 0; i < want; i++) {
            Product p = floorProducts.get(i);
            double qty = customer.desiredQty(p, rnd);

            // На уровне PriceService скидки завязаны на продукт/категорию и срок годности.
            // Здесь в простом варианте expiry не пробрасываем (можно расширить в будущем).
            LocalDate expiry = null;
            BigDecimal unitPrice = prices.retailPrice(p, when, expiry);
            BigDecimal cost = unitPrice.multiply(BigDecimal.valueOf(qty));

            if (total.add(cost).doubleValue() > customer.budget) {
                continue;
            }

            double deducted = AppContext.inventory.deductFromFloor(p, qty);
            if (deducted > 0) {
                purchased.put(p, deducted);

                BigDecimal line = unitPrice.multiply(BigDecimal.valueOf(deducted));
                total = total.add(line);

                AppContext.dayStats.addSale(p, deducted, line);
            }
        }

        if (purchased.isEmpty()) {
            System.out.printf("[%s] Покупатель #%d: ничего не куплено (бюджет=%.2f)%n",
                    when, customer.id, customer.budget);
            return;
        }

        AppContext.dayStats.addReceipt(total, purchased.size());

        System.out.printf("[%s] Покупатель #%d: чек=%.2f, позиций=%d (бюджет=%.2f)%n",
                when, customer.id, total.doubleValue(), purchased.size(), customer.budget);
        for (var e : purchased.entrySet()) {
            System.out.printf("   - %s x %.2f%n", e.getKey().name(), e.getValue());
        }
    }

    /**
     * Покупки выполняются после выкладки товаров.
     */
    @Override
    public int priority() {
        return 30;
    }
}