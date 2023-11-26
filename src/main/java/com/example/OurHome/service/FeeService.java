package com.example.OurHome.service;

import com.example.OurHome.model.Entity.Fee;
import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.ResidentialEntity;

import java.math.BigDecimal;

public interface FeeService {
    Fee createFee(ResidentialEntity newResidentialEntity);

    BigDecimal calculateMonthlyFee(ResidentialEntity residentialEntity, Property property);
}
