package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.*;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyCreateBindingModel;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyEditBindingModel;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyRegisterBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.PropertyFeeEditBindingModel;
import com.example.OurHome.model.enums.CityName;
import com.example.OurHome.repo.*;
import com.example.OurHome.service.PropertyRegisterRequestService;
import com.example.OurHome.service.PropertyService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class PropertyServiceImplTestIT {

    @Autowired
    private PropertyService propertyServiceToTest;

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResidentialEntityRepository residentialEntityRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PropertyRegisterRequestRepository propertyRegisterRequestRepository;

    @Autowired
    private PropertyTypeRepository propertyTypeRepository;

    @BeforeEach
    void setUp() {
        propertyRepository.deleteAll();
        residentialEntityRepository.deleteAll();
        userRepository.deleteAll();
        propertyRegisterRequestRepository.deleteAll();
        propertyTypeRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        propertyRepository.deleteAll();
        residentialEntityRepository.deleteAll();
        userRepository.deleteAll();
        propertyRegisterRequestRepository.deleteAll();
    }

    @Test
    void testMapPropertyToEditBindingModel() {
        Property property = createTestProperty();

        PropertyEditBindingModel propertyEditBindingModel = propertyServiceToTest.mapPropertyToEditBindingModel(property);

        assertEquals(property.getNumber(), propertyEditBindingModel.getNumber());
        assertEquals(property.getNumberOfAdults(), propertyEditBindingModel.getNumberOfAdults());
    }

    @Test
    void testSetOverpayment() {
        createTestProperty();

        Long id = null;
        List<Property> all = propertyRepository.findAll();
        for (Property property1 : all) {
            id = property1.getId();
        }

        PropertyFeeEditBindingModel propertyFeeEditBindingModel = new PropertyFeeEditBindingModel();
        propertyFeeEditBindingModel.setOverPayment(BigDecimal.valueOf(1000.00));
        propertyFeeEditBindingModel.setPropertyId(id);

        propertyServiceToTest.setOverpayment(propertyFeeEditBindingModel);
        Optional<Property> modifiedProperty = propertyRepository.findById(id);

        assertNotNull(modifiedProperty);
        assertEquals(0, propertyFeeEditBindingModel.getOverPayment().compareTo(modifiedProperty.get().getOverpayment()));
    }

//    @Test
//    void testSetMonthlyFee() {
//        Property property = createTestProperty();
//
//        Long id = null;
//        List<Property> all = propertyRepository.findAll();
//        for (Property property1 : all) {
//            id = property1.getId();
//        }
//
//        BigDecimal monthlyFee = BigDecimal.valueOf(Double.parseDouble("50.55"));
//
//        propertyServiceToTest.setMonthlyFee(monthlyFee, property);
//        Optional<Property> modifiedProperty = propertyRepository.findById(id);
//
//        assertNotNull(modifiedProperty);
//        assertEquals(BigDecimal.valueOf(50.55), modifiedProperty.get().getMonthlyFee());
//    }

    @Test
    void testFindAllProperties() {
        createTestProperty();
        createTestProperty();

        List<Property> allProperties = propertyServiceToTest.findAllProperties();

        assertNotNull(allProperties);
        assertEquals(2, allProperties.size());
    }

    @Test
    void testNeedOfVerificationNewNumber() {
        createTestProperty();

        PropertyEditBindingModel propertyEditBindingModel = new PropertyEditBindingModel();
        propertyEditBindingModel.setNumber(2);

        Long id = null;
        List<Property> all = propertyRepository.findAll();
        for (Property property1 : all) {
            id = property1.getId();
        }

        boolean needOfVerification = propertyServiceToTest.validationIsRequired(id, propertyEditBindingModel);

        assertTrue(needOfVerification);
    }

    @Test
    void testNeedOfVerificationNewFloor() {
        createTestProperty();

        PropertyEditBindingModel propertyEditBindingModel = new PropertyEditBindingModel();
        propertyEditBindingModel.setFloor(String.valueOf(2));

        Long id = null;
        List<Property> all = propertyRepository.findAll();
        for (Property property1 : all) {
            id = property1.getId();
        }

        boolean needOfVerification = propertyServiceToTest.validationIsRequired(id, propertyEditBindingModel);

        assertTrue(needOfVerification);
    }

    @Test
    void testNeedOfVerificationNewNumberOfAdults() {
        createTestProperty();

        PropertyEditBindingModel propertyEditBindingModel = new PropertyEditBindingModel();
        propertyEditBindingModel.setNumberOfAdults(3);

        Long id = null;
        List<Property> all = propertyRepository.findAll();
        for (Property property1 : all) {
            id = property1.getId();
        }

        boolean needOfVerification = propertyServiceToTest.validationIsRequired(id, propertyEditBindingModel);

        assertTrue(needOfVerification);
    }

    @Test
    void testNeedOfVerificationNewNumberOfChildren() {
        createTestProperty();

        PropertyEditBindingModel propertyEditBindingModel = new PropertyEditBindingModel();
        propertyEditBindingModel.setNumberOfChildren(3);

        Long id = null;
        List<Property> all = propertyRepository.findAll();
        for (Property property1 : all) {
            id = property1.getId();
        }

        boolean needOfVerification = propertyServiceToTest.validationIsRequired(id, propertyEditBindingModel);

        assertTrue(needOfVerification);
    }

    @Test
    void testNeedOfVerificationNewNumberOfPets() {
        createTestProperty();

        PropertyEditBindingModel propertyEditBindingModel = new PropertyEditBindingModel();
        propertyEditBindingModel.setNumberOfPets(3);

        Long id = null;
        List<Property> all = propertyRepository.findAll();
        for (Property property1 : all) {
            id = property1.getId();
        }

        boolean needOfVerification = propertyServiceToTest.validationIsRequired(id, propertyEditBindingModel);

        assertTrue(needOfVerification);
    }

    @Test
    void testNeedOfVerificationHabitableStatusChange() {
        createTestProperty();

        PropertyEditBindingModel propertyEditBindingModel = new PropertyEditBindingModel();
        propertyEditBindingModel.setNotHabitable(true);

        Long id = null;
        List<Property> all = propertyRepository.findAll();
        for (Property property1 : all) {
            id = property1.getId();
        }

        boolean needOfVerification = propertyServiceToTest.validationIsRequired(id, propertyEditBindingModel);

        assertTrue(needOfVerification);
    }

    @Test
    void testNeedOfVerification() {
        createTestProperty();

        PropertyEditBindingModel propertyEditBindingModel = new PropertyEditBindingModel();
        propertyEditBindingModel.setFloor(String.valueOf(1));
        propertyEditBindingModel.setNumber(1);
        propertyEditBindingModel.setNumberOfAdults(2);
        propertyEditBindingModel.setNumberOfChildren(2);
        propertyEditBindingModel.setNumberOfPets(2);
        propertyEditBindingModel.setNotHabitable(false);

        Long id = null;
        List<Property> all = propertyRepository.findAll();
        for (Property property1 : all) {
            id = property1.getId();
        }

        boolean needOfVerification = propertyServiceToTest.validationIsRequired(id, propertyEditBindingModel);

        assertFalse(needOfVerification);
    }

    @Test
    void testModeratorEditPropertyIfPropertyNotHabitable() {
        PropertyType propertyType = createTestPropertyType();
        propertyTypeRepository.save(propertyType);

        Property testProperty = createTestProperty();
        testProperty.setPropertyType(propertyType);
        testProperty.setResidentialEntity(createResidentialEntity());

        propertyRepository.save(testProperty);

        PropertyEditBindingModel propertyEditBindingModel = new PropertyEditBindingModel();
        propertyEditBindingModel.setFloor(String.valueOf(10));
        propertyEditBindingModel.setNumber(10);
        propertyEditBindingModel.setNumberOfAdults(20);
        propertyEditBindingModel.setNumberOfChildren(20);
        propertyEditBindingModel.setNumberOfPets(20);
        propertyEditBindingModel.setNotHabitable(true);

        Long id = null;
        List<Property> all = propertyRepository.findAll();
        for (Property property1 : all) {
            id = property1.getId();
        }

        propertyServiceToTest.editProperty(id, propertyEditBindingModel, propertyType);
        Optional<Property> property = propertyRepository.findById(id);

        assertNotNull(property);
        assertEquals(propertyEditBindingModel.getFloor(), property.get().getFloor());
        assertEquals(propertyEditBindingModel.getNumber(), property.get().getNumber());
        assertEquals(property.get().getNumberOfChildren(),0);
        assertEquals(property.get().getNumberOfPets(),0);
        assertEquals(property.get().getNumberOfAdults(),0);
        assertTrue(property.get().isValidated());
    }

    @Test
    void testModeratorEditPropertyIfPropertyIsHabitable() {
        PropertyType propertyType = createTestPropertyType();
        propertyTypeRepository.save(propertyType);

        Property testProperty = createTestProperty();
        testProperty.setPropertyType(propertyType);
        testProperty.setValidated(false);
        testProperty.setResidentialEntity(createResidentialEntity());

        propertyRepository.save(testProperty);

        PropertyEditBindingModel propertyEditBindingModel = new PropertyEditBindingModel();
        propertyEditBindingModel.setFloor(String.valueOf(10));
        propertyEditBindingModel.setNumber(10);
        propertyEditBindingModel.setNumberOfAdults(20);
        propertyEditBindingModel.setNumberOfChildren(20);
        propertyEditBindingModel.setNumberOfPets(20);
        propertyEditBindingModel.setNotHabitable(false);

        List<Property> allProperties = propertyRepository.findAll();

        propertyServiceToTest.editProperty(allProperties.getFirst().getId(), propertyEditBindingModel, propertyType);
        Optional<Property> property = propertyRepository.findById(allProperties.getFirst().getId());

        assertNotNull(property);
        assertEquals(propertyEditBindingModel.getFloor(), property.get().getFloor());
        assertEquals(propertyEditBindingModel.getNumber(), property.get().getNumber());
        assertEquals(propertyEditBindingModel.getNumberOfChildren(), property.get().getNumberOfChildren());
        assertEquals(propertyEditBindingModel.getNumberOfPets(), property.get().getNumberOfPets());
        assertEquals(propertyEditBindingModel.getNumberOfAdults(),property.get().getNumberOfAdults());
        assertTrue(property.get().isValidated());
    }

    private PropertyType createTestPropertyType() {
        PropertyType propertyType = new PropertyType();

        propertyType.setFundRepairNotHabitable(BigDecimal.valueOf(8.0));
        propertyType.setFundRepairHabitable(BigDecimal.valueOf(8.0));
        propertyType.setCommonPartsPercentage(BigDecimal.valueOf(8.0));
        propertyType.setTotalFlatSpace(BigDecimal.valueOf(8.0));
        propertyType.setDescription("test property type");
        return propertyType;
    }

    @Test
    void testEditHabitableProperty() {
        Property testProperty = createTestProperty();
        testProperty.setResidentialEntity(createResidentialEntity());

        propertyRepository.save(testProperty);

        PropertyEditBindingModel propertyEditBindingModel = new PropertyEditBindingModel();
        propertyEditBindingModel.setFloor(String.valueOf(10));
        propertyEditBindingModel.setNumber(10);
        propertyEditBindingModel.setNumberOfAdults(20);
        propertyEditBindingModel.setNumberOfChildren(20);
        propertyEditBindingModel.setNumberOfPets(20);
        propertyEditBindingModel.setNotHabitable(false);

        Long id = null;
        List<Property> all = propertyRepository.findAll();
        for (Property property1 : all) {
            id = property1.getId();
        }

        PropertyType propertyType = new PropertyType();
        propertyType.setFundRepairHabitable(BigDecimal.TEN);
        propertyType.setFundRepairNotHabitable(BigDecimal.TEN);
        propertyTypeRepository.save(propertyType);

        propertyServiceToTest.editProperty(id, propertyEditBindingModel, propertyType);

        Optional<Property> property = propertyRepository.findById(id);

        assertNotNull(property);
        assertEquals(propertyEditBindingModel.getFloor(), property.get().getFloor());
        assertEquals(propertyEditBindingModel.getNumber(), property.get().getNumber());
        assertEquals(propertyEditBindingModel.getNumberOfAdults(), property.get().getNumberOfAdults());
        assertEquals(propertyEditBindingModel.getNumberOfChildren(), property.get().getNumberOfChildren());
        assertEquals(propertyEditBindingModel.getNumberOfPets(), property.get().getNumberOfPets());
        assertEquals(propertyEditBindingModel.isNotHabitable(), property.get().isNotHabitable());
        assertTrue(property.get().isValidated());
    }

    @Test
    void testEditNonHabitableProperty() {
        Property testProperty = createTestProperty();
        testProperty.setResidentialEntity(createResidentialEntity());

        propertyRepository.save(testProperty);

        PropertyEditBindingModel propertyEditBindingModel = new PropertyEditBindingModel();
        propertyEditBindingModel.setFloor(String.valueOf(10));
        propertyEditBindingModel.setNumber(10);
        propertyEditBindingModel.setNumberOfAdults(20);
        propertyEditBindingModel.setNumberOfChildren(20);
        propertyEditBindingModel.setNumberOfPets(20);
        propertyEditBindingModel.setNotHabitable(true);

        Long id = null;
        List<Property> all = propertyRepository.findAll();
        for (Property property1 : all) {
            id = property1.getId();
        }

        PropertyType propertyType = new PropertyType();
        propertyType.setFundRepairHabitable(BigDecimal.TEN);
        propertyType.setFundRepairNotHabitable(BigDecimal.TEN);
        propertyTypeRepository.save(propertyType);

        propertyServiceToTest.editProperty(id, propertyEditBindingModel, propertyType);

        Optional<Property> property = propertyRepository.findById(id);

        assertNotNull(property);
        assertEquals(propertyEditBindingModel.getFloor(), property.get().getFloor());
        assertEquals(propertyEditBindingModel.getNumber(), property.get().getNumber());
        assertEquals(0, property.get().getNumberOfAdults());
        assertEquals(0, property.get().getNumberOfChildren());
        assertEquals(0, property.get().getNumberOfPets());
        assertEquals(propertyEditBindingModel.isNotHabitable(), property.get().isNotHabitable());
        assertTrue(property.get().isValidated());
    }

    @Test
    void testCreateSingleProperty() {

        PropertyCreateBindingModel propertyCreateBindingModel = creteTestPropertyCreateBindingModel();
        ResidentialEntity residentialEntity = createResidentialEntity();
        residentialEntityRepository.save(residentialEntity);

        propertyServiceToTest.createSingleProperty(propertyCreateBindingModel, residentialEntity, null);

        List<Property> allProperties = propertyRepository.findAll();
        Property newProperty = allProperties.getFirst();

        assertEquals(propertyCreateBindingModel.getFloor(), newProperty.getFloor());
        assertEquals(propertyCreateBindingModel.getNumber(), newProperty.getNumber());
        assertEquals(propertyCreateBindingModel.getNumberOfAdults(), newProperty.getNumberOfAdults());
        assertEquals(propertyCreateBindingModel.getNumberOfChildren(), newProperty.getNumberOfChildren());
        assertEquals(propertyCreateBindingModel.getNumberOfPets(), newProperty.getNumberOfPets());
        assertEquals(propertyCreateBindingModel.isNotHabitable(), newProperty.isNotHabitable());

    }

    private PropertyCreateBindingModel creteTestPropertyCreateBindingModel() {
        PropertyCreateBindingModel propertyCreateBindingModel = new PropertyCreateBindingModel();
        propertyCreateBindingModel.setFloor("89");
        propertyCreateBindingModel.setNumber(89);
        propertyCreateBindingModel.setNumberOfAdults(22);
        propertyCreateBindingModel.setNumberOfChildren(22);
        propertyCreateBindingModel.setNumberOfPets(22);
        propertyCreateBindingModel.setNotHabitable(true);
        return  propertyCreateBindingModel;
    }

    @Test
    void testApprovePropertyRegistrationWithDataChange() {

        ResidentialEntity residentialEntity = createResidentialEntity();
        residentialEntityRepository.save(residentialEntity);

        PropertyRegisterRequest testPropertyRegisterRequest = createTestpropertyRegisterRequest();
        testPropertyRegisterRequest.setResidentialEntityId(100L);
        propertyRegisterRequestRepository.save(testPropertyRegisterRequest);

        Property testProperty = createTestProperty();
        testProperty.setResidentialEntity(residentialEntity);
        testProperty.setPropertyRegisterRequest(testPropertyRegisterRequest);
        propertyRepository.save(testProperty);

        UserEntity testOwner = createTestOwner();
        userRepository.save(testOwner);

        Long id = null;
        List<Property> allProperties = propertyRepository.findAll();
        Property resultProperty = allProperties.getFirst();

        propertyServiceToTest.approvePropertyRegistrationWithDataChange(resultProperty.getId(), true);

        Optional<Property> property = propertyRepository.findById(resultProperty.getId());
        List<PropertyRegisterRequest> allPropertyRegisterRequests = propertyRegisterRequestRepository.findAll();
        PropertyRegisterRequest resultPropertyRegisterRequest = allPropertyRegisterRequests.getFirst();

        assertEquals(property.get().getNumberOfAdults(), testPropertyRegisterRequest.getNumberOfAdults());
        assertEquals(property.get().getNumberOfChildren(), testPropertyRegisterRequest.getNumberOfChildren());
        assertEquals(property.get().getNumberOfPets(), testPropertyRegisterRequest.getNumberOfPets());
        assertTrue(property.get().isObtained());
        assertNull(property.get().getPropertyRegisterRequest());
        assertFalse(resultPropertyRegisterRequest.isActive());
    }

    @Test
    void testRejectProperty() {
        Property testProperty = createTestProperty();
        testProperty.setRejected(false);
        testProperty.setResidentialEntity(createResidentialEntity());
        propertyRepository.save(testProperty);

        Long id = null;
        List<Property> all = propertyRepository.findAll();
        for (Property property1 : all) {
            id = property1.getId();
        }

        propertyServiceToTest.rejectProperty(id);

        Optional<Property> property = propertyRepository.findById(id);

        assertTrue(property.get().isRejected());
    }


    @Test
    void testUnlinkOwnerByManager() {
        Property testProperty = createTestProperty();
        testProperty.setRejected(false);
        testProperty.setResidentialEntity(createResidentialEntity());
        propertyRepository.save(testProperty);

        Long id = null;
        List<Property> all = propertyRepository.findAll();
        for (Property property1 : all) {
            id = property1.getId();
        }

        propertyServiceToTest.unlinkOwner(id, true);

        Optional<Property> property = propertyRepository.findById(id);

        assertFalse(property.get().isObtained());
    }

    @Test
    void testUnlinkOwnerByOwner() {
        Property testProperty = createTestProperty();
        testProperty.setRejected(false);
        testProperty.setResidentialEntity(createResidentialEntity());
        propertyRepository.save(testProperty);

        Long id = null;
        List<Property> all = propertyRepository.findAll();
        for (Property property1 : all) {
            id = property1.getId();
        }

        propertyServiceToTest.unlinkOwner(id, false);

        Optional<Property> property = propertyRepository.findById(id);

        assertFalse(property.get().isObtained());
    }

    @Test
    void testRequestToObtainFreeProperty() {
        ResidentialEntity residentialEntity = createResidentialEntity();
        residentialEntityRepository.save(residentialEntity);

        Property testProperty = createTestProperty();
        testProperty.setNumber(10);
        testProperty.setResidentialEntity(residentialEntity);
        propertyRepository.save(testProperty);

        PropertyRegisterBindingModel propertyRegisterBindingModel = getPropertyRegisterBindingModel(residentialEntity);

        UserEntity testOwner = createTestOwner();
        userRepository.save(testOwner);

        propertyServiceToTest.requestToObtainProperty(propertyRegisterBindingModel, testOwner);

        List<Property> allProperties = propertyRepository.findAll();
        Property property = allProperties.getFirst();

        //PropertyRequest is created
        List<PropertyRegisterRequest> allPropertyRegisterRequest = propertyRegisterRequestRepository.findAll();
        PropertyRegisterRequest propertyRegisterRequest = allPropertyRegisterRequest.getFirst();

        //comparing propertyRegisterBindingModel with PropertyRequest
        assertEquals(propertyRegisterBindingModel.getNumberOfAdults(), propertyRegisterRequest.getNumberOfAdults());
        assertEquals(propertyRegisterBindingModel.getNumberOfChildren(), propertyRegisterRequest.getNumberOfChildren());
        assertEquals(propertyRegisterBindingModel.getNumberOfPets(), propertyRegisterRequest.getNumberOfPets());
        assertEquals(propertyRegisterBindingModel.getFloor(), propertyRegisterRequest.getFloor());
        assertEquals(propertyRegisterBindingModel.isNotHabitable(), propertyRegisterRequest.isNotHabitable());

        assertEquals(testOwner.getId(), propertyRegisterRequest.getOwner().getId());
        assertEquals(property.getOwner().getId(), testOwner.getId());
        assertTrue(propertyRegisterRequest.isActive());
        assertEquals(100L, property.getResidentialEntity().getId());
    }

    @Test
    void testRequestToObtainNonFreeProperty() {
        ResidentialEntity residentialEntity = createResidentialEntity();
        residentialEntityRepository.save(residentialEntity);

        PropertyRegisterRequest testPropertyRegisterRequest = createTestpropertyRegisterRequest();
        testPropertyRegisterRequest.setResidentialEntityId(1L);
        propertyRegisterRequestRepository.save(testPropertyRegisterRequest);

        Property testProperty = createTestProperty();
        testProperty.setNumber(10);
        testProperty.setResidentialEntity(residentialEntity);
        testProperty.setPropertyRegisterRequest(testPropertyRegisterRequest);
        propertyRepository.save(testProperty);

        PropertyRegisterBindingModel propertyRegisterBindingModel = getPropertyRegisterBindingModel(residentialEntity);

        UserEntity testOwner = createTestOwner();
        userRepository.save(testOwner);

        propertyServiceToTest.requestToObtainProperty(propertyRegisterBindingModel, testOwner);

        List<Property> allProperties = propertyRepository.findAll();
        Property property = allProperties.getFirst();

        //PropertyRequest is created
        List<PropertyRegisterRequest> allPropertyRegisterRequest = propertyRegisterRequestRepository.findAll();
        PropertyRegisterRequest resultPropertyRegisterRequest = allPropertyRegisterRequest.getFirst();

        //comparing propertyRegisterBindingModel with PropertyRequest
        assertNotEquals(propertyRegisterBindingModel.getNumberOfAdults(), testPropertyRegisterRequest.getNumberOfAdults());
        assertNotEquals(propertyRegisterBindingModel.getNumberOfChildren(), testPropertyRegisterRequest.getNumberOfChildren());
        assertNotEquals(propertyRegisterBindingModel.getNumberOfPets(), testPropertyRegisterRequest.getNumberOfPets());
        assertNotEquals(propertyRegisterBindingModel.getFloor(), testPropertyRegisterRequest.getFloor());
        assertNotEquals(propertyRegisterBindingModel.isNotHabitable(), testPropertyRegisterRequest.isNotHabitable());

    }



    private static PropertyRegisterBindingModel getPropertyRegisterBindingModel(ResidentialEntity residentialEntity) {
        PropertyRegisterBindingModel propertyRegisterBindingModel = new PropertyRegisterBindingModel();
        propertyRegisterBindingModel.setResidentialEntity(residentialEntity.getId());
        propertyRegisterBindingModel.setNumber(10);
        propertyRegisterBindingModel.setFloor(String.valueOf(10));
        propertyRegisterBindingModel.setNumberOfAdults(10);
        propertyRegisterBindingModel.setNumberOfChildren(10);
        propertyRegisterBindingModel.setNumberOfPets(10);
        propertyRegisterBindingModel.setNotHabitable(true);
        return propertyRegisterBindingModel;
    }

    private Property createTestProperty() {
        Property property = new Property();
        property.setFloor(String.valueOf(1));
        property.setNumber(1);
        property.setNumberOfAdults(2);
        property.setNumberOfChildren(2);
        property.setNumberOfPets(2);
        property.setValidated(false);
        property.setAdditionalPropertyFee(BigDecimal.ZERO);
        propertyRepository.save(property);
        return property;
    }

    private ResidentialEntity createResidentialEntity() {
        ResidentialEntity residentialEntity = new ResidentialEntity();
        residentialEntity.setManager(createTestManager());
        residentialEntity.setId(100L);
        residentialEntity.setAccessCode("test");
        residentialEntity.setCity(cityRepository.findByName(CityName.valueOf("София")));
        residentialEntity.setStreetName("test");
        residentialEntity.setStreetNumber(String.valueOf(1));
        residentialEntity.setFee(createTestFee());
        residentialEntity.setIncomesTotalAmount(BigDecimal.valueOf(0.00));
        residentialEntity.setIncomesFundMm(BigDecimal.valueOf(0.00));
        residentialEntity.setIncomesFundRepair(BigDecimal.valueOf(0.00));
        residentialEntityRepository.save(residentialEntity);
        return residentialEntity;
    }

    private UserEntity createTestManager() {
        UserEntity manager = new UserEntity();
        Role role = roleRepository.findRoleByName("MANAGER");
        roleRepository.save(role);
        manager.setEmail("test@test.test");
        manager.setFirstName("Test");
        manager.setLastName("Test");
        manager.setUsername("testerManager");
        manager.setPassword("testPassword");
        manager.setPhoneNumber("0777777777");
        manager.setRegistrationDateTime(LocalDateTime.now());
        manager.setRole(role);
        userRepository.save(manager);
        return manager;
    }

    private Fee createTestFee() {
        Fee fee = new Fee();
        fee.setAdultFee(BigDecimal.valueOf(0));
        fee.setChildFee(BigDecimal.valueOf(0));
        fee.setPetFee(BigDecimal.valueOf(0));
        fee.setAdditionalFeeHabitable(BigDecimal.valueOf(0));
        fee.setFixedFeeHabitable(BigDecimal.valueOf(0));
        fee.setFixedFeeNonHabitable(BigDecimal.valueOf(0));
        fee.setAdditionalFeeNonHabitable(BigDecimal.valueOf(0));
        fee.setFundRepairHabitable(BigDecimal.valueOf(0));
        fee.setFundRepairNonHabitable(BigDecimal.valueOf(0));
        return fee;
    }

    private UserEntity createTestOwner() {
        Role role = roleRepository.findRoleByName("RESIDENT");
        roleRepository.save(role);

        UserEntity user = new UserEntity();
        user.setEmail("test@mail.bg");
        user.setFirstName("Test");
        user.setLastName("Test");
        user.setUsername("tester");
        user.setPassword("testPassword");
        user.setPhoneNumber("0666666666");
        user.setRegistrationDateTime(LocalDateTime.now());
        user.setRole(role);
        userRepository.save(user);
        return user;
    }

    private PropertyRegisterRequest createTestpropertyRegisterRequest() {
        PropertyRegisterRequest propertyRegisterRequest = new PropertyRegisterRequest();
        propertyRegisterRequest.setActive(true);
        propertyRegisterRequest.setCreationDateTime(LocalDateTime.now());
        propertyRegisterRequest.setLastModificationDateTime(LocalDateTime.now());
        propertyRegisterRequest.setNumberOfAdults(9);
        propertyRegisterRequest.setNumberOfChildren(9);
        propertyRegisterRequest.setNumberOfPets(9);
        propertyRegisterRequest.setNotHabitable(false);
        return propertyRegisterRequest;
    }
}