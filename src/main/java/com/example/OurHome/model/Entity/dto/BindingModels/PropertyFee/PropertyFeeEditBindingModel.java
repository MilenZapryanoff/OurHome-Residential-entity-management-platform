package com.example.OurHome.model.Entity.dto.BindingModels.PropertyFee;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PropertyFeeEditBindingModel {

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

    @NotNull(message = "Overpayment amount cannot be null")
    @DecimalMin(value = "0.0", message = "Fixed Fee must be greater than or equal to zero")
    private BigDecimal overPayment;

    private boolean isPaid;

    private Long propertyId;

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

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
