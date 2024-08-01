package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.*;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyCreateBindingModel;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyEditBindingModel;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyRegisterBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.OverpaymentBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.PropertyFeeEditBindingModel;
import com.example.OurHome.repo.PropertyRepository;
import com.example.OurHome.repo.ResidentialEntityRepository;
import com.example.OurHome.service.*;
import com.example.OurHome.service.events.PropertyCreationEvent;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class PropertyServiceImpl implements PropertyService {

    private final ModelMapper modelMapper;
    private final PropertyRepository propertyRepository;
    private final MessageService messageService;
    private final FeeService feeService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ResidentialEntityRepository residentialEntityRepository;
    private final PropertyRegisterRequestService propertyRegisterRequestService;
    private final PropertyChangeRequestService propertyChangeRequestService;


    public PropertyServiceImpl(ModelMapper modelMapper, PropertyRepository propertyRepository, MessageService messageService, FeeService feeService, ApplicationEventPublisher applicationEventPublisher, ResidentialEntityRepository residentialEntityRepository, PropertyRegisterRequestService propertyRegisterRequestService, PropertyChangeRequestService propertyChangeRequestService) {
        this.modelMapper = modelMapper;
        this.propertyRepository = propertyRepository;
        this.messageService = messageService;
        this.feeService = feeService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.residentialEntityRepository = residentialEntityRepository;
        this.propertyRegisterRequestService = propertyRegisterRequestService;
        this.propertyChangeRequestService = propertyChangeRequestService;
    }

    /**
     * Private class method to check if there is need to check propertyType for differences.
     * If some of the fields in the if statement of the method are changed property is set to verified=false
     *
     * @param property                 The property that is going to be changed.
     * @param propertyEditBindingModel the binding model with the data returning from frontend
     * @return TRUE if propertyType need verification from manager/moderator. FALSE if no need of verification
     */
    private static boolean isPropertyTypeCheck(PropertyEditBindingModel propertyEditBindingModel, Property property) {

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

        boolean propertyTypeCheck = false;
        if (property.isObtained()) {
            propertyTypeCheck = !currentPropertyTypeId.equals(newPropertyTypeId);
        }
        return propertyTypeCheck;
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
                && propertyRegisterRequestService
                .checkForNoActivePropertyRegisterRequest(propertyNumber, residentialEntityId)) {

            boolean verificationRequired = validationIsRequired(property.getId(), propertyRegisterBindingModel);

            //Set propertyRq data
            PropertyRegisterRequest propertyRegisterRequest = modelMapper.map(propertyRegisterBindingModel, PropertyRegisterRequest.class);

            propertyRegisterRequest.setOwner(loggedUser);
            propertyRegisterRequest.setResidentialEntityId(residentialEntityId);
            propertyRegisterRequest.setCreationDateTime(LocalDateTime.now());
            propertyRegisterRequest.setLastModificationDateTime(LocalDateTime.now());
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
     * Property approval with data change.
     * Performed by Residential entity MANAGER
     *
     * @param id property id
     */
    @Override
    public void approvePropertyRegistrationWithDataChange(Long id, boolean validationRequired) {

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
                //publish event to create first fee after approving property change that is still not validated.
                publishEvent(property);
                property.setValidated(true);
            }

            // Update (recalculate) property fee
            if (validationRequired) {
                updatePropertyFee(property.getResidentialEntity(), property, property.getPropertyType());
            }
            propertyRepository.save(property);
        }
    }

    /**
     * Property approval without data change (ignore data change input from owner).
     * Performed by Residential entity MANAGER
     *
     * @param id property id
     */
    @Override
    public void approvePropertyRegistrationWithoutDataChange(Long id) {

        Property property = getProperty(id);

        if (property != null && property.isValidated()) {
            PropertyRegisterRequest propertyRegisterRequest = property.getPropertyRegisterRequest();

            //invalidate property request
            propertyRegisterRequestService.invalidateRequest(propertyRegisterRequest);
            property.setPropertyRegisterRequest(null);

            property.setNumberOfRooms(propertyRegisterRequest.getNumberOfRooms());
            property.setParkingAvailable(propertyRegisterRequest.isParkingAvailable());
            property.setObtained(true);
            property.setRejected(false);

            if (!property.isValidated()) {
                //publish event to create first fee after approving property change that is still not validated.
                publishEvent(property);
                property.setValidated(true);
            }

            propertyRepository.save(property);

            messageService.propertyRegistrationApprovedWithNoChangesMessage(property);
        }
    }

    /**
     * Data change request approval.
     * Performed by Residential entity MANAGER
     *
     * @param propertyId property id
     */
    @Override
    public void approvePropertyChangeRequest(Long propertyId) {

        Property property = getProperty(propertyId);

        if (property != null && property.isValidated()) {
            PropertyChangeRequest propertyChangeRequest = property.getPropertyChangeRequest();

            //invalidate property request
            propertyChangeRequestService.invalidateRequest(propertyChangeRequest);
            property.setPropertyChangeRequest(null);

            property.setFloor(propertyChangeRequest.getFloor());
            property.setNumberOfAdults(propertyChangeRequest.getNumberOfAdults());
            property.setNumberOfChildren(propertyChangeRequest.getNumberOfChildren());
            property.setNumberOfPets(propertyChangeRequest.getNumberOfPets());
            property.setNotHabitable(propertyChangeRequest.isNotHabitable());
            property.setPropertyType(propertyChangeRequest.getPropertyType());

            // Update (recalculate) property fee
            updatePropertyFee(property.getResidentialEntity(), property, property.getPropertyType());

            propertyRepository.save(property);

            messageService.propertyChangeRequestApproved(property);
        }
    }

    /**
     * Data change request rejection.
     * Performed by Residential entity MANAGER
     *
     * @param propertyId property id
     */
    @Override
    public void rejectPropertyChangeRequest(Long propertyId) {

        Property property = getProperty(propertyId);

        if (property != null && property.isValidated()) {
            PropertyChangeRequest propertyChangeRequest = property.getPropertyChangeRequest();

            //set propertyChangeRequest to rejected
            propertyChangeRequestService.markChangeRequestAsRejected(propertyChangeRequest);

            messageService.propertyChangeRequestRejected(property);
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
     * Method is used for preparing propertyEditBindingModel in case of rejected property.
     * Using this method instead of mapPropertyToEditBindingModel() because we don't want to show property
     * data to new owner before request been approved.
     * Performed by user.
     *
     * @param property property entity
     */
    @Override
    public PropertyEditBindingModel mapRegistrationRequestToEditBindingModel(Property property) {
        return modelMapper.map(property.getPropertyRegisterRequest(), PropertyEditBindingModel.class);
    }

    @Override
    public PropertyEditBindingModel mapChangeRequestToEditBindingModel(Property property) {

        PropertyEditBindingModel propertyEditBindingModel = new PropertyEditBindingModel();

        PropertyChangeRequest propertyChangeRequest = property.getPropertyChangeRequest();
        if (propertyChangeRequest != null) {
            propertyEditBindingModel = modelMapper.map(propertyChangeRequest, PropertyEditBindingModel.class);
            propertyEditBindingModel.setPropertyType(propertyChangeRequest.getId());
        }

        return propertyEditBindingModel;
    }

    /**
     * Method for setting all properties monthly (auto) fee in current residential entity ON
     * Performed only by Residential Entity manager!
     *
     * @param residentialEntity current residential entity
     */
    @Override
    public void turnAllPropertiesFeesOn(ResidentialEntity residentialEntity) {
        List<Property> allProperties = residentialEntity.getProperties();

        if (!allProperties.isEmpty()) {
            allProperties.forEach(property -> property.setAutoFee(true));
            propertyRepository.saveAll(allProperties);
        }
    }

    /**
     * Method for setting all properties monthly (auto) fee in current residential entity OFF
     * Performed only by Residential Entity manager!
     *
     * @param residentialEntity current residential entity
     */
    @Override
    public void turnAllPropertiesFeesOff(ResidentialEntity residentialEntity) {
        List<Property> allProperties = residentialEntity.getProperties();

        if (!allProperties.isEmpty()) {
            allProperties.forEach(property -> property.setAutoFee(false));
            propertyRepository.saveAll(allProperties);
        }
    }

    /**
     * Property edit method.
     * Performed only by Residential Entity manager!
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
        //reset inhabitants count if property not habitable
        modelMapper.map(propertyEditBindingModel, property);
        if (property.isNotHabitable()) {
            property.setNumberOfPets(0);
            property.setNumberOfChildren(0);
            property.setNumberOfAdults(0);
        }

        // Update (recalculate) property fee
        updatePropertyFee(residentialEntity, property, propertyType);

        //publish event to create first fee after editing (setting) property for the first time.
        if (!property.isValidated()) {
            publishEvent(property);
            property.setValidated(true);
        }

        property.setPropertyType(propertyType);

        propertyRepository.save(property);


        //sending message to owner if owner registration is completed
        if (property.isObtained()) {
            messageService.propertyModificationMessageToResident(property);
        }

        return true;
    }

    /**
     * Property data change in case of changes of non-fee component data change (fast-lane change).
     * Performed only by Owner!
     *
     * @param property                 current property
     * @param propertyEditBindingModel the binding model with the data returning from frontend
     */
    @Override
    public void updateNonFeeComponentData(Property property, PropertyEditBindingModel propertyEditBindingModel, PropertyType propertyType) {

        property.setParkingAvailable(propertyEditBindingModel.isParkingAvailable());
        property.setNumberOfRooms(propertyEditBindingModel.getNumberOfRooms());
        propertyRepository.save(property);
    }

    /**
     * Property change request method.
     * Performed only by Owner!
     *
     * @param id                       property id
     * @param propertyEditBindingModel the binding model with the data returning from frontend
     * @param propertyType             propertyType if set
     * @param loggedUser               logged user data
     */
    @Override
    public boolean processChangeRequest(Long id, PropertyEditBindingModel propertyEditBindingModel, PropertyType propertyType, UserEntity loggedUser) {

        Property property = getProperty(id);
        Long propertyOwnerId = property.getOwner().getId();
        Long LoggedUserId = loggedUser.getId();

        //if property ownership is already set
        if (property.isObtained() && propertyOwnerId.equals(LoggedUserId)) {
            processChangeIfPropertyOwnerSet(propertyEditBindingModel, propertyType, property);
            return true;
        }
        //if property is rejected (ownership not obtained)
        if (property.isRejected()) {
            //Editing and resending registration request if property rejected
            processChangeIfPropertyRejected(propertyEditBindingModel, property);
            return true;
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
    public boolean validationIsRequired(Long id, PropertyEditBindingModel propertyEditBindingModel) {
        Property property = getProperty(id);
        if (property != null) {
            return property.getNumber() != propertyEditBindingModel.getNumber()
                    || !property.getFloor().equals(propertyEditBindingModel.getFloor())
                    || property.getNumberOfAdults() != propertyEditBindingModel.getNumberOfAdults()
                    || property.getNumberOfChildren() != propertyEditBindingModel.getNumberOfChildren()
                    || property.getNumberOfPets() != propertyEditBindingModel.getNumberOfPets()
                    || property.isNotHabitable() != propertyEditBindingModel.isNotHabitable()
                    || isPropertyTypeCheck(propertyEditBindingModel, property);
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
    public boolean validationIsRequired(Long id, PropertyRegisterBindingModel propertyRegisterBindingModel) {

        Property property = getProperty(id);
        if (property != null) {
            return property.getNumber() != propertyRegisterBindingModel.getNumber()
                    || !property.getFloor().equals(propertyRegisterBindingModel.getFloor())
                    || property.getNumberOfAdults() != propertyRegisterBindingModel.getNumberOfAdults()
                    || property.getNumberOfChildren() != propertyRegisterBindingModel.getNumberOfChildren()
                    || property.getNumberOfPets() != propertyRegisterBindingModel.getNumberOfPets()
                    || property.isNotHabitable() != propertyRegisterBindingModel.isNotHabitable();
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

        property.setAdditionalPropertyFee(Objects.requireNonNullElse(additionalPropertyFee, BigDecimal.ZERO));
        updateTotalMonthlyFee(property);
        
        propertyRepository.save(property);
    }

    @Override
    public void setPropertyType(Property property, PropertyType propertyType) {
        property.setPropertyType(propertyType);
        propertyRepository.save(property);
    }

    /**
     * Create single RE properties
     *
     * @param propertyCreateBindingModel property params from manager input
     */
    @Override
    public void createSingleProperty(PropertyCreateBindingModel propertyCreateBindingModel, ResidentialEntity residentialEntity, PropertyType propertyType) {

        Property newProperty = modelMapper.map(propertyCreateBindingModel, Property.class);
        newProperty.setAutoFee(true);
        newProperty.setResidentialEntity(residentialEntity);
        newProperty.setObtained(false);
        newProperty.setMonthlyFeeFundMm(feeService.calculateFundMm(newProperty.getResidentialEntity(), newProperty));
        newProperty.setMonthlyFeeFundRepair(feeService.calculateFundRepair(newProperty.getResidentialEntity(), newProperty));
        newProperty.setAdditionalPropertyFee(BigDecimal.ZERO);
        newProperty.setValidated(true);

        if (propertyType != null) {
            newProperty.setPropertyType(propertyType);
        }

        //update totalMonthlyFee
        updateTotalMonthlyFee(newProperty);

        propertyRepository.save(newProperty);

        //publish event to create first fee.
        publishEvent(newProperty);
    }

    /**
     * Create all RE properties
     *
     * @param newResidentialEntity current RE
     */
    @Override
    public void createAllProperties(ResidentialEntity newResidentialEntity, Long numberOfApartments) {

        List<Property> properties = new ArrayList<>();

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
            updateTotalMonthlyFee(newProperty);

            properties.add(newProperty);
        }
        propertyRepository.saveAll(properties);
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

    /**
     * Private method for processing change request if property is already obtained by an owner.!
     */
    private void processChangeIfPropertyOwnerSet(PropertyEditBindingModel propertyEditBindingModel, PropertyType propertyType, Property property) {

        //if property has no active change request assigned
        if (property.getPropertyChangeRequest() == null) {

            PropertyChangeRequest newPropertyChangeRequest = modelMapper.map(propertyEditBindingModel, PropertyChangeRequest.class);
            newPropertyChangeRequest.setResidentialEntityId(property.getResidentialEntity().getId());
            newPropertyChangeRequest.setActive(true);
            newPropertyChangeRequest.setProperty(property);
            newPropertyChangeRequest.setPropertyType(propertyType);
            newPropertyChangeRequest.setRejected(false);
            newPropertyChangeRequest.setCreationDateTime(LocalDateTime.now());
            newPropertyChangeRequest.setLastModificationDateTime(LocalDateTime.now());

            //Save PropertyRq in DB and return DB entity
            PropertyChangeRequest propertyChangeRequest = propertyChangeRequestService.saveRequestToDBAndReturnEntity(newPropertyChangeRequest);

            property.setPropertyChangeRequest(propertyChangeRequest);
            propertyRepository.save(property);

        } else {
            //if property has an active change request assigned
            PropertyChangeRequest existingPropertyChangeRequest = property.getPropertyChangeRequest();

            //update existing change request data
            modelMapper.map(propertyEditBindingModel, existingPropertyChangeRequest);
            existingPropertyChangeRequest.setActive(true);
            existingPropertyChangeRequest.setRejected(false);
            existingPropertyChangeRequest.setPropertyType(propertyType);
            existingPropertyChangeRequest.setLastModificationDateTime(LocalDateTime.now());

            propertyChangeRequestService.save(existingPropertyChangeRequest);
        }


        //sending message (notification) to manager
        messageService.propertyModificationMessageToManager(property);
    }

    /**
     * Private method for processing change request if property is rejected.!
     */
    private void processChangeIfPropertyRejected(PropertyEditBindingModel propertyEditBindingModel, Property property) {

        if (property.getPropertyRegisterRequest() != null) {

            //Step 1. Updating property register request
            PropertyRegisterRequest propertyRegisterRequest = updatePropertyRegisterRequest(propertyEditBindingModel, property);

            //if validation is required (input fee components data equals to property data)
            if (validationIsRequired(property.getId(), propertyEditBindingModel)) {
                messageService.propertyModificationMessageToManager(property);
                //if validation is not required (input fee components data equals to property data)
            } else {
                property.setParkingAvailable(propertyRegisterRequest.isParkingAvailable());
                property.setNumberOfRooms(propertyRegisterRequest.getNumberOfRooms());
                property.setObtained(true);
                propertyRegisterRequestService.invalidateRequest(property.getPropertyRegisterRequest());
                property.setPropertyRegisterRequest(null);

                //sending message (notification) to manager
                messageService.propertyRegistrationMessageToManager(property.getResidentialEntity());
            }
            property.setRejected(false);
            propertyRepository.save(property);
        }
    }


    /**
     * Private method for updating property register request.
     * Used when editing a property with active registration request
     */
    private PropertyRegisterRequest updatePropertyRegisterRequest(PropertyEditBindingModel propertyEditBindingModel, Property property) {
        PropertyRegisterRequest propertyRegisterRequest = property.getPropertyRegisterRequest();

        propertyRegisterRequest.setFloor(propertyEditBindingModel.getFloor());
        propertyRegisterRequest.setNumberOfAdults(propertyEditBindingModel.getNumberOfAdults());
        propertyRegisterRequest.setNumberOfChildren(propertyEditBindingModel.getNumberOfChildren());
        propertyRegisterRequest.setNumberOfPets(propertyEditBindingModel.getNumberOfPets());
        propertyRegisterRequest.setNotHabitable(propertyEditBindingModel.isNotHabitable());
        propertyRegisterRequest.setNumberOfRooms(propertyEditBindingModel.getNumberOfRooms());
        propertyRegisterRequest.setParkingAvailable(propertyEditBindingModel.isParkingAvailable());
        propertyRegisterRequest.setNumberOfRooms(propertyEditBindingModel.getNumberOfRooms());
        propertyRegisterRequest.setLastModificationDateTime(LocalDateTime.now());
        propertyRegisterRequestService.save(propertyRegisterRequest);

        return propertyRegisterRequest;
    }

    /**
     * Private method for updating property fee.
     */
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
    private void updateTotalMonthlyFee(Property property) {
        property.setTotalMonthlyFee(Objects.requireNonNullElse(property.getMonthlyFeeFundMm(), BigDecimal.ZERO)
                .add(property.getMonthlyFeeFundRepair())
                .add(property.getAdditionalPropertyFee()));
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

    /**
     * Private method for event publishing. This will trigger createFirstFee in PropertyFeeServiceImlp class
     *
     * @param property current property
     */
    private void publishEvent(Property property) {
        applicationEventPublisher.publishEvent(new PropertyCreationEvent("PropertyService", property));
    }

    private Property getProperty(Long id) {
        return propertyRepository.findById(id).orElse(null);
    }
}
