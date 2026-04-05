package com.finance.platform.controller;

import com.finance.platform.dto.CreateRecordRequest;
import com.finance.platform.dto.RecordResponse;
import com.finance.platform.dto.UpdateRecordRequest;
import com.finance.platform.enums.RecordType;
import com.finance.platform.security.AuthenticatedUserProvider;
import com.finance.platform.service.FinancialRecordService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

/**
 * Endpoints for creating, reading, updating, and soft-deleting financial records.
 */
@RestController
@RequestMapping("/api/records")
public class FinancialRecordController {

    private final FinancialRecordService recordService;
    private final AuthenticatedUserProvider authProvider;

    public FinancialRecordController(FinancialRecordService recordService,
                                     AuthenticatedUserProvider authProvider) {
        this.recordService = recordService;
        this.authProvider = authProvider;
    }

    /**
     * Creates a financial record.
     */
    @PostMapping
    public ResponseEntity<RecordResponse> createRecord(
            @Valid @RequestBody CreateRecordRequest request) {
        Long userId = authProvider.getCurrentUserId();
        RecordResponse created = recordService.createRecord(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * GET /api/records/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<RecordResponse> getRecord(@PathVariable Long id) {
        return ResponseEntity.ok(recordService.getRecordById(id));
    }

    /**
     * Returns paginated records with optional filters.
     */
    @GetMapping
    public ResponseEntity<Page<RecordResponse>> getAllRecords(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) RecordType type,
            @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(recordService.getAllRecords(
                pageable, startDate, endDate, category, type));
    }

    /**
     * Returns the most recent transactions.
     */
    @GetMapping("/recent")
    public ResponseEntity<List<RecordResponse>> getRecentTransactions() {
        return ResponseEntity.ok(recordService.getRecentTransactions());
    }

    /**
     * Replaces all editable fields of a record.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RecordResponse> updateRecord(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRecordRequest request) {
        return ResponseEntity.ok(recordService.updateRecord(id, request));
    }

    /**
     * Soft-deletes a record.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecord(@PathVariable Long id) {
        recordService.softDeleteRecord(id);
        return ResponseEntity.noContent().build();
    }
}
