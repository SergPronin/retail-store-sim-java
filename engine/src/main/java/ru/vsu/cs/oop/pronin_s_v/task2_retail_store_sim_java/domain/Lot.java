package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.domain;

import java.time.LocalDate;

/**
 * Партия товара в инвентаре.
 * <p>
 * Описывает конкретный объём товара ({@link Product}) с:
 * <ul>
 *     <li>количеством ({@link #qty}) — штучным или весовым, зависит от {@link MeasureType};</li>
 *     <li>датой истечения срока годности ({@link #expiry}), может быть {@code null};</li>
 *     <li>ссылкой на продукт ({@link #product}).</li>
 * </ul>
 * Партия может частично списываться, пока количество не упадёт практически до нуля.
 */
public class Lot {

    private final Product product;
    /** Количество: штучное или весовое (кг). */
    private double qty;
    /** Дата истечения срока годности; {@code null} для нескоропортящихся товаров. */
    private final LocalDate expiry;

    /**
     * Создаёт новую партию товара.
     *
     * @param product товар
     * @param qty     количество (должно быть > 0)
     * @param expiry  срок годности или {@code null}, если не задан
     */
    public Lot(Product product, double qty, LocalDate expiry) {
        if (qty <= 0) throw new IllegalArgumentException("Количество должно быть > 0");
        this.product = product;
        this.qty = qty;
        this.expiry = expiry;
    }

    /**
     * @return товар этой партии
     */
    public Product product() {
        return product;
    }

    /**
     * @return текущее количество в партии
     */
    public double qty() {
        return qty;
    }

    /**
     * Списывает часть партии.
     *
     * @param amount сколько списать
     * @throws IllegalArgumentException если amount &lt; 0 или больше доступного количества
     */
    public void subtract(double amount) {
        if (amount < 0) throw new IllegalArgumentException("Нельзя списать отрицательное");
        if (amount > qty) throw new IllegalArgumentException("Нельзя списать больше, чем в партии");
        qty -= amount;
    }

    /**
     * Проверяет, считается ли партия пустой.
     * <p>
     * Используется допуск 1e-9 для работы с вещественными числами.
     *
     * @return true, если количество ~ 0
     */
    public boolean isEmpty() {
        return qty <= 1e-9;
    }

    /**
     * @return дата истечения срока годности или {@code null}, если не задана
     */
    public LocalDate expiry() {
        return expiry;
    }
}