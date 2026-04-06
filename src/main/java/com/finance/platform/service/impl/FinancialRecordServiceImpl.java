package com.finance.platform.service.impl;

import com.finance.platform.dto.CreateRecordRequest;
import com.finance.platform.dto.RecordResponse;
import com.finance.platform.dto.UpdateRecordRequest;
import com.finance.platform.entity.FinancialRecord;
import com.finance.platform.enums.RecordType;
import com.finance.platform.exception.ResourceNotFoundException;
import com.finance.platform.repository.FinancialRecordRepository;
import com.finance.platform.service.FinancialRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Default implementation of financial record operations.
 */
@Service
public class FinancialRecordServiceImpl implements FinancialRecordService {

    private static final Logger log = LoggerFactory.getLogger(FinancialRecordServiceImpl.class);

    private final FinancialRecordRepository recordRepository;

    public FinancialRecordServiceImpl(FinancialRecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @Override
    @Transactional
    public RecordResponse createRecord(CreateRecordRequest request, Long authenticatedUserId) {
        if (authenticatedUserId == null || authenticatedUserId <= 0) {
            throw new IllegalStateException("Authenticated user context is required to create records");
        }

        FinancialRecord record = new FinancialRecord();
        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(normalizeCategory(request.getCategory()));
        record.setDate(request.getDate());
        record.setDescription(normalizeDescription(request.getDescription()));

        record.setCreatedBy(authenticatedUserId);

        FinancialRecord saved = recordRepository.save(record);

        log.info("Created financial record: id={}, type={}, amount={}, createdBy={}",
                saved.getId(), saved.getType(), saved.getAmount(), saved.getCreatedBy());

        return RecordResponse.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RecordResponse getRecordById(Long id) {
        FinancialRecord record = findActiveRecordOrThrow(id);
        return RecordResponse.from(record);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RecordResponse> getAllRecords(Pageable pageable, LocalDate startDate,
                                              LocalDate endDate, String category, RecordType type) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("endDate must be on or after startDate");
        }

        return recordRepository.findAll(buildFilters(startDate, endDate, category, type), pageable)
                .map(RecordResponse::from);
    }

    @Override
    @Transactional
    public RecordResponse updateRecord(Long id, UpdateRecordRequest request) {
        FinancialRecord record = findActiveRecordOrThrow(id);

        record.setAmount(request.getAmount());
        record.setType(request.getType());
        record.setCategory(normalizeCategory(request.getCategory()));
        record.setDate(request.getDate());
        record.setDescription(normalizeDescription(request.getDescription()));

        FinancialRecord saved = recordRepository.save(record);

        log.info("Updated financial record: id={}, type={}, amount={}",
                saved.getId(), saved.getType(), saved.getAmount());

        return RecordResponse.from(saved);
    }

    @Override
    @Transactional
    public void softDeleteRecord(Long id) {
        FinancialRecord record = findActiveRecordOrThrow(id);

        record.setDeleted(true);
        recordRepository.save(record);

        log.warn("Soft-deleted financial record: id={}, amount={}, type={}, createdBy={}",
                record.getId(), record.getAmount(), record.getType(), record.getCreatedBy());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecordResponse> getRecentTransactions() {
        return recordRepository.findTop10ByDeletedFalseOrderByDateDescCreatedAtDesc()
                .stream()
                .map(RecordResponse::from)
                .collect(Collectors.toList());
    }

    private Specification<FinancialRecord> buildFilters(LocalDate startDate, LocalDate endDate,
                                                        String category, RecordType type) {
        Specification<FinancialRecord> spec = Specification.where(notDeleted());

        if (startDate != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("date"), startDate));
        }

        if (endDate != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("date"), endDate));
        }

        if (category != null && !category.isBlank()) {
            String normalizedCategory = category.trim().toUpperCase();
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category"), normalizedCategory));
        }

        if (type != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type));
        }

        return spec;
    }

    private Specification<FinancialRecord> notDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }

    /**
     * Loads a record that is still active.
     */
    private FinancialRecord findActiveRecordOrThrow(Long id) {
        return recordRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Financial record not found with id: " + id));
    }

    private String normalizeCategory(String category) {
        return category.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeDescription(String description) {
        if (description == null) {
            return null;
        }

        String trimmed = description.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
