package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyEditBindingModel;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyRegisterBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.PropertyFeeEditBindingModel;
import com.example.OurHome.model.events.PropertyApprovalEvent;
import com.example.OurHome.repo.PropertyRepository;
import com.example.OurHome.service.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PropertyServiceImpl implements PropertyService {

    private final ModelMapper modelMapper;
    private final ResidentialEntityService residentialEntityService;
    private final PropertyRepository propertyRepository;
    private final MessageService messageService;
    private final FeeService feeService;
    private final ApplicationEventPublisher applicationEventPublisher;


    public PropertyServiceImpl(ModelMapper modelMapper, ResidentialEntityService residentialEntityService, PropertyRepository propertyRepository, MessageService messageService, FeeService feeService, ApplicationEventPublisher applicationEventPublisher) {
        this.modelMapper = modelMapper;
        this.residentialEntityService = residentialEntityService;
        this.propertyRepository = propertyRepository;
        this.messageService = messageService;
        this.feeService = feeService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * Find property by id
     *
     * @param id property id
     * @return Property
     */
    @Override
    public Property findPropertyById(Long id) {
        return propertyRepository.findById(id).orElse(null);
    }

    /**
     * Property creation. Performed by USER
     *
     * @param propertyRegisterBindingModel the binding model with the data from frontend
     * @param loggedUser                   logged user.
     */
    @Override
    @Transactional
    public void newProperty(PropertyRegisterBindingModel propertyRegisterBindingModel, UserEntity loggedUser) {

        Property newProperty = modelMapper.map(propertyRegisterBindingModel, Property.class);
        Long residentialEntityId = propertyRegisterBindingModel.getResidentialEntity();

        ResidentialEntity residentialEntity = residentialEntityService
                .findResidentialEntityById(residentialEntityId)
                .orElse(null);

        newProperty.setResidentialEntity(residentialEntity);
        newProperty.setOwner(loggedUser);
        newProperty.setValidated(false);

        propertyRepository.save(newProperty);

        //sending message to residential entity manager
        messageService.propertyRegistrationMessageToManager(residentialEntity);
    }

    /**
     * Single Property deletion
     * Performed by USER or MANAGER
     *
     * @param id property id
     */
    public void deleteProperty(Long id, boolean deletedByManaged) {
        Property property = propertyRepository.findById(id).orElse(null);
        if (property != null) {
            propertyRepository.delete(property);

            if (deletedByManaged) {
                messageService.propertyDeletedMessageToOwner(property);
            } else {
                messageService.propertyDeletedMessageToManager(property);
            }


        }
    }

    /**
     * Delete existing in RE owned properties when owner is removed from RE
     * Performed by MANAGER
     *
     * @param residentId          owner(resident) id
     * @param residentialEntityId residential entity id
     */
    @Override
    public void deletePropertiesWhenResidentRemoved(Long residentId, Long residentialEntityId) {
        List<Property> allUserProperties = propertyRepository.findAllUserProperties(residentId, residentialEntityId);
        propertyRepository.deleteAll(allUserProperties);
    }


    /**
     * Property approval.
     * Performed by Residential entity MANAGER
     *
     * @param id property id
     */
    @Override
    public void approveProperty(Long id) {
        Property property = propertyRepository.findById(id).orElse(null);
        if (property != null) {
            property.setValidated(true);
            propertyRepository.save(property);

            applicationEventPublisher.publishEvent(new PropertyApprovalEvent("PropertyService", property));

            messageService.propertyApprovedMessage(property);
        }
    }

    /**
     * Property reject.
     * Performed by Residential entity MANAGER
     *
     * @param id property id
     */
    @Override
    public void rejectProperty(Long id) {
        Property property = propertyRepository.findById(id).orElse(null);
        if (property != null) {
            property.setRejected(true);
            propertyRepository.save(property);

            messageService.propertyRejectedMessage(property);
        }
    }

    /**
     * Mapping of Property to PropertyEditBindingModel used for edit of property data.
     *
     * @param property Property entity
     * @return PropertyEditBindingModel
     */
    @Override
    public PropertyEditBindingModel mapPropertyToEditBindingModel(Property property) {

        PropertyEditBindingModel propertyEditBindingModel = new PropertyEditBindingModel();

        if (property != null) {
            propertyEditBindingModel = modelMapper.map(property, PropertyEditBindingModel.class);
        }

        return propertyEditBindingModel;
    }

    /**
     * Edit property method
     *
     * @param id                       property id
     * @param propertyEditBindingModel the binding model with the data returning from frontend
     * @param moderatorChange          TRUE if change is made by moderator, FALSE if change is made by owner
     */
    @Override
    public void editProperty(Long id, PropertyEditBindingModel propertyEditBindingModel, boolean moderatorChange) {

        Property property = propertyRepository.findById(id).orElse(null);

        if (property != null) {
            modelMapper.map(propertyEditBindingModel, property);

            ResidentialEntity residentialEntity = property.getResidentialEntity();
            property.setMonthlyFee(feeService.calculateMonthlyFee(residentialEntity, property));

            if (moderatorChange) {
                property.setValidated(true);
            } else {
                property.setValidated(false);
                //sending message (notification) to manager
                messageService.propertyModificationMessageToManager(property);
            }
            property.setRejected(false);
            propertyRepository.save(property);
        }
    }

    /**
     * Check if the modified property data needs manager/moderator verification.
     * If some of the fields in the if statement of the method are changed property is set to verified=false
     *
     * @param id                       property id
     * @param propertyEditBindingModel the binding model with the data returning from frontend
     * @return TRUE if changes need verification from manager/moderator. FALSE if no need of verification
     */
    @Override
    public boolean needOfVerification(Long id, PropertyEditBindingModel propertyEditBindingModel) {
        Property property = propertyRepository.findById(id).orElse(null);
        if (property != null) {

            return !property.getNumber().equals(propertyEditBindingModel.getNumber()) ||
                    !property.getFloor().equals(propertyEditBindingModel.getFloor()) ||
                    !property.getNumberOfAdults().equals(propertyEditBindingModel.getNumberOfAdults()) ||
                    !property.getNumberOfChildren().equals(propertyEditBindingModel.getNumberOfChildren()) ||
                    !property.getNumberOfPets().equals(propertyEditBindingModel.getNumberOfPets()) ||
                    property.isNotHabitable() != propertyEditBindingModel.isNotHabitable();
        }
        return false;
    }

    @Override
    public List<Property> findAllProperties() {
        return propertyRepository.findAll();
    }

    @Override
    public void setOverpayment(PropertyFeeEditBindingModel propertyFeeEditBindingModel) {
        Property property = propertyRepository.findById(propertyFeeEditBindingModel.getPropertyId()).orElse(null);

        if (property != null){
            property.setOverpayment(propertyFeeEditBindingModel.getOverPayment());
            propertyRepository.save(property);
        }
    }

    @Override
    public void setMonthlyFee(BigDecimal monthlyFee, Property property) {
        property.setMonthlyFee(monthlyFee);
        propertyRepository.save(property);
    }
}
