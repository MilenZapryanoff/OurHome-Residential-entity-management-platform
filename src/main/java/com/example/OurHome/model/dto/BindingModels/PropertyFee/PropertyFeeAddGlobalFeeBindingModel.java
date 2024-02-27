package com.example.OurHome.model.dto.BindingModels.PropertyFee;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PropertyFeeAddGlobalFeeBindingModel {

    private Long id;

    @NotNull(message = "Fee amount cannot be null")
    @DecimalMin(value = "0.0", message = "Fixed Fee must be greater than or equal to zero")
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
}
