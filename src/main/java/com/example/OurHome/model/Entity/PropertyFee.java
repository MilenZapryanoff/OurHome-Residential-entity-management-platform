package com.example.OurHome.model.Entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity(name = "property_fees")
public class PropertyFee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal feeAmount;

    @Column(nullable = false)
    private BigDecimal fundRepairAmount;

    @Column(nullable = false)
    private BigDecimal fundMmAmount;

    @Column()
    private BigDecimal overpaidAmountStart;

    @Column()
    private BigDecimal overpaidAmountEnd;

    @Column(nullable = false)
    private BigDecimal dueAmount;

    @Column(nullable = false)
    private LocalDate periodStart;

    @Column(nullable = false)
    private LocalDate periodEnd;

    @Column
    private boolean isPaid;

    @Column
    private boolean isManual;

    @Column(length = 45)
    private String description;

    @ManyToOne
    private Property property;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isManual() {
        return isManual;
    }

    public void setManual(boolean manual) {
        isManual = manual;
    }

    public BigDecimal getOverpaidAmountStart() {
        return overpaidAmountStart;
    }

    public void setOverpaidAmountStart(BigDecimal overpaidAmountStart) {
        this.overpaidAmountStart = overpaidAmountStart;
    }

    public BigDecimal getOverpaidAmountEnd() {
        return overpaidAmountEnd;
    }

    public void setOverpaidAmountEnd(BigDecimal overpaidAmountEnd) {
        this.overpaidAmountEnd = overpaidAmountEnd;
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

    public BigDecimal getDueAmount() {
        return dueAmount;
    }

    public void setDueAmount(BigDecimal dueAmount) {
        this.dueAmount = dueAmount;
    }
}
