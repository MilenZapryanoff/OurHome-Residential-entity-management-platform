package com.example.OurHome.service;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.PropertyFee;
import com.example.OurHome.model.Entity.dto.BindingModels.PropertyFee.PropertyFeeEditBindingModel;

public interface PropertyFeeService {
    void createFirstFee(Property property);
    void createNewFee(Property property);
    PropertyFee findPropertyFeeById(Long propertyFeeId);
    PropertyFeeEditBindingModel mapPropertyFeeToBindingModel(Long id);
    void modifyFee(Long propertyFeeId, PropertyFeeEditBindingModel propertyFeeEditBindingModel);
    void deleteFee(PropertyFee propertyFee);
}
