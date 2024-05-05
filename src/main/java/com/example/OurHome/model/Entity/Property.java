package com.example.OurHome.model.Entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int number;

    @Column
    private String floor;

    @Column
    private int numberOfAdults;

    @Column
    private int numberOfChildren;

    @Column
    private int numberOfPets;

    @Column
    private String numberOfRooms;

    @Column
    private boolean parkingAvailable;

    @Column
    private boolean notHabitable;

    @Column
    private boolean isValidated;

    @Column
    private boolean isObtained;

    @Column
    private boolean isRejected;

    @Column
    private boolean autoFee;

    @Column
    private BigDecimal overpayment;

    @Column
    private BigDecimal monthlyFeeFundMm;

    @Column
    private BigDecimal monthlyFeeFundRepair;

    @Column
    private BigDecimal additionalPropertyFee;

    @Column
    private BigDecimal totalMonthlyFee;

    @ManyToOne
    private UserEntity owner;

    @ManyToOne
    private ResidentialEntity residentialEntity;

    @OneToOne
    private PropertyRequest propertyRequest;

    @ManyToOne
    private PropertyType propertyType;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "property",cascade = CascadeType.REMOVE)
    private List<PropertyFee> propertyFees;

    public Property() {
        propertyFees = new ArrayList<>();
        this.overpayment = BigDecimal.valueOf(0);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public int getNumberOfAdults() {
        return numberOfAdults;
    }

    public void setNumberOfAdults(int numberOfAdults) {
        this.numberOfAdults = numberOfAdults;
    }

    public int getNumberOfChildren() {
        return numberOfChildren;
    }

    public void setNumberOfChildren(int numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
    }

    public int getNumberOfPets() {
        return numberOfPets;
    }

    public void setNumberOfPets(int numberOfPets) {
        this.numberOfPets = numberOfPets;
    }

    public boolean isNotHabitable() {
        return notHabitable;
    }

    public void setNotHabitable(boolean notHabitable) {
        this.notHabitable = notHabitable;
    }

    public boolean isValidated() {
        return isValidated;
    }

    public void setValidated(boolean validated) {
        isValidated = validated;
    }

    public boolean isRejected() {
        return isRejected;
    }

    public void setRejected(boolean rejected) {
        isRejected = rejected;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }

    public ResidentialEntity getResidentialEntity() {
        return residentialEntity;
    }

    public void setResidentialEntity(ResidentialEntity residentialEntity) {
        this.residentialEntity = residentialEntity;
    }

    public String getNumberOfRooms() {
        return numberOfRooms;
    }

    public void setNumberOfRooms(String numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }

    public boolean isParkingAvailable() {
        return parkingAvailable;
    }

    public void setParkingAvailable(boolean parkingAvailable) {
        this.parkingAvailable = parkingAvailable;
    }

    public BigDecimal getMonthlyFeeFundMm() {
        return monthlyFeeFundMm;
    }

    public void setMonthlyFeeFundMm(BigDecimal monthlyFee) {
        this.monthlyFeeFundMm = monthlyFee;
    }

    public BigDecimal getOverpayment() {
        return overpayment;
    }

    public void setOverpayment(BigDecimal overpayment) {
        this.overpayment = overpayment;
    }

    public List<PropertyFee> getPropertyFees() {
        return propertyFees;
    }

    public void setPropertyFees(List<PropertyFee> propertyFees) {
        this.propertyFees = propertyFees;
    }

    public boolean isAutoFee() {
        return autoFee;
    }

    public void setAutoFee(boolean autoFee) {
        this.autoFee = autoFee;
    }

    public BigDecimal getAdditionalPropertyFee() {
        return additionalPropertyFee;
    }

    public void setAdditionalPropertyFee(BigDecimal additionalPropertyFee) {
        this.additionalPropertyFee = additionalPropertyFee;
    }

    public BigDecimal getTotalMonthlyFee() {
        return totalMonthlyFee;
    }

    public void setTotalMonthlyFee(BigDecimal totalMonthlyFee) {
        this.totalMonthlyFee = totalMonthlyFee;
    }

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(PropertyType propertyType) {
        this.propertyType = propertyType;
    }

    public BigDecimal getMonthlyFeeFundRepair() {
        return monthlyFeeFundRepair;
    }

    public void setMonthlyFeeFundRepair(BigDecimal monthlyFeeFundRepair) {
        this.monthlyFeeFundRepair = monthlyFeeFundRepair;
    }

    public boolean isObtained() {
        return isObtained;
    }

    public void setObtained(boolean obtained) {
        isObtained = obtained;
    }

    public PropertyRequest getPropertyRequest() {
        return propertyRequest;
    }

    public void setPropertyRequest(PropertyRequest propertyRequest) {
        this.propertyRequest = propertyRequest;
    }
}
