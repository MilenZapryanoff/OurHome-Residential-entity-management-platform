package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.PropertyType;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyEditBindingModel;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyRegisterBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.OverpaymentBindingModel;
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
import java.util.Objects;

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
    public boolean newProperty(PropertyRegisterBindingModel propertyRegisterBindingModel, UserEntity loggedUser) {

        if (propertyRepository.countPropertiesByNumber(propertyRegisterBindingModel.getNumber()) > 0) {
            return false;
        }

        Property newProperty = modelMapper.map(propertyRegisterBindingModel, Property.class);
        Long residentialEntityId = propertyRegisterBindingModel.getResidentialEntity();

        ResidentialEntity residentialEntity = residentialEntityService
                .findResidentialEntityById(residentialEntityId)
                .orElse(null);

        newProperty.setResidentialEntity(residentialEntity);
        newProperty.setOwner(loggedUser);
        newProperty.setAutoFee(true);
        newProperty.setValidated(false);
        newProperty.setTotalMonthlyFee(BigDecimal.ZERO);
        newProperty.setAdditionalPropertyFee(BigDecimal.ZERO);

        propertyRepository.save(newProperty);

        //sending message to residential entity manager
        messageService.propertyRegistrationMessageToManager(residentialEntity);
        return true;
    }

    /**
     * Single Property deletion
     * Performed by USER or MANAGER
     *
     * @param id property id
     */
    public void deleteProperty(Long id, boolean deletedByManager) {
        Property property = getProperty(id);
        if (property != null) {
            propertyRepository.delete(property);

            if (deletedByManager) {
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

        Property property = getProperty(id);
        if (property != null) {

            //set monthly fee for a first time
            property.setMonthlyFeeFundMm(feeService.calculateFundMm(property.getResidentialEntity(), property));
            property.setMonthlyFeeFundRepair(feeService.calculateFundRepair(property.getResidentialEntity(), property));

            property.setValidated(true);

            //update totalMonthlyFee. Default value before approve is 0.00
            updateTotalMonthlyFee(property, property.getAdditionalPropertyFee());

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
        Property property = getProperty(id);
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
     * @param noValidationNeed         TRUE if change is made by moderator or no sensitive data changed, FALSE if change is made by owner and sensitive data changed
     * @param propertyType             propertyType if set
     */

    @Override
    public boolean editProperty(Long id, PropertyEditBindingModel propertyEditBindingModel, boolean noValidationNeed, PropertyType propertyType) {

        Property property = getProperty(id);

        //fail data change request if trying to duplicate existing property number and NOT an own-property change.
        if (propertyRepository.countPropertiesByNumber(propertyEditBindingModel.getNumber()) > 0 &&
                !propertyEditBindingModel.getNumber().equals(property.getNumber())) {
            return false;
        }

        if (property != null) {
            modelMapper.map(propertyEditBindingModel, property);

            ResidentialEntity residentialEntity = property.getResidentialEntity();

            if (property.getMonthlyFeeFundMm() != null || noValidationNeed) {

                BigDecimal fundMm = feeService.calculateFundMm(residentialEntity, property);
                BigDecimal fundRepair = feeService.calculateNewFundRepair(property, propertyType);

                property.setMonthlyFeeFundMm(fundMm);
                property.setMonthlyFeeFundRepair(fundRepair);
                property.setTotalMonthlyFee(fundMm.add(fundRepair).add(property.getAdditionalPropertyFee()));

            }

            if (noValidationNeed) {
                property.setValidated(true);
            } else {
                property.setValidated(false);
                //sending message (notification) to manager
                messageService.propertyModificationMessageToManager(property);
            }

            //reset rejected status
            property.setRejected(false);
            property.setPropertyType(propertyType);
            propertyRepository.save(property);
        }
        return true;
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
    public boolean checkNeedOfVerification(Long id, PropertyEditBindingModel propertyEditBindingModel) {
        Property property = getProperty(id);
        if (property != null) {

            //setting current and new propertyTypes ids to -1 to exclude null values.
            Long currentPropertyTypeId = (long) -1;
            Long newPropertyTypeId = (long) -1;

            //checking if current propertyType is not null.
            if (property.getPropertyType() != null) {
                currentPropertyTypeId = property.getPropertyType().getId();
            }

            //checking if new propertyType is not null.
            if (propertyEditBindingModel.getPropertyType() != null) {
                newPropertyTypeId = propertyEditBindingModel.getPropertyType();
            }

            return property.isRejected() || !property.getNumber().equals(propertyEditBindingModel.getNumber()) ||
                    !property.getFloor().equals(propertyEditBindingModel.getFloor()) ||
                    property.getNumberOfAdults() != propertyEditBindingModel.getNumberOfAdults() ||
                    property.getNumberOfChildren() != propertyEditBindingModel.getNumberOfChildren() ||
                    property.getNumberOfPets() != propertyEditBindingModel.getNumberOfPets() ||
                    property.isNotHabitable() != propertyEditBindingModel.isNotHabitable() ||
                    !currentPropertyTypeId.equals(newPropertyTypeId);
        }
        return false;
    }

    @Override
    public List<Property> findAllProperties() {
        return propertyRepository.findAll();
    }

    @Override
    public void changeAutoFeeGeneration(Property property) {
        property.setAutoFee(!property.isAutoFee());
        propertyRepository.save(property);
    }

    @Override
    public void setOverpayment(PropertyFeeEditBindingModel propertyFeeEditBindingModel) {
        Property property = getProperty(propertyFeeEditBindingModel.getPropertyId());

        if (property != null) {
            property.setOverpayment(propertyFeeEditBindingModel.getOverPayment());
            propertyRepository.save(property);
        }
    }

    @Override
    public OverpaymentBindingModel mapOverPaymentBindingModel(Property property) {
        return modelMapper.map(property, OverpaymentBindingModel.class);
    }

    /**
     * Update overpayment method
     */
    @Override
    public void updateOverpayment(Property property, BigDecimal overPayment) {
        property.setOverpayment(Objects.requireNonNullElseGet(overPayment, () -> BigDecimal.valueOf(0)));
        propertyRepository.save(property);
    }

    /**
     * Update additional property fee method
     */
    @Override
    public void setAdditionalPropertyFee(Property property, BigDecimal additionalPropertyFee) {

        if (additionalPropertyFee == null) {
            additionalPropertyFee = BigDecimal.ZERO;
        }

        property.setAdditionalPropertyFee(additionalPropertyFee);
        updateTotalMonthlyFee(property, additionalPropertyFee);
    }

    @Override
    public Property findPropertyByNumberAndResidentialEntity(String propertyNumber, Long residentialEntityId) {
        return propertyRepository.findByPropertyNumberAndResidentialEntityId(propertyNumber, residentialEntityId);
    }

    @Override
    public void setPropertyType(Property property, PropertyType propertyType) {
        property.setPropertyType(propertyType);
        propertyRepository.save(property);
    }

    @Override
    public List<Property> findAllPropertiesByPropertyType(PropertyType propertyType) {
        return propertyRepository.findAllPropertiesByPropertyType(propertyType);
    }




    /**
     * Private method for Total monthly fee update. Necessary in case of monthly fee modification!
     */
    private void updateTotalMonthlyFee(Property property, BigDecimal additionalPropertyFee) {
        property.setTotalMonthlyFee
                (Objects.requireNonNullElse(property.getMonthlyFeeFundMm(), BigDecimal.ZERO)
                        .add(property.getMonthlyFeeFundRepair())
                        .add(additionalPropertyFee));
        propertyRepository.save(property);
    }

    private Property getProperty(Long id) {
        return propertyRepository.findById(id).orElse(null);
    }
}
