package com.example.OurHome.model.Entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class Fee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal fixedFeeHabitable;
    private BigDecimal adultFee;
    private BigDecimal childFee;
    private BigDecimal petFee;
    private BigDecimal additionalFeeHabitable;
    private BigDecimal fixedFeeNonHabitable;
    private BigDecimal additionalFeeNonHabitable;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "fee")
    private ResidentialEntity residentialEntity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public ResidentialEntity getResidentialEntity() {
        return residentialEntity;
    }

    public void setResidentialEntity(ResidentialEntity residentialEntity) {
        this.residentialEntity = residentialEntity;
    }
}
