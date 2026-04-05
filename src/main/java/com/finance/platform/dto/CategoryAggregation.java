package com.finance.platform.dto;

import java.math.BigDecimal;

/**
 * Response payload for category totals.
 */
public class CategoryAggregation {

    private String category;
    private BigDecimal totalAmount;
    private long count;

    public CategoryAggregation(String category, BigDecimal totalAmount, long count) {
        this.category = category;
        this.totalAmount = totalAmount;
        this.count = count;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public long getCount() {
        return count;
    }
}
