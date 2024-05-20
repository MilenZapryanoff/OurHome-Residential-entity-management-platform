package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.*;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyEditBindingModel;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyRegisterBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.PropertyFeeEditBindingModel;
import com.example.OurHome.model.enums.CityName;
import com.example.OurHome.repo.*;
import com.example.OurHome.service.PropertyService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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

    @BeforeEach
    void setUp() {
        propertyRepository.deleteAll();
        residentialEntityRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        propertyRepository.deleteAll();
        residentialEntityRepository.deleteAll();
        userRepository.deleteAll();
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
    void testModeratorEditProperty() {
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
        //TODO : to fix tests because of the new object propertyType
        PropertyType propertyType = new PropertyType();

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
    void testOwnerEditProperty() {
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

        //TODO : to fix tests because of the new object propertyType
        PropertyType propertyType = new PropertyType();

        propertyServiceToTest.editProperty(id, propertyEditBindingModel, propertyType);

        Optional<Property> property = propertyRepository.findById(id);

        assertNotNull(property);
        assertEquals(propertyEditBindingModel.getFloor(), property.get().getFloor());
        assertEquals(propertyEditBindingModel.getNumber(), property.get().getNumber());
        assertEquals(propertyEditBindingModel.getNumberOfAdults(), property.get().getNumberOfAdults());
        assertEquals(propertyEditBindingModel.getNumberOfChildren(), property.get().getNumberOfChildren());
        assertEquals(propertyEditBindingModel.getNumberOfPets(), property.get().getNumberOfPets());
        assertEquals(propertyEditBindingModel.isNotHabitable(), property.get().isNotHabitable());
        assertFalse(property.get().isValidated());
    }

    @Test
    void testApprovePropertyWithDataChange() {
        Property testProperty = createTestProperty();
        testProperty.setResidentialEntity(createResidentialEntity());
        propertyRepository.save(testProperty);

        Long id = null;
        List<Property> all = propertyRepository.findAll();
        for (Property property1 : all) {
            id = property1.getId();
        }

        propertyServiceToTest.approvePropertyWithDataChange(id, true);

        Optional<Property> property = propertyRepository.findById(id);

        assertTrue(property.get().isValidated());
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

        assertTrue(property.isEmpty());
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

        assertTrue(property.isEmpty());
    }

    @Test
    void testRequestToObtainProperty() {
        ResidentialEntity residentialEntity = createResidentialEntity();

        PropertyRegisterBindingModel propertyRegisterBindingModel =
                getPropertyRegisterBindingModel(residentialEntity);

        propertyServiceToTest.requestToObtainProperty(propertyRegisterBindingModel, createTestOwner());

        List<Property> allProperties = propertyRepository.findAll();
        Property property = allProperties.get(0);

        assertNotNull(property);
        assertEquals(100L, property.getResidentialEntity().getId());
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
        user.setRole(role);
        userRepository.save(user);
        return user;
    }
}