package com.example.OurHome.model.dto.BindingModels.PropertyFee;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PropertyFeeAddGlobalFeeBindingModel {

    private Long id;

    @PositiveOrZero(message = "Amount must be positive or 0")
    @Column(nullable = false)
    private BigDecimal fundRepairAmount;

    @PositiveOrZero(message = "Amount must be positive or 0")
    @Column(nullable = false)
    private BigDecimal fundMmAmount;

    @Positive(message = "Total fee amount must be bigger than 0")
    @Column
    private BigDecimal feeAmount;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Start period date cannot be null")
    private LocalDate periodStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "End period date cannot be null")
    private LocalDate periodEnd;

    @DecimalMin(value = "0.0", message = "Fixed Fee must be greater than or equal to zero")
    private BigDecimal overPayment;

    private boolean isPaid;

    @Size(max = 40, message = "Description must not be longer than 45 symbols")
    private String description;

    public BigDecimal getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(BigDecimal feeAmount) {
        this.feeAmount = feeAmount;
    }

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }

    public BigDecimal getOverPayment() {
        return overPayment;
    }

    public void setOverPayment(BigDecimal overPayment) {
        this.overPayment = overPayment;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getFundRepairAmount() {
        return fundRepairAmount;
    }

    public void setFundRepairAmount(BigDecimal fundRepairAmount) {
        this.fundRepairAmount = fundRepairAmount;
    }

    public BigDecimal getFundMmAmount() {
        return fundMmAmount;
    }

    public void setFundMmAmount(BigDecimal fundMmAmount) {
        this.fundMmAmount = fundMmAmount;
    }
}
