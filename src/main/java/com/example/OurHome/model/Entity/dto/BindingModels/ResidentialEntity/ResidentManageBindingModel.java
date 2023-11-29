package com.example.OurHome.model.Entity.dto.BindingModels.ResidentialEntity;

import jakarta.validation.constraints.Size;

public class ResidentManageBindingModel {

    private Long userId;
    private Long entityId;
    private String message;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
