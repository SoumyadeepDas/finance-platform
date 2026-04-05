package com.finance.platform.service.impl;

import com.finance.platform.dto.CategoryAggregation;
import com.finance.platform.dto.DashboardSummary;
import com.finance.platform.dto.MonthlyTrend;
import com.finance.platform.dto.RecordResponse;
import com.finance.platform.enums.RecordType;
import com.finance.platform.repository.FinancialRecordRepository;
import com.finance.platform.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Default implementation of dashboard analytics operations.
 */
@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final FinancialRecordRepository recordRepository;

    public DashboardServiceImpl(FinancialRecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @Override
    public DashboardSummary getSummary() {
        BigDecimal totalIncome = recordRepository.sumByType(RecordType.INCOME);
        BigDecimal totalExpenses = recordRepository.sumByType(RecordType.EXPENSE);
        BigDecimal netBalance = totalIncome.subtract(totalExpenses);
        long recordCount = recordRepository.countByDeletedFalse();

        return new DashboardSummary(totalIncome, totalExpenses, netBalance, recordCount);
    }

    @Override
    public List<CategoryAggregation> getCategoryBreakdown() {
        return recordRepository.aggregateByCategory().stream()
                .map(row -> new CategoryAggregation(
                        (String) row[0],
                        (BigDecimal) row[1],
                        (Long) row[2]
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<MonthlyTrend> getMonthlyTrends() {
        return recordRepository.monthlyTrends().stream()
                .map(row -> new MonthlyTrend(
                        (Integer) row[0],
                        (Integer) row[1],
                        (BigDecimal) row[2],
                        (BigDecimal) row[3]
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<RecordResponse> getRecentActivity() {
        return recordRepository.findTop10ByDeletedFalseOrderByDateDescCreatedAtDesc()
                .stream()
                .map(RecordResponse::from)
                .collect(Collectors.toList());
    }
}
