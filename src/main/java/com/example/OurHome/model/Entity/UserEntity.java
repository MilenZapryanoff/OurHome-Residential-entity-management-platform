package com.example.OurHome.model.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 3, max = 20)
    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    @Size(min = 3, max = 20)
    private String lastName;

    @Column(nullable = false)
    @Size(min = 3, max = 20)
    private String username;

    @Column(nullable = false)
    private LocalDateTime registrationDateTime;

    @Column
    private String phoneNumber;

    private String avatarPath;

    @Email
    @Column(unique = true, nullable = false)
    private String email;

    @NotNull
    @Column(nullable = false)
    private String password;

    @NotNull
    @ManyToOne
    private Role role;

    private boolean validated;

    private String validationCode;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    private List<ResidentialEntity> residentialEntities;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.DETACH)
    private List<ResidentialEntity> moderatedResidentialEntities;

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Property> properties;

    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<PropertyRegisterRequest> propertyRegisterRequests;

    @OneToMany(mappedBy = "receiver", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Message> receivedMessages;

    @OneToMany(mappedBy = "sender", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Message> sentMessages;

    public UserEntity() {
        properties = new ArrayList<>();
        receivedMessages = new ArrayList<>();
        sentMessages = new ArrayList<>();
        residentialEntities = new ArrayList<>();
        moderatedResidentialEntities = new ArrayList<>();
        propertyRegisterRequests = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String userName) {
        this.username = userName;
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

    public List<Message> getReceivedMessages() {
        return receivedMessages;
    }

    public void setReceivedMessages(List<Message> messages) {
        this.receivedMessages = messages;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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


    public List<ResidentialEntity> getModeratedResidentialEntities() {
        return moderatedResidentialEntities;
    }

    public void setModeratedResidentialEntities(List<ResidentialEntity> moderatedResidentialEntities) {
        this.moderatedResidentialEntities = moderatedResidentialEntities;
    }

    public String getValidationCode() {
        return validationCode;
    }

    public void setValidationCode(String validationCode) {
        this.validationCode = validationCode;
    }

    public List<Message> getSentMessages() {
        return sentMessages;
    }

    public void setSentMessages(List<Message> sentMessages) {
        this.sentMessages = sentMessages;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public List<PropertyRegisterRequest> getPropertyRequests() {
        return propertyRegisterRequests;
    }

    public void setPropertyRequests(List<PropertyRegisterRequest> propertyRegisterRequests) {
        this.propertyRegisterRequests = propertyRegisterRequests;
    }

    public LocalDateTime getRegistrationDateTime() {
        return registrationDateTime;
    }

    public void setRegistrationDateTime(LocalDateTime registrationDateTime) {
        this.registrationDateTime = registrationDateTime;
    }

    public List<PropertyRegisterRequest> getPropertyRegisterRequests() {
        return propertyRegisterRequests;
    }

    public void setPropertyRegisterRequests(List<PropertyRegisterRequest> propertyRegisterRequests) {
        this.propertyRegisterRequests = propertyRegisterRequests;
    }
}
