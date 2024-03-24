package com.example.OurHome.service;
import com.example.OurHome.model.Entity.PropertyType;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.dto.BindingModels.PropertyType.PropertyTypeAddBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyType.PropertyTypeEditBindingModel;


public interface PropertyTypeService {

    void deletePropertyType(Long id);

    ResidentialEntity findResidentialEntityByPropertyType(Long id);

    PropertyTypeEditBindingModel mapPropertyTypeToEditBindingModel(Long id);

    boolean editPropertyType(Long id, PropertyTypeEditBindingModel propertyTypeEditBindingModel);

    boolean addPropertyType(Long id, PropertyTypeAddBindingModel propertyTypeAddBindingModel);

    PropertyType findById(Long propertyTypeId);
}
