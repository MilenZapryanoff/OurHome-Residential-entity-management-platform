package com.example.OurHome.model.dto.ViewModels;

import com.example.OurHome.model.Entity.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserViewModel {

    private Long id;
    private String firstName;
    private String lastName;
    private Long pendingPropertyApproval;
    private String email;
    private String phoneNumber;
    private LocalDateTime registrationDateTime;
    private List<ResidentialEntity> residentialEntities;
    private List<Property> properties;
    private List<Message> messages;
    private Role role;
    private boolean validated;
    private Language language;
    boolean messageEmail;
    boolean newFeeEmail;
    boolean eventEmail;
    boolean reportEmail;

    private String avatarPath;

    public UserViewModel() {
        messages = new ArrayList<>();
        properties = new ArrayList<>();
        residentialEntities = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getPendingPropertyApproval() {
        return pendingPropertyApproval;
    }

    public void setPendingPropertyApproval(Long pendingPropertyApproval) {
        this.pendingPropertyApproval = pendingPropertyApproval;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<ResidentialEntity> getResidentialEntities() {
        return residentialEntities;
    }

    public void setResidentialEntities(List<ResidentialEntity> residentialEntities) {
        this.residentialEntities = residentialEntities;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public LocalDateTime getRegistrationDateTime() {
        return registrationDateTime;
    }

    public void setRegistrationDateTime(LocalDateTime registrationDateTime) {
        this.registrationDateTime = registrationDateTime;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public boolean isMessageEmail() {
        return messageEmail;
    }

    public void setMessageEmail(boolean messageEmail) {
        this.messageEmail = messageEmail;
    }

    public boolean isNewFeeEmail() {
        return newFeeEmail;
    }

    public void setNewFeeEmail(boolean newFeeEmail) {
        this.newFeeEmail = newFeeEmail;
    }

    public boolean isEventEmail() {
        return eventEmail;
    }

    public void setEventEmail(boolean eventEmail) {
        this.eventEmail = eventEmail;
    }

    public boolean isReportEmail() {
        return reportEmail;
    }

    public void setReportEmail(boolean reportEmail) {
        this.reportEmail = reportEmail;
    }
}
