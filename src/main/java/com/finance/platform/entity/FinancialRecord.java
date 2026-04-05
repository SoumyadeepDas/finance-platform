package com.finance.platform.entity;

import com.finance.platform.enums.RecordType;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Persistent financial record model.
 */
@Entity
@Table(name = "financial_records", indexes = {
    @Index(name = "idx_record_date", columnList = "date"),
    @Index(name = "idx_record_created_by", columnList = "createdBy"),
    @Index(name = "idx_record_type", columnList = "type"),
    @Index(name = "idx_record_category", columnList = "category"),
    @Index(name = "idx_record_deleted_date", columnList = "deleted, date")
})
public class FinancialRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private RecordType type;

    @Column(nullable = false, length = 50)
    private String category;

    /** Business date of the transaction. */
    @Column(nullable = false)
    private LocalDate date;

    @Column(length = 500)
    private String description;

    /** Identifier of the user who created the record. */
    @Column(nullable = false, updatable = false)
    private Long createdBy;

    /** Soft-delete flag used to retain record history. */
    @Column(nullable = false)
    private boolean deleted = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public FinancialRecord() {
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public RecordType getType() {
        return type;
    }

    public void setType(RecordType type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
