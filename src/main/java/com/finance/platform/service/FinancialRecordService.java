package com.finance.platform.service;

import com.finance.platform.dto.CreateRecordRequest;
import com.finance.platform.dto.RecordResponse;
import com.finance.platform.dto.UpdateRecordRequest;
import com.finance.platform.enums.RecordType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * Financial record service contract.
 */
public interface FinancialRecordService {

    RecordResponse createRecord(CreateRecordRequest request, Long authenticatedUserId);

    RecordResponse getRecordById(Long id);

    Page<RecordResponse> getAllRecords(Pageable pageable, LocalDate startDate,
                                       LocalDate endDate, String category, RecordType type);

    RecordResponse updateRecord(Long id, UpdateRecordRequest request);

    void softDeleteRecord(Long id);

    List<RecordResponse> getRecentTransactions();
}
