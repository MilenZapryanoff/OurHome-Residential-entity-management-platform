package com.example.OurHome.service;

import com.example.OurHome.model.Entity.Fee;
import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.PropertyType;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.dto.BindingModels.Fee.FeeEditBindingModel;

import java.math.BigDecimal;

public interface FeeService {
    Fee createFee(ResidentialEntity newResidentialEntity);

    BigDecimal calculateFundMm(ResidentialEntity residentialEntity, Property property);

    BigDecimal calculateFundRepair(ResidentialEntity residentialEntity, Property property);

    FeeEditBindingModel mapFeeToBindingModel(Fee fee);

    Fee findById(Long id);

    void changeFee(ResidentialEntity residentialEntity, FeeEditBindingModel feeEditBindingModel);

    BigDecimal calculateNewFundRepair(Property property, PropertyType newPropertyType);
}
