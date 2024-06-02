package com.example.OurHome.service;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.PropertyFee;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.Fee.BankDetailsBindingModel;
import com.example.OurHome.model.dto.BindingModels.ResidentialEntity.ResidentialEntityEditBindingModel;
import com.example.OurHome.model.dto.BindingModels.ResidentialEntity.ResidentialEntityRegisterBindingModel;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ResidentialEntityService {
    boolean newResidentialEntity(ResidentialEntityRegisterBindingModel residentialEntityRegisterBindingModel, UserEntity loggedUser);

    void deleteResidentialEntity(Long id);

    boolean checkIfUserIsResidentialEntityModerator(Long residentialEntityId, Long residentId);

    boolean accessCodesMatchCheck(String accessCode, String confirmAccessCode);

    Optional<ResidentialEntity> findResidentialEntityById(Long id);

    void editResidentialEntity(Long entityId, ResidentialEntityEditBindingModel residentialEntityEditBindingModel);

    List<ResidentialEntity> findResidentialEntitiesByManagerId(Long id);

    ResidentialEntityEditBindingModel mapEntityToEditBindingModel(ResidentialEntity residentialEntity);

    ResidentialEntity findResidentialEntityByPropertyId(Long id);

    void addPaymentAmountToIncomes(PropertyFee propertyFee, Property property);

    void reversePaymentAmountFromIncomes(PropertyFee propertyFee, Property property);

    void changeExpensesVisibility(Long id);

    void changeIncomesVisibility(Long id);

    boolean checkIfResidentialEntityDeletable(Long id);

    BankDetailsBindingModel mapEntityToBankDetailsBindingModel(ResidentialEntity residentialEntity);

    void updateResidentialEntityBankDetails(ResidentialEntity residentialEntity, BankDetailsBindingModel bankDetailsBindingModel);

    void deleteResidentialEntityBankDetails(ResidentialEntity residentialEntity);

    String savePicture(MultipartFile file, ResidentialEntity residentialEntity) throws IOException;

    void updatePicturePath(ResidentialEntity residentialEntity, String picturePath);

    void removeResidentialEntityPicture(ResidentialEntity residentialEntity) throws IOException;
}
