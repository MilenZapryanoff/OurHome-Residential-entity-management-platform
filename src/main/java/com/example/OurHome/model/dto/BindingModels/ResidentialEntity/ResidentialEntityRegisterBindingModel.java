package com.example.OurHome.model.dto.BindingModels.ResidentialEntity;

import com.example.OurHome.model.enums.CityName;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public class ResidentialEntityRegisterBindingModel {

    private CityName city;

    @NotNull
    @Size(min = 3, max = 20)
    private String accessCode;

    @NotNull
    @Size(min = 3, max = 20)
    private String confirmAccessCode;

    @NotNull
    private String streetName;

    @NotNull
    private String streetNumber;

    private String entrance;

    private String facebookUrl;

    @Email
    private String email;

    @NotNull
    @Positive(message = "Number of apartments must be between 0 and 500")
    @Max(value = 500L, message = "Number of apartments must be up to 500")
    private Long numberOfApartments;

    public CityName getCity() {
        return city;
    }

    public void setCity(CityName city) {
        this.city = city;
    }

    public String getStreetName() {
        return streetName;
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

    public Long getNumberOfApartments() {
        return numberOfApartments;
    }

    public void setNumberOfApartments(Long numberOfApartments) {
        this.numberOfApartments = numberOfApartments;
    }

    public String getFacebookUrl() {
        return facebookUrl;
    }

    public void setFacebookUrl(String facebookUrl) {
        this.facebookUrl = facebookUrl;
    }

    public @Email String getEmail() {
        return email;
    }

    public void setEmail(@Email String email) {
        this.email = email;
    }
}
