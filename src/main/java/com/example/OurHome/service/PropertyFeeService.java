package com.example.OurHome.service;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.PropertyFee;

public interface PropertyFeeService {
    void createFirstFee(Property property);
    void createNewFee(Property property);
}
