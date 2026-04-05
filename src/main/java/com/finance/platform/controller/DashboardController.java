package com.finance.platform.controller;

import com.finance.platform.dto.CategoryAggregation;
import com.finance.platform.dto.DashboardSummary;
import com.finance.platform.dto.MonthlyTrend;
import com.finance.platform.dto.RecordResponse;
import com.finance.platform.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Read-only endpoints for dashboard analytics.
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Returns income, expense, balance, and record count totals.
     */
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummary> getSummary() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }

    /**
     * Returns category totals.
     */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryAggregation>> getCategoryBreakdown() {
        return ResponseEntity.ok(dashboardService.getCategoryBreakdown());
    }

    /**
     * Returns monthly income and expense trends.
     */
    @GetMapping("/trends")
    public ResponseEntity<List<MonthlyTrend>> getMonthlyTrends() {
        return ResponseEntity.ok(dashboardService.getMonthlyTrends());
    }

    /**
     * Returns recent dashboard activity.
     */
    @GetMapping("/recent")
    public ResponseEntity<List<RecordResponse>> getRecentActivity() {
        return ResponseEntity.ok(dashboardService.getRecentActivity());
    }
}
