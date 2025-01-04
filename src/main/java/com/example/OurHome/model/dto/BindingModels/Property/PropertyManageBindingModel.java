package com.example.OurHome.model.dto.BindingModels.Property;

import com.example.OurHome.model.Entity.PropertyClass;

public class PropertyManageBindingModel {

    private Long propertyId;

    private Long entityId;

    private String action;

    public PropertyManageBindingModel() {
    }

    public Long getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Long propertyId) {
        this.propertyId = propertyId;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    private PropertyClass propertyClass;


    public PropertyClass getPropertyClass() {
        return propertyClass;
    }

    public void setPropertyClass(PropertyClass propertyClass) {
        this.propertyClass = propertyClass;
    }
}
