package com.example.OurHome.model.dto.BindingModels.Fee;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class FeeEditBindingModel {

    @NotNull(message = "Fixed Fee Habitable cannot be null")
    @DecimalMin(value = "0.0", message = "Fixed Fee must be greater than or equal to zero")
    private BigDecimal fixedFeeHabitable;

    @NotNull(message = "Adult Fee cannot be null")
    @DecimalMin(value = "0.0", message = "Adult Fee must be greater than or equal to zero")
    private BigDecimal adultFee;

    @NotNull(message = "Child Fee cannot be null")
    @DecimalMin(value = "0.0", message = "Child Fee must be greater than or equal to zero")
    private BigDecimal childFee;

    @NotNull(message = "Pet Fee cannot be null")
    @DecimalMin(value = "0.0", message = "Pet Fee must be greater than or equal to zero")
    private BigDecimal petFee;

    @NotNull(message = "Additional Fee Habitable cannot be null")
    @DecimalMin(value = "0.0", message = "Additional Fee must be greater than or equal to zero")
    private BigDecimal additionalFeeHabitable;

    @NotNull(message = "Fixed Fee Non-Habitable cannot be null")
    @DecimalMin(value = "0.0", message = "Fixed Fee must be greater than or equal to zero")
    private BigDecimal fixedFeeNonHabitable;

    @NotNull(message = "Additional Fee Non-Habitable cannot be null")
    @DecimalMin(value = "0.0", message = "Additional Fee must be greater than or equal to zero")
    private BigDecimal additionalFeeNonHabitable;

    public BigDecimal getFixedFeeHabitable() {
        return fixedFeeHabitable;
    }

    public void setFixedFeeHabitable(BigDecimal fixedFeeHabitable) {
        this.fixedFeeHabitable = fixedFeeHabitable;
    }

    public BigDecimal getAdultFee() {
        return adultFee;
    }

    public void setAdultFee(BigDecimal adultFee) {
        this.adultFee = adultFee;
    }

    public BigDecimal getChildFee() {
        return childFee;
    }

    public void setChildFee(BigDecimal childFee) {
        this.childFee = childFee;
    }

    public BigDecimal getPetFee() {
        return petFee;
    }

    public void setPetFee(BigDecimal petFee) {
        this.petFee = petFee;
    }

    public BigDecimal getAdditionalFeeHabitable() {
        return additionalFeeHabitable;
    }

    public void setAdditionalFeeHabitable(BigDecimal additionalFeeHabitable) {
        this.additionalFeeHabitable = additionalFeeHabitable;
    }

    public BigDecimal getFixedFeeNonHabitable() {
        return fixedFeeNonHabitable;
    }

    public void setFixedFeeNonHabitable(BigDecimal fixedFeeNonHabitable) {
        this.fixedFeeNonHabitable = fixedFeeNonHabitable;
    }

    public BigDecimal getAdditionalFeeNonHabitable() {
        return additionalFeeNonHabitable;
    }

    public void setAdditionalFeeNonHabitable(BigDecimal additionalFeeNonHabitable) {
        this.additionalFeeNonHabitable = additionalFeeNonHabitable;
    }
}
