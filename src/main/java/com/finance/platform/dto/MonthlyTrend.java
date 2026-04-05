package com.finance.platform.dto;

import java.math.BigDecimal;

/**
 * Response payload for monthly trend data.
 */
public class MonthlyTrend {

    private int year;
    private int month;
    private BigDecimal income;
    private BigDecimal expense;

    public MonthlyTrend(int year, int month, BigDecimal income, BigDecimal expense) {
        this.year = year;
        this.month = month;
        this.income = income;
        this.expense = expense;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public BigDecimal getExpense() {
        return expense;
    }
}
