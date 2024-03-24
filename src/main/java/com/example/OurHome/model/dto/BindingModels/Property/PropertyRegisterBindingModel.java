package com.example.OurHome.model.dto.BindingModels.Property;

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
    private int numberOfAdults;

    @PositiveOrZero(message = "Number of children must be a positive digit or 0")
    private int numberOfChildren;

    @PositiveOrZero(message = "Number of pets must be a positive digit or 0")
    private int numberOfPets;

    @PositiveOrZero(message = "Number of rooms must be a positive digit")
    private String numberOfRooms;

    private boolean parkingAvailable;

    private boolean notHabitable;

    @NotNull
    private Long residentialEntity;

    private Long propertyType;


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

    public Long getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(Long propertyType) {
        this.propertyType = propertyType;
    }
}
