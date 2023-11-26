package com.example.OurHome.model.Entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity(name = "properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String number;

    @Column(nullable = false)
    private String floor;

    @Column
    private String numberOfAdults;

    @Column
    private String numberOfChildren;

    @Column
    private String numberOfPets;

    @Column
    private String totalFlatSpace;

    @Column
    private String numberOfRooms;

    @Column
    private boolean parkingAvailable;

    @Column
    private boolean notHabitable;

    @Column
    private boolean isValidated;

    @Column
    private boolean isRejected;

    @Column
    private BigDecimal monthlyFee;

    @Column
    private BigDecimal feesDue;

    @Column
    private BigDecimal overpayment;

    @ManyToOne
    private UserEntity owner;

    @ManyToOne
    private ResidentialEntity residentialEntity;

    public Property() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getNumberOfAdults() {
        return numberOfAdults;
    }

    public void setNumberOfAdults(String numberOfAdults) {
        this.numberOfAdults = numberOfAdults;
    }

    public String getNumberOfChildren() {
        return numberOfChildren;
    }

    public void setNumberOfChildren(String numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
    }

    public String getNumberOfPets() {
        return numberOfPets;
    }

    public void setNumberOfPets(String numberOfPets) {
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

    public String getTotalFlatSpace() {
        return totalFlatSpace;
    }

    public void setTotalFlatSpace(String totalFlatSpace) {
        this.totalFlatSpace = totalFlatSpace;
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

    public BigDecimal getMonthlyFee() {
        return monthlyFee;
    }

    public void setMonthlyFee(BigDecimal monthlyFee) {
        this.monthlyFee = monthlyFee;
    }

    public BigDecimal getFeesDue() {
        return feesDue;
    }

    public void setFeesDue(BigDecimal feesDue) {
        this.feesDue = feesDue;
    }

    public BigDecimal getOverpayment() {
        return overpayment;
    }

    public void setOverpayment(BigDecimal overpayment) {
        this.overpayment = overpayment;
    }
}
