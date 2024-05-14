package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.*;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyEditBindingModel;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyRegisterBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.OverpaymentBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.PropertyFeeEditBindingModel;
import com.example.OurHome.model.events.PropertyApprovalEvent;
import com.example.OurHome.repo.PropertyRegisterRequestRepository;
import com.example.OurHome.repo.PropertyRepository;
import com.example.OurHome.repo.ResidentialEntityRepository;
import com.example.OurHome.service.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
public class PropertyServiceImpl implements PropertyService {

    private static final Logger log = LoggerFactory.getLogger(PropertyServiceImpl.class);
    //private static final Logger log = LoggerFactory.getLogger(PropertyServiceImpl.class);
    private final ModelMapper modelMapper;
    private final PropertyRepository propertyRepository;
    private final MessageService messageService;
    private final FeeService feeService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ResidentialEntityRepository residentialEntityRepository;
    private final PropertyRegisterRequestService propertyRegisterRequestService;
    private final PropertyChangeRequestService propertyChangeRequestService;
    private final PropertyRegisterRequestRepository propertyRegisterRequestRepository;

    public PropertyServiceImpl(ModelMapper modelMapper, PropertyRepository propertyRepository, MessageService messageService, FeeService feeService, ApplicationEventPublisher applicationEventPublisher, ResidentialEntityRepository residentialEntityRepository, PropertyRegisterRequestService propertyRegisterRequestService, PropertyChangeRequestService propertyChangeRequestService, PropertyRegisterRequestRepository propertyRegisterRequestRepository) {
        this.modelMapper = modelMapper;
        this.propertyRepository = propertyRepository;
        this.messageService = messageService;
        this.feeService = feeService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.residentialEntityRepository = residentialEntityRepository;
        this.propertyRegisterRequestService = propertyRegisterRequestService;
        this.propertyChangeRequestService = propertyChangeRequestService;
        this.propertyRegisterRequestRepository = propertyRegisterRequestRepository;
    }

    /**
     * Property obtain from owner. Performed by USER
     *
     * @param propertyRegisterBindingModel the binding model with the data from frontend
     * @param loggedUser                   logged user.
     */
    @Override
    @Transactional
    public boolean requestToObtainProperty(PropertyRegisterBindingModel propertyRegisterBindingModel, UserEntity loggedUser) {

        int propertyNumber = propertyRegisterBindingModel.getNumber();
        Long residentialEntityId = propertyRegisterBindingModel.getResidentialEntity();

        ResidentialEntity residentialEntity = residentialEntityRepository.findById(residentialEntityId).orElse(null);
        Property property = propertyRepository.findByPropertyNumberAndResidentialEntityId(propertyNumber, residentialEntityId);

        //proceed with obtaining of property if
        if (property != null
                //if no request set for this property
                && property.getPropertyRegisterRequest() == null
                //if there is no owner already set
                && property.getOwner() == null
                //if there is no active pending request for this property. (This should not happen at all)
                && propertyRegisterRequestService.checkForNoActivePropertyRegisterRequest(propertyNumber, residentialEntityId)) {

            boolean verificationRequired = checkNeedOfVerification(property.getId(), propertyRegisterBindingModel);

            //Set propertyRq data
            PropertyRegisterRequest propertyRegisterRequest = modelMapper.map(propertyRegisterBindingModel, PropertyRegisterRequest.class);
            propertyRegisterRequest.setOwner(loggedUser);
            propertyRegisterRequest.setResidentialEntityId(residentialEntityId);
            propertyRegisterRequest.setProperty(null);

            if (verificationRequired) {
                propertyRegisterRequest.setActive(true);
            }

            //Save PropertyRq in DB and return DB entity
            PropertyRegisterRequest newPropertyRegisterRequest = propertyRegisterRequestService.saveRequestToDBAndReturnEntity(propertyRegisterRequest);

            property.setOwner(loggedUser);
            property.setRejected(false);

            if (verificationRequired) {
                property.setPropertyRegisterRequest(newPropertyRegisterRequest);
                //sending message to residential entity manager for pending approval
                messageService.propertyPendingRegistrationMessageToManager(residentialEntity);
            } else {
                property.setObtained(true);
                //sending message to residential entity manager for auto-approval
                messageService.propertyRegistrationMessageToManager(residentialEntity);
            }

            property.setNumberOfRooms(propertyRegisterRequest.getNumberOfRooms());
            property.setParkingAvailable(propertyRegisterRequest.isParkingAvailable());

            propertyRepository.save(property);
            return true;
        }
        return false;
    }

    /**
     * Property owner remove. Property remains active.
     * Performed by USER or MANAGER
     *
     * @param id property id
     */
    public void unlinkOwner(Long id, boolean deletedByManager) {
        Property property = getProperty(id);

        if (property != null) {

            //if property has a pending registration request then invalidate it.
            if (property.getPropertyRegisterRequest() != null) {
                clearPendingPropertyRegisterRequests(property);
            }
            //if property has a pending change request then invalidate it.
            if (property.getPropertyChangeRequest() != null) {
                clearPendingPropertyChangeRequests(property);
            }

            //reset property to no-owner
            property.setOwner(null);
            property.setRejected(false);
            property.setObtained(false);

            //reset personal property data
            //If some of the fields becomes usable by RE Manager in some stage, personal data deletion may have to be removed!
            property.setNumberOfRooms(String.valueOf(0));
            property.setParkingAvailable(false);

            propertyRepository.save(property);

            if (deletedByManager) {
                messageService.propertyDeletedMessageToOwner(property);
            } else {
                messageService.propertyDeletedMessageToManager(property);
            }
        }
    }

    /**
     * User (owner) remove by RE manager. All owned properties remains active but with no owner.
     * Performed only by MANAGER
     *
     * @param residentId        is user (owner) id
     * @param residentialEntity is the current residentialEntity
     */
    @Override
    public void unlinkAllPropertiesFromOwner(Long residentId, ResidentialEntity residentialEntity) {
        List<Property> allUserProperties = propertyRepository.findAllUserProperties(residentId, residentialEntity.getId());

        if (!allUserProperties.isEmpty()) {
            allUserProperties.forEach(property -> {

                //if property has a pending registration/change request invalidate it.
                if (property.getPropertyRegisterRequest() != null) {
                    clearPendingPropertyRegisterRequests(property);
                }

                //reset property to no-owner
                property.setOwner(null);
                property.setRejected(false);
                property.setObtained(false);
                propertyRepository.save(property);
            });
        }
    }

    /**
     * Property approval.
     * Performed by Residential entity MANAGER
     *
     * @param id property id
     */
    @Override
    public void approveProperty(Long id, boolean noValidationNeed) {

        Property property = getProperty(id);

        if (property != null) {
            PropertyRegisterRequest propertyRegisterRequest = property.getPropertyRegisterRequest();

            //deactivating property request
            propertyRegisterRequestService.invalidateRequest(propertyRegisterRequest);

            modelMapper.map(propertyRegisterRequest, property);
            property.setId(id);
            property.setPropertyRegisterRequest(null);
            property.setObtained(true);
            property.setNumberOfRooms(propertyRegisterRequest.getNumberOfRooms());
            property.setParkingAvailable(propertyRegisterRequest.isParkingAvailable());

            if (!property.isValidated()) {
                property.setValidated(true);
            }

            // Update (recalculate) property fee
            if (noValidationNeed) {
                updatePropertyFee(property.getResidentialEntity(), property, property.getPropertyType());
            }
            propertyRepository.save(property);
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
     * Single Property deletion
     * Performed by USER or MANAGER
     *
     * @param id property id
     */
    public void deleteProperty(Long id, boolean deletedByManager) {

        Property property = getProperty(id);

        //invalidation of active registration/change request if such exists for this property
        if (property.getPropertyRegisterRequest() != null) {
            propertyRegisterRequestService.invalidateRequest(property.getPropertyRegisterRequest());
        }

        propertyRepository.delete(property);

        if (deletedByManager && property.getOwner() != null && property.isObtained()) {
            messageService.propertyDeletedMessageToOwner(property);
        }
    }

    /**
     * Property edit method.
     * Performed only by Residential Entitiy manager!
     *
     * @param id                       property id
     * @param propertyEditBindingModel the binding model with the data returning from frontend
     * @param propertyType             propertyType if set
     */
    @Override
    public boolean editProperty(Long id, PropertyEditBindingModel propertyEditBindingModel, PropertyType propertyType) {

        Property property = getProperty(id);
        ResidentialEntity residentialEntity = property.getResidentialEntity();

        //fail data change request if trying to duplicate existing property number AND NOT an owned-property.
        if (propertyRepository.countPropertiesByNumber(propertyEditBindingModel.getNumber()) > 0
                && propertyEditBindingModel.getNumber() != (property.getNumber())) {
            return false;
        }

        modelMapper.map(propertyEditBindingModel, property);

        // Update (recalculate) property fee
        updatePropertyFee(residentialEntity, property, propertyType);

        property.setValidated(true);

        property.setPropertyType(propertyType);
        propertyRepository.save(property);

        //sending message to owner if owner registration is completed
        if (property.isObtained()) {
            messageService.propertyModificationMessageToResident(property);
        }

        return true;
    }


    /**
     * Property change request method.
     * Performed only by Owner!
     *
     * @param id                       property id
     * @param propertyEditBindingModel the binding model with the data returning from frontend
     * @param propertyType             propertyType if set
     * @param loggedUser               logged user data
     * @param validationRequired       boolean showing if data modification requires manager validation
     */
    @Override
    public boolean propertyChangeRequest(Long id, PropertyEditBindingModel propertyEditBindingModel, PropertyType propertyType, UserEntity loggedUser, boolean validationRequired) {

        Property property = getProperty(id);
        ResidentialEntity residentialEntity = property.getResidentialEntity();

        //if there are no changes of fee component data
        if (!validationRequired) {
            property.setParkingAvailable(propertyEditBindingModel.isParkingAvailable());
            property.setNumberOfRooms(propertyEditBindingModel.getNumberOfRooms());
            propertyRepository.save(property);
            return true;
        } else {

            Long propertyOwnerId = property.getOwner().getId();
            Long LoggedUserId = loggedUser.getId();

            //if property ownership is already set
            if (property.isObtained() && propertyOwnerId.equals(LoggedUserId)) {
                //if there is no change request linked to this property
                if (property.getPropertyChangeRequest() == null) {
                    PropertyChangeRequest propertyChangeRequest = modelMapper.map(propertyEditBindingModel, PropertyChangeRequest.class);

                    propertyChangeRequest.setResidentialEntityId(property.getResidentialEntity().getId());
                    propertyChangeRequest.setActive(true);
                    propertyChangeRequest.setProperty(property);
                    propertyChangeRequest.setPropertyType(propertyType);

                    //Save PropertyRq in DB and return DB entity
                    PropertyChangeRequest newPropertyChangeRequest = propertyChangeRequestService.saveRequestToDBAndReturnEntity(propertyChangeRequest);

                    property.setPropertyChangeRequest(newPropertyChangeRequest);
                    propertyRepository.save(property);

                } else {
                    //if there is an existing property change request linked to this property.
                    PropertyChangeRequest existingPropertyChangeRequest = property.getPropertyChangeRequest();

                    //update change request data
                    modelMapper.map(propertyEditBindingModel, existingPropertyChangeRequest);
                    existingPropertyChangeRequest.setActive(true);
                    existingPropertyChangeRequest.setPropertyType(propertyType);

                    propertyChangeRequestService.save(existingPropertyChangeRequest);
                }

                //sending message (notification) to manager
                messageService.propertyModificationMessageToManager(property);

                return true;
            }
            //Editing and resending registration request if property rejected
            if (property.isRejected()) {
                PropertyRegisterRequest propertyRegisterRequest = property.getPropertyRegisterRequest();

                modelMapper.map(propertyEditBindingModel, propertyRegisterRequest);
                propertyRegisterRequest.setPropertyType(propertyType);

                propertyRegisterRequestRepository.save(propertyRegisterRequest);

                property.setRejected(false);
                propertyRepository.save(property);

                //sending message (notification) to manager
                messageService.propertyModificationMessageToManager(property);

                return true;
            }
        }

        return false;
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

            return property.isRejected()
                    || property.getNumber() != propertyEditBindingModel.getNumber()
                    || !property.getFloor().equals(propertyEditBindingModel.getFloor())
                    || property.getNumberOfAdults() != propertyEditBindingModel.getNumberOfAdults()
                    || property.getNumberOfChildren() != propertyEditBindingModel.getNumberOfChildren()
                    || property.getNumberOfPets() != propertyEditBindingModel.getNumberOfPets()
                    || property.isNotHabitable() != propertyEditBindingModel.isNotHabitable()
                    || !currentPropertyTypeId.equals(newPropertyTypeId);
        }
        return false;
    }

    /**
     * Check if the registered property data needs manager/moderator verification.
     * If some of the fields in the if statement of the method are changed property is set to verified=false
     *
     * @param id                           property id
     * @param propertyRegisterBindingModel the binding model with the data returning from frontend
     * @return TRUE if changes need verification from manager/moderator. FALSE if no need of verification
     */
    @Override
    public boolean checkNeedOfVerification(Long id, PropertyRegisterBindingModel propertyRegisterBindingModel) {

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
            if (propertyRegisterBindingModel.getPropertyType() != null) {
                newPropertyTypeId = propertyRegisterBindingModel.getPropertyType();
            }

            return property.getNumber() != propertyRegisterBindingModel.getNumber() || !property.getFloor().equals(propertyRegisterBindingModel.getFloor()) || property.getNumberOfAdults() != propertyRegisterBindingModel.getNumberOfAdults() || property.getNumberOfChildren() != propertyRegisterBindingModel.getNumberOfChildren() || property.getNumberOfPets() != propertyRegisterBindingModel.getNumberOfPets() || property.isNotHabitable() != propertyRegisterBindingModel.isNotHabitable() || !currentPropertyTypeId.equals(newPropertyTypeId);
        }
        return false;
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
    public void setPropertyType(Property property, PropertyType propertyType) {

        //TODO: to use this method for setting propertyType when approved by Manager
        property.setPropertyType(propertyType);
        propertyRepository.save(property);
    }

    /**
     * Create all RE properties
     *
     * @param newResidentialEntity current RE
     */
    @Override
    public void createAllProperties(ResidentialEntity newResidentialEntity, Long numberOfApartments) {

        for (int i = 1; i <= numberOfApartments; i++) {

            Property newProperty = new Property();
            newProperty.setAdditionalPropertyFee(BigDecimal.ZERO);
            newProperty.setAutoFee(true);
            newProperty.setNumber(i);
            newProperty.setNumberOfAdults(0);
            newProperty.setNumberOfChildren(0);
            newProperty.setNumberOfPets(0);
            newProperty.setFloor(String.valueOf(0));
            newProperty.setNumberOfRooms(String.valueOf(0));
            newProperty.setResidentialEntity(newResidentialEntity);
            newProperty.setObtained(false);
            newProperty.setMonthlyFeeFundMm(feeService.calculateFundMm(newProperty.getResidentialEntity(), newProperty));
            newProperty.setMonthlyFeeFundRepair(feeService.calculateFundRepair(newProperty.getResidentialEntity(), newProperty));
            newProperty.setValidated(false);

            //update totalMonthlyFee
            updateTotalMonthlyFee(newProperty, newProperty.getAdditionalPropertyFee());

            applicationEventPublisher.publishEvent(new PropertyApprovalEvent("PropertyService", newProperty));

            propertyRepository.save(newProperty);
        }
    }

    @Override
    public void deleteAllProperties(List<Property> residentialEntityProperties) {
        propertyRepository.deleteAll(residentialEntityProperties);
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

    @Override
    public boolean checkPropertiesForOwnersSet(Long residentialEntityId) {
        return propertyRepository.numberOfPropertiesWithOwnerSet(residentialEntityId) == 0;
    }

    private void updatePropertyFee(ResidentialEntity residentialEntity, Property property, PropertyType propertyType) {
        BigDecimal fundMm = feeService.calculateFundMm(residentialEntity, property);
        BigDecimal fundRepair = feeService.calculateNewFundRepair(property, propertyType);

        property.setMonthlyFeeFundMm(fundMm);
        property.setMonthlyFeeFundRepair(fundRepair);
        property.setTotalMonthlyFee(fundMm.add(fundRepair).add(property.getAdditionalPropertyFee()));
    }

    /**
     * Private method for Total monthly fee update. Necessary in case of monthly fee modification!
     */
    private void updateTotalMonthlyFee(Property property, BigDecimal additionalPropertyFee) {
        property.setTotalMonthlyFee(Objects.requireNonNullElse(property.getMonthlyFeeFundMm(), BigDecimal.ZERO).add(property.getMonthlyFeeFundRepair()).add(additionalPropertyFee));
        propertyRepository.save(property);
    }

    /**
     * Private method for clearing pending property register requests assigned to this property
     */
    private void clearPendingPropertyRegisterRequests(Property property) {
        //invalidation of property registration request
        propertyRegisterRequestService.invalidateRequest(property.getPropertyRegisterRequest());
        //removing relation between property and propertyRequest
        property.setPropertyRegisterRequest(null);
    }

    /**
     * Private method for clearing pending property change requests assigned to this property
     */
    private void clearPendingPropertyChangeRequests(Property property) {
        //invalidation of property change request
        propertyChangeRequestService.invalidateRequest(property.getPropertyChangeRequest());
        //removing relation between property and propertyRequest
        property.setPropertyChangeRequest(null);
    }

    @Override
    public Property findPropertyByNumberAndResidentialEntity(int propertyNumber, Long residentialEntityId) {
        return propertyRepository.findByPropertyNumberAndResidentialEntityId(propertyNumber, residentialEntityId);
    }

    @Override
    public Property findPropertyById(Long id) {
        return propertyRepository.findById(id).orElse(null);
    }

    @Override
    public List<Property> findAllPropertiesByPropertyType(PropertyType propertyType) {
        return propertyRepository.findAllPropertiesByPropertyType(propertyType);
    }

    @Override
    public List<Property> findAllProperties() {
        return propertyRepository.findAll();
    }

    private Property getProperty(Long id) {
        return propertyRepository.findById(id).orElse(null);
    }
}
