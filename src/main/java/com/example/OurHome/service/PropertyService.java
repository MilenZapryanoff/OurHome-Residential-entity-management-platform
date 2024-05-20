package com.example.OurHome.service;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.PropertyType;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyCreateBindingModel;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyEditBindingModel;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyRegisterBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.OverpaymentBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.PropertyFeeEditBindingModel;

import java.math.BigDecimal;
import java.util.List;

public interface PropertyService {

    boolean requestToObtainProperty(PropertyRegisterBindingModel propertyRegisterBindingModel, UserEntity loggedUser);

    void unlinkOwner(Long id, boolean deletedByManaged);

    void approvePropertyWithDataChange(Long id, boolean noValidationNeed);

    void approvePropertyWithoutDataChange(Long id);

    void rejectProperty(Long id);

    boolean editProperty(Long id, PropertyEditBindingModel propertyEditBindingModel, PropertyType propertyType);

    boolean processChangeRequest(Long id, PropertyEditBindingModel propertyEditBindingModel, PropertyType propertyType, UserEntity loggedUser);

    void setOverpayment(PropertyFeeEditBindingModel propertyFeeEditBindingModel);

    Property findPropertyById(Long id);

    PropertyEditBindingModel mapPropertyToEditBindingModel(Property property);

    boolean validationIsRequired(Long id, PropertyEditBindingModel propertyEditBindingModel);

    boolean validationIsRequired(Long id, PropertyRegisterBindingModel propertyRegisterBindingModel );

    List<Property> findAllProperties();

    void changeAutoFeeGeneration(Property property);

    OverpaymentBindingModel mapOverPaymentBindingModel(Property property);

    void updateOverpayment(Property property, BigDecimal overPayment);

    void setAdditionalPropertyFee(Property property, BigDecimal additionalPropertyFee);

    Property findPropertyByNumberAndResidentialEntity(int propertyNumber, Long residentialEntityId);

    void setPropertyType(Property property, PropertyType propertyType);

    List<Property> findAllPropertiesByPropertyType(PropertyType propertyType);

    void createAllProperties(ResidentialEntity newResidentialEntity, Long numberOfApartments);

    void deleteAllProperties(List<Property> residentialEntityProperties);

    boolean checkPropertiesForOwnersSet(Long residentialEntityId);

    void unlinkAllPropertiesFromOwner(Long residentId, ResidentialEntity residentialEntity);

    void deleteProperty(Long id, boolean deletedByManager);

    PropertyEditBindingModel mapRegistrationRequestToEditBindingModel(Property property);

    void createSingleProperty(PropertyCreateBindingModel propertyCreateBindingModel, ResidentialEntity residentialEntity, PropertyType propertyType);

    void updateNonFinancialPropertyFields(Property property, PropertyEditBindingModel propertyEditBindingModel, PropertyType propertyType);

    PropertyEditBindingModel mapChangeRequestToEditBindingModel(Property property);

    void turnAllPropertiesFeesOn(ResidentialEntity residentialEntity);

    void turnAllPropertiesFeesOff(ResidentialEntity residentialEntity);
}
