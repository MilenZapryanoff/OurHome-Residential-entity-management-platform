package com.example.OurHome.model.dto.BindingModels.User;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class ResetPasswordBindingModel {

    @NotEmpty(message = "Field cannot be empty")
    private String resetCode;
    @NotEmpty(message = "Field cannot be empty")
    @Size(min = 3, max = 20, message = "Password length must be between 3 and 20 characters!")
    private String newPassword;
    @NotEmpty(message = "Field cannot be empty")
    @Size(min = 3, max = 20, message = "Password length must be between 3 and 20 characters!")
    private String confirmPassword;

    public String getResetCode() {
        return resetCode;
    }

    public void setResetCode(String resetCode) {
        this.resetCode = resetCode;
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
