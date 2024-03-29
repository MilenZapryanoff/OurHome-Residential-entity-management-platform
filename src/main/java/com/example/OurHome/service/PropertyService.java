package com.example.OurHome.service;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.PropertyType;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyEditBindingModel;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyRegisterBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.OverpaymentBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.PropertyFeeEditBindingModel;

import java.math.BigDecimal;
import java.util.List;

public interface PropertyService {

    boolean newProperty(PropertyRegisterBindingModel propertyRegisterBindingModel, UserEntity loggedUser);

    void deleteProperty(Long id, boolean deletedByManaged);

    void deletePropertiesWhenResidentRemoved(Long residentId, Long residentialEntityId);

    void approveProperty(Long id);

    void rejectProperty(Long id);

    boolean editProperty(Long id, PropertyEditBindingModel propertyEditBindingModel, boolean noValidationNeed, PropertyType propertyType);

    void setOverpayment(PropertyFeeEditBindingModel propertyFeeEditBindingModel);

    Property findPropertyById(Long id);

    PropertyEditBindingModel mapPropertyToEditBindingModel(Property property);

    boolean checkNeedOfVerification(Long id, PropertyEditBindingModel propertyEditBindingModel);

    List<Property> findAllProperties();

    void changeAutoFeeGeneration(Property property);

    OverpaymentBindingModel mapOverPaymentBindingModel(Property property);

    void updateOverpayment(Property property, BigDecimal overPayment);

    void setAdditionalPropertyFee(Property property, BigDecimal additionalPropertyFee);

    Property findPropertyByNumberAndResidentialEntity(String propertyNumber, Long residentialEntityId);

    void setPropertyType(Property property, PropertyType propertyType);

    List<Property> findAllPropertiesByPropertyType(PropertyType propertyType);

}
