package com.example.OurHome.model.dto.BindingModels.Financial;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class IncomesBindingModel {

    private BigDecimal incomesAmount;

    public BigDecimal getIncomesAmount() {
        return incomesAmount;
    }

    public void setIncomesAmount(BigDecimal incomesAmount) {
        this.incomesAmount = incomesAmount;
    }
}
