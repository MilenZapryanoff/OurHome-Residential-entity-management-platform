package com.example.OurHome.service;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.PropertyFee;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.OverpaymentBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.PropertyFeeAddBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.PropertyFeeAddGlobalFeeBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.PropertyFeeEditBindingModel;
import com.example.OurHome.model.events.PropertyApprovalEvent;

import java.math.BigDecimal;

public interface PropertyFeeService {
    BigDecimal checkTotalDueAmount(Long id);
    PropertyFee findPropertyFeeById(Long propertyFeeId);
    PropertyFeeEditBindingModel mapPropertyFeeToBindingModel(Long id);
    void createFirstFee(PropertyApprovalEvent propertyApprovalEvent);
    void createMonthlyFee(Property property);
    void modifyFee(Long propertyFeeId, PropertyFeeEditBindingModel propertyFeeEditBindingModel);
    void deleteFee(PropertyFee propertyFee);
    void addFee(Property property, PropertyFeeAddBindingModel propertyFeeAddBindingModel);
    void changePaymentStatus(PropertyFee propertyFee);
    boolean addGlobalFee(ResidentialEntity residentialEntity, PropertyFeeAddGlobalFeeBindingModel propertyFeeAddGlobalFeeBindingModel);

}
