package com.example.OurHome.model.dto.BindingModels.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ManagerRegisterBindingModel {
    @Size(min = 2, max = 20, message = "First name must be between 2 and 20 characters!")
    private String firstName;
    @Size(min = 2, max = 20, message = "Last name must be between 2 and 20 characters!")
    private String lastName;


    @Email
    @NotEmpty(message = "Email cannot be empty!")
    private String email;

    @Positive(message = "Phone number must be only digits")
    private String phoneNumber;

    @Size(min = 3, max = 20, message = "Password length must be between 3 and 20 characters!")
    private String password;

    @NotEmpty(message = "Field cannot be empty!")
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
