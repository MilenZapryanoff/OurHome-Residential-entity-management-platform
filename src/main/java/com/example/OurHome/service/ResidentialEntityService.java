package com.example.OurHome.service;

import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.Entity.dto.BindingModels.ResidentialEntityEditBindingModel;
import com.example.OurHome.model.Entity.dto.BindingModels.ResidentialEntityRegisterBindingModel;

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
}
