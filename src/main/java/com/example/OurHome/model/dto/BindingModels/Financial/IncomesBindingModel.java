package com.example.OurHome.model.dto.BindingModels.Financial;

import java.math.BigDecimal;

public class IncomesBindingModel {

    private BigDecimal incomesAmount;
    private BigDecimal incomesFundMm;
    private BigDecimal incomesFundRepair;

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
}
