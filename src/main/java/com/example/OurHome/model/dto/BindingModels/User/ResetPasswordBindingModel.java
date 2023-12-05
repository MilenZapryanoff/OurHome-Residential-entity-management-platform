package com.example.OurHome.model.dto.BindingModels.User;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class ResetPasswordBindingModel {

    @NotEmpty(message = "Field cannot be empty")
    private String verificationCode;
    @NotEmpty(message = "Field cannot be empty")
    @Size(min = 3, max = 20, message = "Password length must be between 3 and 20 characters!")
    private String newPassword;
    @NotEmpty(message = "Field cannot be empty")
    @Size(min = 3, max = 20, message = "Password length must be between 3 and 20 characters!")
    private String confirmPassword;

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
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
