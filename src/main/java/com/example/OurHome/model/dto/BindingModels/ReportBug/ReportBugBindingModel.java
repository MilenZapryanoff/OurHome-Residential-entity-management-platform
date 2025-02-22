package com.example.OurHome.model.dto.BindingModels.ReportBug;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReportBugBindingModel {


    private String name;

    @Email(message = "Enter a valid email address")
    private String email;

    @NotNull (message = "Enter Subject")
    private String subject;

    @NotNull(message = "Enter message")
    private String message;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
