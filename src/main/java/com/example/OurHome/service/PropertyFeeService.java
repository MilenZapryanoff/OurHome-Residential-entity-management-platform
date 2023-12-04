package com.example.OurHome.service;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.PropertyFee;
import com.example.OurHome.model.Entity.dto.BindingModels.PropertyFee.PropertyFeeAddBindingModel;
import com.example.OurHome.model.Entity.dto.BindingModels.PropertyFee.PropertyFeeEditBindingModel;

import java.math.BigDecimal;

public interface PropertyFeeService {
    BigDecimal checkTotalDueAmount(Long id);
    PropertyFee findPropertyFeeById(Long propertyFeeId);
    PropertyFeeEditBindingModel mapPropertyFeeToBindingModel(Long id);
    void createFirstFee(Property property);
    void createMonthlyFee(Property property);
    void modifyFee(Long propertyFeeId, PropertyFeeEditBindingModel propertyFeeEditBindingModel);
    void deleteFee(PropertyFee propertyFee);
    void addFee(Property property, PropertyFeeAddBindingModel propertyFeeAddBindingModel);
    void changePaymentStatus(PropertyFee propertyFee);
}
