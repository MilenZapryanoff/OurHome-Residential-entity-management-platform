package com.example.OurHome.model.dto.BindingModels.PropertyFee;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public class OverpaymentBindingModel {

    @DecimalMin(value = "0.0", message = "Overpayment amount must be greater than or equal to zero")
    private BigDecimal overPayment;

    @DecimalMin(value = "0.0", message = "Additional fee amount must be greater than or equal to zero")
    private BigDecimal additionalPropertyFee;

    public BigDecimal getOverPayment() {
        return overPayment;
    }

    public void setOverPayment(BigDecimal overPayment) {
        this.overPayment = overPayment;
    }

    public BigDecimal getAdditionalPropertyFee() {
        return additionalPropertyFee;
    }

    public void setAdditionalPropertyFee(BigDecimal additionalPropertyFee) {
        this.additionalPropertyFee = additionalPropertyFee;
    }
}
