package com.finance.platform.dto;

import java.math.BigDecimal;

/**
 * Aggregated financial summary.
 *
 * All values are computed via SQL aggregation, NOT in Java.
 * The service layer assembles this from multiple repository queries.
 */
public class DashboardSummary {

    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netBalance;
    private long recordCount;

    public DashboardSummary(BigDecimal totalIncome, BigDecimal totalExpenses,
                            BigDecimal netBalance, long recordCount) {
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.netBalance = netBalance;
        this.recordCount = recordCount;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public BigDecimal getNetBalance() {
        return netBalance;
    }

    public long getRecordCount() {
        return recordCount;
    }
}
