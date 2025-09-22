package com.example.OurHome.model.dto.BindingModels.Property;

import com.example.OurHome.model.Entity.PropertyClass;
import com.example.OurHome.model.dto.BindingModels.AddressBook.AdultBindingModel;
import com.example.OurHome.model.dto.BindingModels.AddressBook.ChildBindingModel;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.ArrayList;
import java.util.List;

public class PropertyRegisterBindingModel {

    @NotNull
    @Positive(message = "Property № must be a positive digit")
    private int number;

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

    private PropertyClass propertyClass;

    private String ownerFullName;
    private String ownerPhone;
    private String ownerEmail;

    private List<AdultBindingModel> adults;
    private List<ChildBindingModel> children;

    public PropertyRegisterBindingModel() {
        adults = new ArrayList<>();
        children = new ArrayList<>();
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
