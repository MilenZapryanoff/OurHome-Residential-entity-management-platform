package com.example.OurHome.model.dto.BindingModels.PropertyType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class PropertyTypeAddBindingModel {
    @NotNull
    @Size(min = 3, max = 20, message = "Description must be between 3 and 20 symbols")
    private String description;

    @NotNull
    @DecimalMin(value = "0.0", message = "Total flat space must be value greater than or equal to zero")
    private BigDecimal totalFlatSpace;

    @DecimalMin(value = "0.0", message = "Common parts percentage flat space must be value greater than or equal to zero")
    private BigDecimal commonPartsPercentage;

    @DecimalMin(value = "0.0", message = "Fund Repair amount must be greater than or equal to zero")
    private BigDecimal fundRepairHabitable;
    @DecimalMin(value = "0.0", message = "Fund Repair amount must be greater than or equal to zero")
    private BigDecimal fundRepairNotHabitable;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getTotalFlatSpace() {
        return totalFlatSpace;
    }

    public void setTotalFlatSpace(BigDecimal totalFlatSpace) {
        this.totalFlatSpace = totalFlatSpace;
    }

    public BigDecimal getCommonPartsPercentage() {
        return commonPartsPercentage;
    }

    public void setCommonPartsPercentage(BigDecimal commonPartsPercentage) {
        this.commonPartsPercentage = commonPartsPercentage;
    }

    public BigDecimal getFundRepairHabitable() {
        return fundRepairHabitable;
    }

    public void setFundRepairHabitable(BigDecimal fundRepairHabitable) {
        this.fundRepairHabitable = fundRepairHabitable;
    }

    public BigDecimal getFundRepairNotHabitable() {
        return fundRepairNotHabitable;
    }

    public void setFundRepairNotHabitable(BigDecimal fundRepairNotHabitable) {
        this.fundRepairNotHabitable = fundRepairNotHabitable;
    }
}
