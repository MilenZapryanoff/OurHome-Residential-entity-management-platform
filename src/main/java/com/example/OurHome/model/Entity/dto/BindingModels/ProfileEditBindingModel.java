package com.example.OurHome.model.Entity.dto.BindingModels;

import jakarta.validation.constraints.Size;

public class ProfileEditBindingModel {

    @Size(min = 3, max = 20, message = "First name must be between 3 and 20 symbols")
    private String firstName;
    @Size(min = 3, max = 20, message = "First name must be between 3 and 20 symbols")
    private String lastName;
    @Size(min = 3, max = 20, message = "First name must be between 3 and 20 symbols")
    private String username;
    private String phoneNumber;
    private String newPassword;
    private String confirmPassword;


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
