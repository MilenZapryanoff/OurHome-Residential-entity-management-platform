package com.example.OurHome.model.dto.BindingModels.Property;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class PropertyRegisterBindingModel {

    @NotNull
    @Positive(message = "Property № must be a positive digit")
    private String number;

    @NotNull
    @Positive(message = "Floor № must be a positive digit")
    private String floor;

    @PositiveOrZero(message = "Number of adults must be a positive digit or 0")
    private String numberOfAdults;

    @PositiveOrZero(message = "Number of children must be a positive digit or 0")
    private String numberOfChildren;

    @PositiveOrZero(message = "Number of pets must be a positive digit or 0")
    private String numberOfPets;

    @Positive(message = "Total flat space must be positive digit")
    private String totalFlatSpace;

    @Column
    private String numberOfRooms;

    private boolean parkingAvailable;

    private boolean notHabitable;

    @NotNull
    private Long residentialEntity;


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

    public Long getResidentialEntity() {
        return residentialEntity;
    }

    public void setResidentialEntity(Long residentialEntity) {
        this.residentialEntity = residentialEntity;
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
}
