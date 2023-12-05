package com.example.OurHome.model.dto.BindingModels.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class ForgotPasswordBindingModel {
    @Email (message = "Please enter a valid email")
    @NotEmpty (message = "Mandatory field")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
