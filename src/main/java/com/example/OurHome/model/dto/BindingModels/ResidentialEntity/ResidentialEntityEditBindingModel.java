package com.example.OurHome.model.dto.BindingModels.ResidentialEntity;

import com.example.OurHome.model.enums.CityName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class ResidentialEntityEditBindingModel {

    @NotNull
    private CityName city;

    private String accessCode;

    private String confirmAccessCode;

    @NotNull
    private String streetName;

    @NotNull
    private String streetNumber;

    private String entrance;

    private String facebookUrl;

    @Email
    private String email;

    public CityName getCity() {
        return city;
    }

    public void setCity(CityName city) {
        this.city = city;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public String getConfirmAccessCode() {
        return confirmAccessCode;
    }

    public void setConfirmAccessCode(String confirmAccessCode) {
        this.confirmAccessCode = confirmAccessCode;
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

    public void setEntrance(String entrance) {
        this.entrance = entrance;
    }

    public String getFacebookUrl() {
        return facebookUrl;
    }

    public void setFacebookUrl(String facebookUrl) {
        this.facebookUrl = facebookUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
