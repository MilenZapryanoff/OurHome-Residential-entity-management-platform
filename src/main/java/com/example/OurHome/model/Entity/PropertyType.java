package com.example.OurHome.model.Entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "property_types")
public class PropertyType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String description;
    @Column
    private BigDecimal totalFlatSpace;
    @Column
    private BigDecimal commonPartsPercentage;
    @Column
    private BigDecimal fundRepairHabitable;
    @Column
    private BigDecimal fundRepairNotHabitable;
    @ManyToOne
    private ResidentialEntity residentialEntity;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "propertyType")
    private List<Property> properties;

    public PropertyType() {
        properties = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setFundRepairHabitable(BigDecimal fundRepairAmount) {
        this.fundRepairHabitable = fundRepairAmount;
    }

    public BigDecimal getFundRepairNotHabitable() {
        return fundRepairNotHabitable;
    }

    public void setFundRepairNotHabitable(BigDecimal fundRepairNotHabitable) {
        this.fundRepairNotHabitable = fundRepairNotHabitable;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ResidentialEntity getResidentialEntity() {
        return residentialEntity;
    }

    public void setResidentialEntity(ResidentialEntity residentialEntity) {
        this.residentialEntity = residentialEntity;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> property) {
        this.properties = property;
    }

}
