package com.example.OurHome.model.dto.BindingModels.Reports;

import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class ReportAddBindingModel {


    @NotNull
    private String category;

    @NotNull
    private String subCategory;

//    @NotNull
    private String status;

    @NotNull
    private String priority;

    @NotNull
    private String source;

    @NotNull
    private String description;

    private MultipartFile image1;
    private MultipartFile image2;
    private MultipartFile image3;

    @ManyToOne
    private UserEntity creator;

    @ManyToOne
    private UserEntity processedBy;

    @ManyToOne
    private ResidentialEntity residentialEntity;

    private String contactInfo;

    private boolean publicReport;

    private boolean resolved;

    @NotNull
    private String resolution;

    public @NotNull @Size(min = 3, max = 20, message = "Description must be between 3 and 20 symbols") String getCategory() {
        return category;
    }

    public void setCategory(@NotNull @Size(min = 3, max = 20, message = "Description must be between 3 and 20 symbols") String category) {
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

    public MultipartFile getImage1() {
        return image1;
    }

    public void setImage1(MultipartFile image1) {
        this.image1 = image1;
    }

    public MultipartFile getImage2() {
        return image2;
    }

    public void setImage2(MultipartFile image2) {
        this.image2 = image2;
    }

    public MultipartFile getImage3() {
        return image3;
    }

    public void setImage3(MultipartFile image3) {
        this.image3 = image3;
    }

    public UserEntity getCreator() {
        return creator;
    }

    public void setCreator(UserEntity creator) {
        this.creator = creator;
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

    public @NotNull String getResolution() {
        return resolution;
    }

    public void setResolution(@NotNull String resolution) {
        this.resolution = resolution;
    }
}
