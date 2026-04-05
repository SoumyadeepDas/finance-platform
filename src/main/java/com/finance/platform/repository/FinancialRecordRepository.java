package com.finance.platform.repository;

import com.finance.platform.entity.FinancialRecord;
import com.finance.platform.enums.RecordType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository for financial record persistence and aggregations.
 */
@Repository
public interface FinancialRecordRepository
        extends JpaRepository<FinancialRecord, Long>, JpaSpecificationExecutor<FinancialRecord> {

    /**
     * Returns an active record by ID.
     */
    Optional<FinancialRecord> findByIdAndDeletedFalse(Long id);

    /**
     * Returns active records with pagination.
     */
    Page<FinancialRecord> findByDeletedFalse(Pageable pageable);

    /**
     * Returns the latest active records.
     */
    List<FinancialRecord> findTop10ByDeletedFalseOrderByDateDescCreatedAtDesc();

    /**
     * Returns the amount total for a record type.
     */
    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM FinancialRecord r " +
           "WHERE r.type = :type AND r.deleted = false")
    BigDecimal sumByType(@Param("type") RecordType type);

    /**
     * Counts active records.
     */
    long countByDeletedFalse();

    /**
     * Returns category totals and counts.
     */
    @Query("SELECT r.category, SUM(r.amount), COUNT(r) " +
           "FROM FinancialRecord r " +
           "WHERE r.deleted = false " +
           "GROUP BY r.category " +
           "ORDER BY SUM(r.amount) DESC")
    List<Object[]> aggregateByCategory();

    /**
     * Returns month-wise income and expense totals.
     */
    @Query("SELECT YEAR(r.date), MONTH(r.date), " +
           "COALESCE(SUM(CASE WHEN r.type = 'INCOME' THEN r.amount ELSE 0 END), 0), " +
           "COALESCE(SUM(CASE WHEN r.type = 'EXPENSE' THEN r.amount ELSE 0 END), 0) " +
           "FROM FinancialRecord r " +
           "WHERE r.deleted = false " +
           "GROUP BY YEAR(r.date), MONTH(r.date) " +
           "ORDER BY YEAR(r.date) DESC, MONTH(r.date) DESC")
    List<Object[]> monthlyTrends();
}
