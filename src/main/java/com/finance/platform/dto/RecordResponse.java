package com.finance.platform.dto;

import com.finance.platform.entity.FinancialRecord;
import com.finance.platform.enums.RecordType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response payload for financial records.
 */
public class RecordResponse {

    private Long id;
    private BigDecimal amount;
    private RecordType type;
    private String category;
    private LocalDate date;
    private String description;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static RecordResponse from(FinancialRecord record) {
        RecordResponse dto = new RecordResponse();
        dto.id = record.getId();
        dto.amount = record.getAmount();
        dto.type = record.getType();
        dto.category = record.getCategory();
        dto.date = record.getDate();
        dto.description = record.getDescription();
        dto.createdBy = record.getCreatedBy();
        dto.createdAt = record.getCreatedAt();
        dto.updatedAt = record.getUpdatedAt();
        return dto;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public RecordType getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
