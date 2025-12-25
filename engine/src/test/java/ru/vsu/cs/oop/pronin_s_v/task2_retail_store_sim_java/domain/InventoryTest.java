package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain;

import org.junit.jupiter.api.Test;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.*;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты для Inventory:
 * - добавление партий
 * - перемещение между локациями
 * - списание с пола
 * - удаление просроченных партий
 */
class InventoryTest {

    private final Product MEAT = new Product(
            "MEAT01", "Говядина", "мясо", MeasureType.WEIGHT, true, 600
    );
    private final Product BREAD = new Product(
            "BREAD1", "Батон", "хлебобулочные", MeasureType.UNIT, true, 40
    );

    @Test
    void addAndTotalByLocation_shouldAccumulateQuantities() {
        Inventory inv = new Inventory();

        inv.add(Location.WAREHOUSE, new Lot(MEAT, 10.0, LocalDate.now().plusDays(5)));
        inv.add(Location.WAREHOUSE, new Lot(MEAT, 5.0, LocalDate.now().plusDays(7)));

        Map<Product, Double> totals = inv.totalByLocation(Location.WAREHOUSE);
        assertEquals(1, totals.size());
        assertEquals(15.0, totals.get(MEAT), 1e-9);
    }

    @Test
    void move_shouldMovePartiallyAndLeaveRest() {
        Inventory inv = new Inventory();

        inv.add(Location.WAREHOUSE, new Lot(BREAD, 20.0, LocalDate.now().plusDays(3)));

        double moved = inv.move(Location.WAREHOUSE, Location.FLOOR, BREAD, 7.0);
        assertEquals(7.0, moved, 1e-9);

        Map<Product, Double> wh = inv.totalByLocation(Location.WAREHOUSE);
        Map<Product, Double> fl = inv.totalByLocation(Location.FLOOR);

        assertEquals(13.0, wh.get(BREAD), 1e-9);
        assertEquals(7.0, fl.get(BREAD), 1e-9);
    }

    @Test
    void deductFromFloor_shouldNotDeductMoreThanExists() {
        Inventory inv = new Inventory();

        inv.add(Location.FLOOR, new Lot(MEAT, 5.0, LocalDate.now().plusDays(2)));

        double d1 = inv.deductFromFloor(MEAT, 3.0);
        assertEquals(3.0, d1, 1e-9);

        double d2 = inv.deductFromFloor(MEAT, 5.0); // осталось только 2
        assertEquals(2.0, d2, 1e-9);

        Map<Product, Double> fl = inv.totalByLocation(Location.FLOOR);
        assertFalse(fl.containsKey(MEAT), "После полного списания партия должна исчезнуть");
    }

    @Test
    void removeExpired_shouldRemoveOnlyExpiredLots() {
        Inventory inv = new Inventory();
        LocalDate today = LocalDate.of(2025, 11, 13);

        inv.add(Location.WAREHOUSE, new Lot(MEAT, 5.0, today.minusDays(1))); // просрочено
        inv.add(Location.WAREHOUSE, new Lot(MEAT, 3.0, today.plusDays(2)));  // ещё годен
        inv.add(Location.WAREHOUSE, new Lot(BREAD, 2.0, null));              // бессрочный

        int removed = inv.removeExpired(Location.WAREHOUSE, today);
        assertEquals(1, removed);

        Map<Product, Double> wh = inv.totalByLocation(Location.WAREHOUSE);
        assertEquals(2, wh.size());
        assertEquals(3.0, wh.get(MEAT), 1e-9);
        assertEquals(2.0, wh.get(BREAD), 1e-9);
    }
}