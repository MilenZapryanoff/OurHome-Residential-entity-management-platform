package com.example.OurHome.model.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    @ManyToOne(optional = false)
    @JoinColumn(referencedColumnName = "id")
    private UserEntity manager;

    @OneToOne(cascade = CascadeType.ALL)
    private Fee fee;

    @Column(nullable = false)
    private BigDecimal incomesFundMm;

    @Column(nullable = false)
    private BigDecimal incomesFundRepair;

    @Column(nullable = false)
    private BigDecimal incomesTotalAmount;

    @Column
    private boolean incomesVisible;

    @Column
    private boolean expensesVisible;

    @Column
    private String bankIban;

    @Column
    private String bankAccountHolder;

    @Column
    private String bankName;

    @Column
    private String bankAdditionalData;

    @Column
    private String facebookUrl;

    @Column
    @Email
    private String email;

    @Column
    private boolean bankDetailsSet;

    @Column
    private String picturePath;


    @OneToMany(mappedBy = "residentialEntity", fetch = FetchType.EAGER)
    private List<Property> properties;

    @OneToMany(mappedBy = "residentialEntity", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Report> reports;

    @OneToMany(mappedBy = "residentialEntity", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Event> events;

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
        reports = new ArrayList<>();
        events = new ArrayList<>();
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

    public BigDecimal getIncomesTotalAmount() {
        return incomesTotalAmount;
    }

    public void setIncomesTotalAmount(BigDecimal incomesAmount) {
        this.incomesTotalAmount = incomesAmount;
    }

    public List<PropertyType> getPropertyTypes() {
        return propertyTypes;
    }

    public void setPropertyTypes(List<PropertyType> propertyTypes) {
        this.propertyTypes = propertyTypes;
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

    public boolean isIncomesVisible() {
        return incomesVisible;
    }

    public void setIncomesVisible(boolean incomesVisible) {
        this.incomesVisible = incomesVisible;
    }

    public boolean isExpensesVisible() {
        return expensesVisible;
    }

    public void setExpensesVisible(boolean expensesVisible) {
        this.expensesVisible = expensesVisible;
    }

    public String getBankIban() {
        return bankIban;
    }

    public void setBankIban(String bankIban) {
        this.bankIban = bankIban;
    }

    public String getBankAccountHolder() {
        return bankAccountHolder;
    }

    public void setBankAccountHolder(String bankAccountNumber) {
        this.bankAccountHolder = bankAccountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAdditionalData() {
        return bankAdditionalData;
    }

    public void setBankAdditionalData(String bankAdditionalData) {
        this.bankAdditionalData = bankAdditionalData;
    }

    public boolean isBankDetailsSet() {
        return bankDetailsSet;
    }

    public void setBankDetailsSet(boolean bankDetailsSet) {
        this.bankDetailsSet = bankDetailsSet;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public List<Report> getReports() {
        return reports;
    }

    public void setReports(List<Report> reports) {
        this.reports = reports;
    }

    public String getFacebookUrl() {
        return facebookUrl;
    }

    public void setFacebookUrl(String facebookUrl) {
        this.facebookUrl = facebookUrl;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

