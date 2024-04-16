package com.example.OurHome.service;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.PropertyFee;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.ResidentialEntity.ResidentialEntityEditBindingModel;
import com.example.OurHome.model.dto.BindingModels.ResidentialEntity.ResidentialEntityRegisterBindingModel;

import java.util.List;
import java.util.Optional;

public interface ResidentialEntityService {
    boolean newResidentialEntity(ResidentialEntityRegisterBindingModel residentialEntityRegisterBindingModel, UserEntity loggedUser);

    void removeResidentialEntity(Long id);

    boolean checkIfResidentialEntityDeletable(Long id);

    boolean accessCodesMatchCheck(String accessCode, String confirmAccessCode);

    Optional<ResidentialEntity> findResidentialEntityById(Long id);


    void editResidentialEntity(Long entityId, ResidentialEntityEditBindingModel residentialEntityEditBindingModel);

    List<ResidentialEntity> findResidentialEntitiesByManagerId(Long id);

    ResidentialEntityEditBindingModel mapEntityToEditBindingModel(ResidentialEntity residentialEntity);

    ResidentialEntity findResidentialEntityByPropertyId(Long id);

    void addPaymentAmountToIncomes(PropertyFee propertyFee, Property property);

    void reversePaymentAmountFromIncomes(PropertyFee propertyFee, Property property);
}
