package com.example.OurHome.model.dto.BindingModels.PropertyFee;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class OverpaymentBindingModel {

    @DecimalMin(value = "0.0", message = "Fixed Fee must be greater than or equal to zero")
    private BigDecimal overPayment;

    public BigDecimal getOverPayment() {
        return overPayment;
    }

    public void setOverPayment(BigDecimal overPayment) {
        this.overPayment = overPayment;
    }
}
