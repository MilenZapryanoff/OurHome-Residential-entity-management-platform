package com.example.OurHome.service;

import com.example.OurHome.model.Entity.Fee;
import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.PropertyFee;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.dto.BindingModels.Fee.FeeEditBindingModel;

import java.math.BigDecimal;

public interface FeeService {
    Fee createFee(ResidentialEntity newResidentialEntity);

    BigDecimal calculateMonthlyFee(ResidentialEntity residentialEntity, Property property);

    FeeEditBindingModel mapFeeToBindingModel(Fee fee);

    Fee findById(Long id);

    void changeFee(ResidentialEntity residentialEntity, FeeEditBindingModel feeEditBindingModel);
}
