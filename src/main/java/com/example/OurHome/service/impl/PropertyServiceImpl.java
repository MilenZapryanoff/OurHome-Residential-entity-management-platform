package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.*;
import com.example.OurHome.model.dto.BindingModels.AddressBook.AdultBindingModel;
import com.example.OurHome.model.dto.BindingModels.AddressBook.ChildBindingModel;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyCreateBindingModel;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyEditBindingModel;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyRegisterBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.OverpaymentBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.PropertyFeeEditBindingModel;
import com.example.OurHome.repo.PropertyRepository;
import com.example.OurHome.repo.ResidentialEntityRepository;
import com.example.OurHome.repo.ResidentsRepository;
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
    private final NotificationService notificationService;
    private final ResidentsService residentsService;
    private static final BigDecimal DEFAULT_AMOUNT = BigDecimal.valueOf(0.00);
    private final LogService logService;
    private final ResidentsRepository residentsRepository;


    public PropertyServiceImpl(ModelMapper modelMapper, PropertyRepository propertyRepository, MessageService messageService, FeeService feeService, ApplicationEventPublisher applicationEventPublisher,
                               ResidentialEntityRepository residentialEntityRepository, PropertyRegisterRequestService propertyRegisterRequestService,
                               PropertyChangeRequestService propertyChangeRequestService, NotificationService notificationService, ResidentsService residentsService, LogService logService, ResidentsRepository residentsRepository) {
        this.modelMapper = modelMapper;
        this.propertyRepository = propertyRepository;
        this.messageService = messageService;
        this.feeService = feeService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.residentialEntityRepository = residentialEntityRepository;
        this.propertyRegisterRequestService = propertyRegisterRequestService;
        this.propertyChangeRequestService = propertyChangeRequestService;
        this.notificationService = notificationService;
        this.residentsService = residentsService;
        this.logService = logService;
        this.residentsRepository = residentsRepository;
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

                messageService.propertyPendingRegistrationMessageToManager(residentialEntity);
            } else {
                property.setObtained(true);
                //sending message to Condominium manager for auto-approval
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
     * Performed by OWNER
     *
     * @param id property id
     */
    public void unlinkOwner(Long id, boolean deletedByManager) {
        Property property = getProperty(id);
        UserEntity currentOwner = property.getOwner();

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
            messageService.ownerRemovedMessageToOwner(property, currentOwner);
        } else {
            messageService.propertyDeletedMessageToManager(property, currentOwner);
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
     * Performed by Condominium MANAGER
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

            messageService.propertyRegistrationApprovedMessage(property);
        }
    }

    /**
     * Property approval without data change (ignore data change input from owner).
     * Performed by Condominium MANAGER
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
     * Performed by Condominium MANAGER
     *
     * @param propertyId property id
     */
    @Override
    public void approvePropertyChangeRequest(Long propertyId) {

        Property property = getProperty(propertyId);

        if (property != null && property.isValidated()) {
            try {
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

                logService.info("✅[approvePropertyChangeRequest ->] Change request for property id: {} APPROVED", property.getId());
            } catch (Exception e) {
                logService.error("❌[approvePropertyChangeRequest ->] Failed to APPROVE change request for property id: {}! {}", property.getId(), e);
                return;
            }

            //send message to owner
            messageService.propertyChangeRequestApprovedMessage(property);
            //send notification to property owner for property change-request approval
            notificationService.propertyChangeRequestApprovedNotification(property);
        }
    }

    /**
     * Data change request rejection.
     * Performed by Condominium MANAGER
     *
     * @param propertyId property id
     */
    @Override
    public void rejectPropertyChangeRequest(Long propertyId) {

        Property property = getProperty(propertyId);
        if (property != null && property.isValidated()) {
            try {
                PropertyChangeRequest propertyChangeRequest = property.getPropertyChangeRequest();
                //set propertyChangeRequest to rejected
                propertyChangeRequestService.markChangeRequestAsRejected(propertyChangeRequest);

                logService.info("✅[rejectPropertyChangeRequest ->] Change request for property id: {} REJECTED", property.getId());
            } catch (Exception e) {
                logService.error("❌[rejectPropertyChangeRequest ->] Failed to REJECT change request for property id: {}! {}", property.getId(), e);
                return;
            }

            //send message to property owner
            messageService.propertyChangeRequestRejectedMessage(property);
            //send notification to property owner for property change-request approval
            notificationService.propertyChangeRequestRejectedNotification(property);
        }
    }

    /**
     * Property reject.
     * Performed by Condominium MANAGER
     *
     * @param id property id
     */
    @Override
    public void rejectProperty(Long id) {

        Property property = getProperty(id);
        if (property != null) {
            try {
                property.setRejected(true);
                propertyRepository.save(property);
                logService.info("✅[rejectProperty ->] Registration of property: {} REJECTED!", property.getId());
            } catch (Exception e) {
                logService.error("❌[rejectProperty ->] Failed REJECT registration of property: {}", property.getId(), e);
                return;
            }

            //send message to property owner for property rejection
            messageService.propertyRejectedMessage(property);
            //send notification to property owner for property rejection
            notificationService.propertyRejectedNotification(property);
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
     * Method for setting all properties monthly (auto) fee in current Condominium ON
     * Performed only by Condominium manager!
     *
     * @param residentialEntity current Condominium
     */
    @Override
    public void turnAllPropertiesFeesOn(ResidentialEntity residentialEntity) {
        try {
            List<Property> allProperties = residentialEntity.getProperties();

            if (!allProperties.isEmpty()) {
                allProperties.forEach(property -> property.setAutoFee(true));
                propertyRepository.saveAll(allProperties);
            }
            logService.info("✅[turnAllPropertiesFeesOn] All automatic monthly fees for condominium id: {} TURNED ON", residentialEntity.getId());
        } catch (Exception e) {
            logService.error("❌[turnAllPropertiesFeesOn] Failed to TURN ON all automatic monthly fees for condominium id: {}", residentialEntity.getId(), e);
        }
    }

    /**
     * Method for setting all properties monthly (auto) fee in current Condominium OFF
     * Performed only by Condominium manager!
     *
     * @param residentialEntity current Condominium
     */
    @Override
    public void turnAllPropertiesFeesOff(ResidentialEntity residentialEntity) {
        try {
            List<Property> allProperties = residentialEntity.getProperties();

            if (!allProperties.isEmpty()) {
                allProperties.forEach(property -> property.setAutoFee(false));
                propertyRepository.saveAll(allProperties);
            }
            logService.info("✅[turnAllPropertiesFeesOff] All automatic monthly fees for condominium id: {} TURNED OFF", residentialEntity.getId());
        } catch (Exception e) {
            logService.error("❌[turnAllPropertiesFeesOff] Failed to TURN OFF all automatic monthly fees for condominium id: {}!, {}", residentialEntity.getId(), e);
        }
    }

    /**
     * Property edit method.
     * Performed only by Condominium manager!
     *
     * @param id                       property id
     * @param propertyEditBindingModel the binding model with the data returning from frontend
     * @param propertyType             propertyType if set
     */

    @Override
    public boolean editProperty(Long id, PropertyEditBindingModel propertyEditBindingModel, PropertyType propertyType) {

        String ownerFullName = propertyEditBindingModel.getOwnerFullName();
        String ownerPhone = propertyEditBindingModel.getOwnerPhone();
        String ownerEmail = propertyEditBindingModel.getOwnerEmail();

        Property property = getProperty(id);
        ResidentialEntity residentialEntity = property.getResidentialEntity();

        //reject data change request if trying to duplicate existing property number AND NOT an owned-property.
        if (propertyRepository.countPropertiesByNumber(propertyEditBindingModel.getNumber()) > 0
                && propertyEditBindingModel.getNumber() != (property.getNumber())) {
            return false;
        }

        property.setNumber(propertyEditBindingModel.getNumber());
        property.setPropertyClass(propertyEditBindingModel.getPropertyClass());
        property.setFloor(propertyEditBindingModel.getFloor());

        if (propertyEditBindingModel.getAdults().isEmpty() && propertyEditBindingModel.getChildren().isEmpty() && propertyEditBindingModel.getNumberOfPets() == 0) {
            property.setNotHabitable(true);
        } else {
            property.setNotHabitable(propertyEditBindingModel.isNotHabitable());
        }

        if (property.isNotHabitable()) {
            property.setNumberOfAdults(0);
            property.setNumberOfChildren(0);
            property.setNumberOfPets(0);
        } else {
            property.setNumberOfAdults(propertyEditBindingModel.getAdults().size());
            property.setNumberOfChildren(propertyEditBindingModel.getChildren().size());
            property.setNumberOfPets(propertyEditBindingModel.getNumberOfPets());
        }

        //update address book
        residentsService.updateAddressBook(property, ownerFullName, ownerPhone, ownerEmail, propertyEditBindingModel);

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
            messageService.propertyModificationMessageToOwner(property);
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
        try {
            if (property.isAutoFee()) {
                property.setAutoFee(false);
                logService.info("✅[changeAutoFeeGeneration] Automatic monthly fee generation for property id: {} TURNED OFF", property.getId());
            } else {
                property.setAutoFee(true);
                logService.info("✅[changeAutoFeeGeneration] Automatic monthly fee generation for property id: {} TURNED ON", property.getId());
            }
            propertyRepository.save(property);

        } catch (Exception e) {
            logService.error("❌[changeAutoFeeGeneration] Failed to toggle automatic monthly fees for property id: {}", property.getId(), e);
        }
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
        property.setOverpayment(Objects.requireNonNullElse(overPayment, DEFAULT_AMOUNT));
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
     * Create single RE property
     *
     * @param propertyCreateBindingModel property params from manager input
     * @param residentialEntity the condominium where the property belongs
     * @param propertyType type of property
     */
    @Override
    public void createSingleProperty(PropertyCreateBindingModel propertyCreateBindingModel, ResidentialEntity residentialEntity, PropertyType propertyType) {

        Property newProperty = modelMapper.map(propertyCreateBindingModel, Property.class);
        newProperty.setAutoFee(true);
        newProperty.setResidentialEntity(residentialEntity);
        newProperty.setObtained(false);
        newProperty.setMonthlyFeeFundMm(feeService.calculateFundMm(newProperty.getResidentialEntity(), newProperty));
        newProperty.setMonthlyFeeFundRepair(feeService.calculateFundRepair(newProperty.getResidentialEntity(), newProperty));
        newProperty.setAdditionalPropertyFee(DEFAULT_AMOUNT);
        newProperty.setValidated(true);
        newProperty.setOwner(null);
        if (propertyType != null) {
            newProperty.setPropertyType(propertyType);
        }

        //calculation of number of occupants according to the input data about residents
        calculateNumberOfOccupants(propertyCreateBindingModel, newProperty);

        //update totalMonthlyFee
        updateTotalMonthlyFee(newProperty);

        propertyRepository.save(newProperty);

        //initialization of address book data with the input data about owner and residents
        initializeAddressBookData(propertyCreateBindingModel, residentialEntity.getId());

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

        try {
            List<Property> properties = new ArrayList<>();

            for (int i = 1; i <= numberOfApartments; i++) {

                Property newProperty = new Property();
                newProperty.setAdditionalPropertyFee(DEFAULT_AMOUNT);
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

        } catch (Exception e) {
            throw new IllegalArgumentException("❌ [createAllProperties] Failed to create properties for residential entity: " + newResidentialEntity, e);
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

        if (property == null) {
            return new PropertyEditBindingModel(); // празен модел при null
        }

        PropertyEditBindingModel model = modelMapper.map(property, PropertyEditBindingModel.class);

        //adding address book data
        for (Resident resident : property.getResidents()) {
            if (resident.isOwner()) {
                model.setOwnerFullName(resident.getName());
                model.setOwnerPhone(resident.getPhone());
                model.setOwnerEmail(resident.getEmail());
            } else if (resident.isAdult()) {
                model.getAdults().add(mapToAdultBindingModel(resident));
            } else {
                model.getChildren().add(mapToChildBindingModel(resident));
            }
        }
        return model;
    }

    /**
     * Private method for initialization of address book data for this property
     *
     * @param propertyCreateBindingModel data from user input
     * @param residentialEntityId        is the ID of the Residential entity
     */
    private void initializeAddressBookData(PropertyCreateBindingModel propertyCreateBindingModel, Long residentialEntityId) {

        Property property = propertyRepository.findByPropertyNumberAndResidentialEntityId(propertyCreateBindingModel.getNumber(), residentialEntityId);

        if (property != null) {

            List<Resident> newResidents = new ArrayList<>();

            //Step 1: Adding Adults to residents list
            if (!propertyCreateBindingModel.getAdults().isEmpty()) {

                List<AdultBindingModel> adults = propertyCreateBindingModel.getAdults();

                for (AdultBindingModel adult : adults) {

                    Resident resident = new Resident();
                    resident.setAdult(true);
                    resident.setName(adult.getName());
                    resident.setPhone(adult.getPhone());
                    resident.setEmail(adult.getEmail());
                    resident.setProperty(property);

                    newResidents.add(resident);
                }
            }

            //Step 2: Adding Children to residents list
            if (!propertyCreateBindingModel.getChildren().isEmpty()) {

                List<ChildBindingModel> children = propertyCreateBindingModel.getChildren();

                for (ChildBindingModel child : children) {

                    Resident resident = new Resident();
                    resident.setAdult(false);
                    resident.setName(child.getName());
                    resident.setAge(child.getAge());
                    resident.setProperty(property);

                    newResidents.add(resident);
                }
            }

            //Step 3: Adding residentOwner to residents list
            if (!propertyCreateBindingModel.getOwnerFullName().isEmpty() || !propertyCreateBindingModel.getOwnerPhone().isEmpty() || !propertyCreateBindingModel.getOwnerEmail().isEmpty()) {

                Resident residentOwner = new Resident();
                residentOwner.setAdult(true);
                residentOwner.setOwner(true);
                residentOwner.setName(propertyCreateBindingModel.getOwnerFullName());
                residentOwner.setPhone(propertyCreateBindingModel.getOwnerPhone());
                residentOwner.setEmail(propertyCreateBindingModel.getOwnerEmail());
                residentOwner.setProperty(property);

                newResidents.add(residentOwner);
            }
            residentsRepository.saveAll(newResidents);
        }
    }

    /**
     * Private method for calculation of number of occupants for a property.
     *
     * @param propertyCreateBindingModel input data from user.
     * @param newProperty the new property that is creating
     */
    private static void calculateNumberOfOccupants(PropertyCreateBindingModel propertyCreateBindingModel, Property newProperty) {

        if (propertyCreateBindingModel.getAdults().isEmpty() && propertyCreateBindingModel.getChildren().isEmpty() && propertyCreateBindingModel.getNumberOfPets() == 0) {
            //if there are no occupants, the property is automatically set as not occupied/habitable
            newProperty.setNotHabitable(true);
        } else {
            //defining number of occupants by type
            newProperty.setNumberOfAdults(propertyCreateBindingModel.getAdults().size());
            newProperty.setNumberOfChildren(propertyCreateBindingModel.getChildren().size());
            newProperty.setNumberOfPets(propertyCreateBindingModel.getNumberOfPets());
        }
    }

    private AdultBindingModel mapToAdultBindingModel(Resident resident) {
        AdultBindingModel adult = new AdultBindingModel();
        adult.setName(resident.getName());
        adult.setPhone(resident.getPhone());
        adult.setEmail(resident.getEmail());
        return adult;
    }

    private ChildBindingModel mapToChildBindingModel(Resident resident) {
        ChildBindingModel child = new ChildBindingModel();
        child.setName(resident.getName());
        child.setAge(resident.getAge());
        return child;
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
            existingPropertyChangeRequest.setFloor(propertyEditBindingModel.getFloor());
            existingPropertyChangeRequest.setNumberOfAdults(propertyEditBindingModel.getNumberOfAdults());
            existingPropertyChangeRequest.setNumberOfChildren(propertyEditBindingModel.getNumberOfChildren());
            existingPropertyChangeRequest.setNumberOfPets(propertyEditBindingModel.getNumberOfPets());
            existingPropertyChangeRequest.setNumberOfRooms(propertyEditBindingModel.getNumberOfRooms());
            existingPropertyChangeRequest.setParkingAvailable(propertyEditBindingModel.isParkingAvailable());
            existingPropertyChangeRequest.setNotHabitable(propertyEditBindingModel.isNotHabitable());
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
                messageService.propertyPendingRegistrationMessageToManager(property.getResidentialEntity());
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
        property.setTotalMonthlyFee(
                Objects.requireNonNullElse(property.getMonthlyFeeFundMm(), BigDecimal.ZERO)
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
