package com.example.OurHome.service;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.Entity.dto.BindingModels.PropertyEditBindingModel;
import com.example.OurHome.model.Entity.dto.BindingModels.PropertyRegisterBindingModel;

import java.util.List;

public interface PropertyService {
    void newProperty(PropertyRegisterBindingModel propertyRegisterBindingModel, UserEntity loggedUser);

    void deleteProperty(Long id, boolean deletedByManaged);

    void deletePropertiesWhenResidentRemoved(Long residentId, Long residentialEntityId);

    void approveProperty(Long id);

    void rejectProperty(Long id);

    Property findPropertyById(Long id);

    PropertyEditBindingModel mapPropertyToEditBindingModel(Property property);

    void editProperty(Long id, PropertyEditBindingModel propertyEditBindingModel, boolean moderatorChange);

    boolean needOfVerification(Long id, PropertyEditBindingModel propertyEditBindingModel);
}
