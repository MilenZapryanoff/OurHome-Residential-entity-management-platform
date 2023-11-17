package com.example.OurHome.model.Entity.dto.BindingModels;

import com.example.OurHome.model.Entity.enums.CityName;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;

public class PropertyEditBindingModel {

    @NotNull
    private String number;
    @NotNull
    private String floor;
    @NotNull
    private String numberOfAdults;
    @NotNull
    private String numberOfChildren;
    @NotNull
    private String numberOfPets;

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
}
