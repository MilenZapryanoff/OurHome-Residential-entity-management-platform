package com.example.OurHome.model.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime creationDateTime;

    private LocalDateTime resolveDateTime;

    private String category;

    private String subCategory;

    private String status;

    private String priority;

    private String source;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String image1;
    private String image2;
    private String image3;

    @ManyToOne
    private UserEntity creator;

    @ManyToOne
    private UserEntity processedBy;

    @ManyToOne
    private ResidentialEntity residentialEntity;

    private String contactInfo;

    private boolean publicReport;

    private boolean resolved;

    @Column(columnDefinition = "TEXT")
    private String resolution;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotNull LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(@NotNull LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public LocalDateTime getResolveDateTime() {
        return resolveDateTime;
    }

    public void setResolveDateTime(LocalDateTime resolveDateTime) {
        this.resolveDateTime = resolveDateTime;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public @NotNull String getCategory() {
        return category;
    }

    public void setCategory(@NotNull String category) {
        this.category = category;
    }

    public @NotNull String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(@NotNull String subCategory) {
        this.subCategory = subCategory;
    }

    public @NotNull String getStatus() {
        return status;
    }

    public void setStatus(@NotNull String status) {
        this.status = status;
    }

    public @NotNull String getPriority() {
        return priority;
    }

    public void setPriority(@NotNull String priority) {
        this.priority = priority;
    }

    public @NotNull String getSource() {
        return source;
    }

    public void setSource(@NotNull String source) {
        this.source = source;
    }

    public @NotNull String getDescription() {
        return description;
    }

    public void setDescription(@NotNull String description) {
        this.description = description;
    }

    public String getImage1() {
        return image1;
    }

    public String getImage2() {
        return image2;
    }

    public String getImage3() {
        return image3;
    }

    public UserEntity getCreator() {
        return creator;
    }

    public void setCreator(UserEntity creator) {
        this.creator = creator;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public boolean isPublicReport() {
        return publicReport;
    }

    public void setPublicReport(boolean publicReport) {
        this.publicReport = publicReport;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public UserEntity getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(UserEntity processedBy) {
        this.processedBy = processedBy;
    }

    public ResidentialEntity getResidentialEntity() {
        return residentialEntity;
    }

    public void setResidentialEntity(ResidentialEntity residentialEntity) {
        this.residentialEntity = residentialEntity;
    }
}
