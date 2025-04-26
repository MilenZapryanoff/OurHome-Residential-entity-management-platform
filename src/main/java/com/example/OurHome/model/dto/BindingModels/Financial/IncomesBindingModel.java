package com.example.OurHome.model.dto.BindingModels.Financial;

import java.math.BigDecimal;

public class IncomesBindingModel {

    private BigDecimal incomesAmount;
    private BigDecimal incomesFundMm;
    private BigDecimal incomesFundRepair;
    private BigDecimal unpaidFeesAmount;
    private BigDecimal expectedTotalFundMmMonthlyIncome;
    private BigDecimal expectedTotalFundRepairMonthlyIncome;
    private BigDecimal expectedTotalMonthlyIncome;

    public BigDecimal getIncomesAmount() {
        return incomesAmount;
    }

    public void setIncomesAmount(BigDecimal incomesAmount) {
        this.incomesAmount = incomesAmount;
    }

    public BigDecimal getIncomesFundMm() {
        return incomesFundMm;
    }

    public void setIncomesFundMm(BigDecimal incomesFundMm) {
        this.incomesFundMm = incomesFundMm;
    }

    public BigDecimal getIncomesFundRepair() {
        return incomesFundRepair;
    }

    public void setIncomesFundRepair(BigDecimal incomesFundRepair) {
        this.incomesFundRepair = incomesFundRepair;
    }

    public BigDecimal getUnpaidFeesAmount() {
        return unpaidFeesAmount;
    }

    public void setUnpaidFeesAmount(BigDecimal unpaidFeesAmount) {
        this.unpaidFeesAmount = unpaidFeesAmount;
    }

    public BigDecimal getExpectedTotalFundMmMonthlyIncome() {
        return expectedTotalFundMmMonthlyIncome;
    }

    public void setExpectedTotalFundMmMonthlyIncome(BigDecimal expectedTotalFundMmMonthlyIncome) {
        this.expectedTotalFundMmMonthlyIncome = expectedTotalFundMmMonthlyIncome;
    }

    public BigDecimal getExpectedTotalFundRepairMonthlyIncome() {
        return expectedTotalFundRepairMonthlyIncome;
    }

    public void setExpectedTotalFundRepairMonthlyIncome(BigDecimal expectedTotalFundRepairMonthlyIncome) {
        this.expectedTotalFundRepairMonthlyIncome = expectedTotalFundRepairMonthlyIncome;
    }

    public BigDecimal getExpectedTotalMonthlyIncome() {
        return expectedTotalMonthlyIncome;
    }

    public void setExpectedTotalMonthlyIncome(BigDecimal expectedTotalMonthlyIncome) {
        this.expectedTotalMonthlyIncome = expectedTotalMonthlyIncome;
    }
}
