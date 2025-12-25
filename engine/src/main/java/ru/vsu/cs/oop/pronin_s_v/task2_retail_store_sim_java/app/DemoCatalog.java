package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.app;

import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.MeasureType;
import ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain.Product;

import java.util.List;

/**
 * Предустановленный каталог товаров.
 * <p>
 * Используется как источник базовых продуктов для MVP-версии симуляции,
 * чтобы не загружать данные извне и не строить полноценный справочник.
 * <br>
 * Каждый товар описан:
 * <ul>
 *   <li>кодом товара</li>
 *   <li>читаемым названием</li>
 *   <li>категорией</li>
 *   <li>типом измерения (штучный или весовой)</li>
 *   <li>наличием срока годности</li>
 *   <li>базовой ценой</li>
 * </ul>
 */
public class DemoCatalog {

    /**
     * Возвращает список существующих продуктов.
     *
     * @return неизменяемый список Product для использования в симуляции
     */
    public static List<Product> products() {
        return List.of(
                new Product("MEAT01",  "Говядина",       "мясо",
                        MeasureType.WEIGHT, true, 600),

                new Product("BREAD1",  "Батон",          "хлебобулочные",
                        MeasureType.UNIT,   true,  40),

                new Product("MILK01",  "Молоко 1л",      "молочные",
                        MeasureType.UNIT,   true,  70),

                new Product("SOAP01",  "Мыло",           "бытовая химия",
                        MeasureType.UNIT,  false,  50),

                new Product("APPLE1", "Яблоки",          "овощи-фрукты",
                        MeasureType.WEIGHT, true, 120)
        );
    }

    /** Утилитарный класс — экземпляры не создаются. */
    private DemoCatalog() { }
}