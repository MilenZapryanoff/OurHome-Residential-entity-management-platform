package com.example.OurHome.service;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyEditBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.PropertyFeeEditBindingModel;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyRegisterBindingModel;

import java.math.BigDecimal;
import java.util.List;

public interface PropertyService {

    boolean newProperty(PropertyRegisterBindingModel propertyRegisterBindingModel, UserEntity loggedUser);

    void deleteProperty(Long id, boolean deletedByManaged);

    void deletePropertiesWhenResidentRemoved(Long residentId, Long residentialEntityId);

    void approveProperty(Long id);

    void rejectProperty(Long id);

    boolean editProperty(Long id, PropertyEditBindingModel propertyEditBindingModel, boolean moderatorChange);

    void setOverpayment(PropertyFeeEditBindingModel propertyFeeEditBindingModel);

    void setMonthlyFee(BigDecimal monthlyFee, Property property);

    Property findPropertyById(Long id);

    PropertyEditBindingModel mapPropertyToEditBindingModel(Property property);

    boolean needOfVerification(Long id, PropertyEditBindingModel propertyEditBindingModel);

    List<Property> findAllProperties();

    void changeAutoFeeGeneration(Property property);

    void updateTotalMonthlyFee(Property property, BigDecimal additionalMonthlyFee);
}
