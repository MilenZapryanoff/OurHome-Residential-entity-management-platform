package com.example.OurHome.model.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "residential_entities")
public class ResidentialEntity {

    @Id
    @NotNull
    private Long id;

    @NotNull
    @ManyToOne
    private City city;

    @Column(nullable = false)
    private String accessCode;

    @Column(name = "accesscode_hint")
    private String accessCodeHint;

    @Column(name = "street_name", nullable = false)
    private String streetName;

    @Column(name = "street_number", nullable = false)
    private String streetNumber;

    @Column
    private String entrance;

    @Column(nullable = false)
    private Long numberOfApartments;

    @ManyToOne(optional = false)
    @JoinColumn(referencedColumnName = "id")
    private UserEntity manager;

    @OneToOne(cascade = CascadeType.ALL)
    private Fee fee;

    @Column(nullable = false)
    private BigDecimal incomesAmount;

    @OneToMany(mappedBy = "residentialEntity", fetch = FetchType.EAGER)
    private List<Property> properties;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "residentialEntities")
    private List<UserEntity> residents;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "moderatedResidentialEntities")
    private List<UserEntity> moderators;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "residentialEntity", cascade = CascadeType.REMOVE)
    private List<Expense> expenses;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "residentialEntity", cascade = CascadeType.REMOVE)
    private List<PropertyType> propertyTypes;

    public ResidentialEntity() {
        residents = new ArrayList<>();
        properties = new ArrayList<>();
        moderators = new ArrayList<>();
        expenses = new ArrayList<>();
        propertyTypes = new ArrayList<>();
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public String getAccessCodeHint() {
        return accessCodeHint;
    }

    public void setAccessCodeHint(String accessCodeHint) {
        this.accessCodeHint = accessCodeHint;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getEntrance() {
        return entrance;
    }

    public void setEntrance(String vhod) {
        this.entrance = vhod;
    }

    public Long getNumberOfApartments() {
        return numberOfApartments;
    }

    public void setNumberOfApartments(Long numberOfApartments) {
        this.numberOfApartments = numberOfApartments;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public List<UserEntity> getResidents() {
        return residents;
    }

    public void setResidents(List<UserEntity> residents) {
        this.residents = residents;
    }

    public UserEntity getManager() {
        return manager;
    }

    public void setManager(UserEntity manager) {
        this.manager = manager;
    }

    public List<UserEntity> getModerators() {
        return moderators;
    }

    public void setModerators(List<UserEntity> moderators) {
        this.moderators = moderators;
    }

    public Fee getFee() {
        return fee;
    }

    public void setFee(Fee fee) {
        this.fee = fee;
    }

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expense) {
        this.expenses = expense;
    }

    public BigDecimal getIncomesAmount() {
        return incomesAmount;
    }

    public void setIncomesAmount(BigDecimal incomesAmount) {
        this.incomesAmount = incomesAmount;
    }

    public List<PropertyType> getPropertyTypes() {
        return propertyTypes;
    }

    public void setPropertyTypes(List<PropertyType> propertyTypes) {
        this.propertyTypes = propertyTypes;
    }
}
