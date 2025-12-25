package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.events;

import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.AppContext;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.core.Event;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.Location;

import java.time.LocalDate;

/**
 * Событие удаления просроченных партий из инвентаря.
 * <p>
 * Чистка выполняется отдельно:
 * <ul>
 *     <li>на складе ({@link Location#WAREHOUSE});</li>
 *     <li>в торговом зале ({@link Location#FLOOR}).</li>
 * </ul>
 * Количество удалённых партий фиксируется в дневной статистике.
 */
public class RemoveExpiredEvent extends Event {

    /**
     * @param when день проверки на просрочку
     */
    public RemoveExpiredEvent(LocalDate when) {
        super(when);
    }

    @Override
    public void apply() {
        int fromWarehouse = AppContext.inventory.removeExpired(Location.WAREHOUSE, when);
        int fromFloor = AppContext.inventory.removeExpired(Location.FLOOR, when);

        AppContext.dayStats.addExpiredLots(fromWarehouse + fromFloor);

        System.out.printf("[%s] Просрочка удалена: склад=%d, зал=%d%n",
                when, fromWarehouse, fromFloor);
    }

    /**
     * Чистка просрочки должна идти в самом начале дня, до всех остальных действий.
     */
    @Override
    public int priority() {
        return 0;
    }
}