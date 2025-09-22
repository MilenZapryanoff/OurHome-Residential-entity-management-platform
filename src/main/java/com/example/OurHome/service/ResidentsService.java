package com.example.OurHome.service;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyEditBindingModel;

public interface ResidentsService {

    void updateAddressBook(Property property, String ownerFullName, String ownerPhone, String ownerEmail, PropertyEditBindingModel propertyEditBindingModel);

}
