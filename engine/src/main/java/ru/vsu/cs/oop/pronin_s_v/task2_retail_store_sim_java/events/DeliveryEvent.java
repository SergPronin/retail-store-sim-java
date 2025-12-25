package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.events;

import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.AppContext;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.app.DemoCatalog;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core.Event;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core.RandomEx;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Событие поставки товаров на склад.
 * <p>
 * При выполнении:
 * <ul>
 *     <li>выбирается 2–4 случайных товара из каталога;</li>
 *     <li>для каждого генерируется количество (штучное или весовое);</li>
 *     <li>назначается срок годности для скоропортящихся товаров;</li>
 *     <li>в инвентарь добавляются новые партии в локацию {@link Location#WAREHOUSE}.</li>
 * </ul>
 */
public class DeliveryEvent extends Event {

    private final RandomEx rnd;

    /**
     * @param when дата поставки
     * @param rnd  генератор случайных чисел
     */
    public DeliveryEvent(LocalDate when, RandomEx rnd) {
        super(when);
        this.rnd = rnd;
    }

    @Override
    public void apply() {
        List<Product> products = DemoCatalog.products();
        int deliveries = rnd.range(2, 4); // 2–4 разных товаров

        for (int i = 0; i < deliveries; i++) {
            Product p = products.get(rnd.range(0, products.size() - 1));
            double qty = (p.measure() == MeasureType.UNIT)
                    ? rnd.range(20, 80)          // штучные товары
                    : rnd.range(10.0, 50.0);     // весовые (кг)

            LocalDate expiry = p.perishable()
                    ? when.plusDays(rnd.range(2, 7))
                    : null;

            AppContext.inventory.add(Location.WAREHOUSE, new Lot(p, qty, expiry));
            System.out.printf("[%s] Поставка: %-12s qty=%6.2f expiry=%s -> СКЛАД%n",
                    when, p.name(), qty, String.valueOf(expiry));
        }
    }

    /**
     * Поставка должна происходить до выкладки и покупок,
     * поэтому ей присвоен более высокий приоритет (меньшее число).
     */
    @Override
    public int priority() {
        return 10;
    }
}