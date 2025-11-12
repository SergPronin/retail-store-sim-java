package ru.vsu.cs.oop.pronin_s_v.task2_retail_store_sim_java.app;

/**
 * Константы симуляции (на следующих шагах будем использовать).
 */
public class Config {
    public static final int SIM_DAYS_DEFAULT = 3;

    // Вероятности событий, горизонты скидок — будут задействованы в следующих фичах
    public static final double P_DELIVERY = 0.6;
    public static final double P_MOVE_TO_FLOOR = 0.7;
    public static final double P_SET_DISCOUNT = 0.4;
    public static final double P_REMOVE_EXPIRED = 0.9;

    public static final int PERISHABLE_HORIZON_DAYS = 2;
    public static final double PERISHABLE_DISCOUNT = 0.30;

    public static final int CUSTOMERS_PER_DAY_MIN = 8;
    public static final int CUSTOMERS_PER_DAY_MAX = 16;

    public static final double DISCOUNT_EVENT_PROB = 0.3;
}