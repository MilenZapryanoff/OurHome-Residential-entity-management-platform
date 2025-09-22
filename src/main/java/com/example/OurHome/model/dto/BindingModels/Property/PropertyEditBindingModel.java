package com.example.OurHome.model.dto.BindingModels.Property;

import com.example.OurHome.model.Entity.PropertyClass;
import com.example.OurHome.model.dto.BindingModels.AddressBook.AdultBindingModel;
import com.example.OurHome.model.dto.BindingModels.AddressBook.ChildBindingModel;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.ArrayList;
import java.util.List;

public class PropertyEditBindingModel {

    @NotNull
    @Positive(message = "Property number must be a positive digit")
    private int number;

    @NotNull
    private String floor;

    @PositiveOrZero(message = "Number of adults must be a positive digit or 0")
    private int numberOfAdults;

    @PositiveOrZero(message = "Number of adults must be a positive digit or 0")
    private int numberOfChildren;

    @PositiveOrZero(message = "Number of adults must be a positive digit or 0")
    private int numberOfPets;

    @Column
    private String numberOfRooms;

    private boolean parkingAvailable;

    private boolean notHabitable;

    private Long propertyType;

    private PropertyClass propertyClass;

    private String ownerFullName;
    private String ownerPhone;
    private String ownerEmail;

    private List<AdultBindingModel> adults;
    private List<ChildBindingModel> children;

    public PropertyEditBindingModel() {
        adults = new ArrayList<>();
        children = new ArrayList<>();
    }

    @NotNull
    @Positive(message = "Property number must be a positive digit")
    public int getNumber() {
        return number;
    }

    public void setNumber(@NotNull @Positive(message = "Property number must be a positive digit") int number) {
        this.number = number;
    }

    public @NotNull String getFloor() {
        return floor;
    }

    public void setFloor(@NotNull String floor) {
        this.floor = floor;
    }

    @PositiveOrZero(message = "Number of adults must be a positive digit or 0")
    public int getNumberOfAdults() {
        return numberOfAdults;
    }

    public void setNumberOfAdults(@PositiveOrZero(message = "Number of adults must be a positive digit or 0") int numberOfAdults) {
        this.numberOfAdults = numberOfAdults;
    }

    @PositiveOrZero(message = "Number of adults must be a positive digit or 0")
    public int getNumberOfChildren() {
        return numberOfChildren;
    }

    public void setNumberOfChildren(@PositiveOrZero(message = "Number of adults must be a positive digit or 0") int numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
    }

    @PositiveOrZero(message = "Number of adults must be a positive digit or 0")
    public int getNumberOfPets() {
        return numberOfPets;
    }

    public void setNumberOfPets(@PositiveOrZero(message = "Number of adults must be a positive digit or 0") int numberOfPets) {
        this.numberOfPets = numberOfPets;
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

    public boolean isNotHabitable() {
        return notHabitable;
    }

    public void setNotHabitable(boolean notHabitable) {
        this.notHabitable = notHabitable;
    }

    public Long getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(Long propertyType) {
        this.propertyType = propertyType;
    }

    public PropertyClass getPropertyClass() {
        return propertyClass;
    }

    public void setPropertyClass(PropertyClass propertyClass) {
        this.propertyClass = propertyClass;
    }

    public String getOwnerFullName() {
        return ownerFullName;
    }

    public void setOwnerFullName(String ownerFullName) {
        this.ownerFullName = ownerFullName;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public List<AdultBindingModel> getAdults() {
        return adults;
    }

    public void setAdults(List<AdultBindingModel> adults) {
        this.adults = adults;
    }

    public List<ChildBindingModel> getChildren() {
        return children;
    }

    public void setChildren(List<ChildBindingModel> children) {
        this.children = children;
    }
}
