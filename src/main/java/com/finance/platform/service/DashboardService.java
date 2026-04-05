package com.finance.platform.service;

import com.finance.platform.dto.CategoryAggregation;
import com.finance.platform.dto.DashboardSummary;
import com.finance.platform.dto.MonthlyTrend;
import com.finance.platform.dto.RecordResponse;

import java.util.List;

/**
 * Dashboard analytics service contract.
 */
public interface DashboardService {

    DashboardSummary getSummary();

    List<CategoryAggregation> getCategoryBreakdown();

    List<MonthlyTrend> getMonthlyTrends();

    List<RecordResponse> getRecentActivity();
}
