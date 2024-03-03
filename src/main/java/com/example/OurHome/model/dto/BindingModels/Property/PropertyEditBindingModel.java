package com.example.OurHome.model.dto.BindingModels.Property;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PropertyEditBindingModel {

    @NotNull
    private String number;
    @NotNull
    private String floor;
    @NotNull
    private int numberOfAdults;
    @NotNull
    private int numberOfChildren;
    @NotNull
    private int numberOfPets;

    @Positive(message = "Total flat space must be positive digit")
    private String totalFlatSpace;

    @Column
    private String numberOfRooms;

    private boolean parkingAvailable;

    private boolean notHabitable;

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
