package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.*;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.PropertyFeeAddBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.PropertyFeeEditBindingModel;
import com.example.OurHome.model.enums.CityName;
import com.example.OurHome.repo.*;
import com.example.OurHome.service.PropertyFeeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PropertyFeeServiceImplTestIT {

    @Autowired
    private PropertyFeeService propertyFeeServiceToTest;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ResidentialEntityRepository residentialEntityRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private PropertyFeeRepository propertyFeeRepository;
    @Autowired
    private PropertyRepository propertyRepository;

    @BeforeEach
    void setUp() {
        propertyFeeRepository.deleteAll();
        propertyRepository.deleteAll();
        residentialEntityRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        propertyFeeRepository.deleteAll();
        propertyRepository.deleteAll();
        residentialEntityRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testCreateMonthlyFeeWithoutOverpayment() {
        Property property = createTestProperty();
        propertyRepository.save(property);

        propertyFeeServiceToTest.createPeriodicalMonthlyFee(property);

        List<PropertyFee> allProperties = propertyFeeRepository.findAll();
        Optional<PropertyFee> propertyFee = propertyFeeRepository.findById(allProperties.get(0).getId());

        assertEquals(0, propertyFee.get().getFeeAmount().compareTo(BigDecimal.valueOf(20.00)));
    }

    @Test
    void testCreateMonthlyFeeWithOverpaymentLowerThanMonthlyFee() {
        Property property = createTestProperty();
        property.setOverpayment(BigDecimal.valueOf(5));
        propertyRepository.save(property);

        propertyFeeServiceToTest.createPeriodicalMonthlyFee(property);

        List<PropertyFee> allProperties = propertyFeeRepository.findAll();
        Optional<PropertyFee> propertyFee = propertyFeeRepository.findById(allProperties.get(0).getId());

        assertEquals(0, propertyFee.get().getDueAmount().compareTo(BigDecimal.valueOf(15)));
    }

    @Test
    void testCreateMonthlyFeeWithOverpaymentEqualToMonthlyFee() {
        ResidentialEntity residentialEntity = createResidentialEntity();
        residentialEntityRepository.save(residentialEntity);

        Property property = createTestProperty();
        property.setResidentialEntity(residentialEntity);
        property.setOverpayment(BigDecimal.valueOf(20));
        propertyRepository.save(property);

        propertyFeeServiceToTest.createPeriodicalMonthlyFee(property);

        List<PropertyFee> allProperties = propertyFeeRepository.findAll();
        Optional<PropertyFee> propertyFee = propertyFeeRepository.findById(allProperties.get(0).getId());

        assertEquals(0, propertyFee.get().getDueAmount().compareTo(BigDecimal.ZERO));
    }

    @Test
    void testCreateMonthlyFeeWithOverpaymentHigherThanMonthlyFee() {
        Property property = createTestProperty();
        property.setOverpayment(BigDecimal.valueOf(15));
        propertyRepository.save(property);

        propertyFeeServiceToTest.createPeriodicalMonthlyFee(property);

        List<PropertyFee> allProperties = propertyFeeRepository.findAll();
        Optional<PropertyFee> propertyFee = propertyFeeRepository.findById(allProperties.get(0).getId());

        assertEquals(0, propertyFee.get().getDueAmount().compareTo(BigDecimal.valueOf(5)));
    }


    @Test
    void testModifyFee() {
        PropertyFee propertyFee = createTestPropertyFee();
        propertyFeeRepository.save(propertyFee);

        PropertyFeeEditBindingModel propertyFeeEditBindingModel = new PropertyFeeEditBindingModel();
        propertyFeeEditBindingModel.setFeeAmount(BigDecimal.valueOf(10));
        propertyFeeEditBindingModel.setPeriodStart(LocalDate.parse("2023-11-01"));
        propertyFeeEditBindingModel.setPeriodEnd(LocalDate.parse("2023-11-30"));
        propertyFeeEditBindingModel.setFundMmAmount(BigDecimal.TEN);
        propertyFeeEditBindingModel.setFundRepairAmount(BigDecimal.TEN);

        List<PropertyFee> allProperties = propertyFeeRepository.findAll();

        propertyFeeServiceToTest.editMonthlyFee(allProperties.get(0).getId(), propertyFeeEditBindingModel);

        Optional<PropertyFee> modifiedPropertyFee = propertyFeeRepository.findById(allProperties.get(0).getId());

        assertEquals(0, modifiedPropertyFee.get().getFeeAmount().compareTo(BigDecimal.valueOf(20.00)));
    }


    @Test
    void testDeleteFee() {
        Property testProperty = createTestProperty();
        propertyRepository.save(testProperty);

        PropertyFee propertyFee = createTestPropertyFee();
        propertyFee.setProperty(testProperty);
        PropertyFee propertyFee2 = createTestPropertyFee();
        propertyFee.setProperty(testProperty);
        propertyFeeRepository.save(propertyFee);
        propertyFeeRepository.save(propertyFee2);

        propertyFeeServiceToTest.deleteMonthlyFee(propertyFee);

        List<PropertyFee> propertyFees = propertyFeeRepository.findAll();

        assertEquals(1, propertyFees.size());
    }

    @Test
    void testAddFee() {
        ResidentialEntity residentialEntity = createResidentialEntity();
        residentialEntityRepository.save(residentialEntity);

        Property property = createTestProperty();
        property.setResidentialEntity(residentialEntity);
        propertyRepository.save(property);

        PropertyFeeAddBindingModel propertyFeeAddBindingModel = createTestPropertyFeeAddBindingModel();

        propertyFeeServiceToTest.createSingleFee(property, propertyFeeAddBindingModel);

        List<PropertyFee> allProperties = propertyFeeRepository.findAll();

        Optional<PropertyFee> addedPropertyFee = propertyFeeRepository.findById(allProperties.get(allProperties.size()-1).getId());

        assertEquals(propertyFeeAddBindingModel.getDescription(), addedPropertyFee.get().getDescription());
    }

    private PropertyFeeAddBindingModel createTestPropertyFeeAddBindingModel() {
        PropertyFeeAddBindingModel propertyFeeAddBindingModel = new PropertyFeeAddBindingModel();
        propertyFeeAddBindingModel.setFundMmAmount(BigDecimal.valueOf(10));
        propertyFeeAddBindingModel.setFundRepairAmount(BigDecimal.valueOf(10));
        propertyFeeAddBindingModel.setPeriodStart(LocalDate.parse("2023-11-01"));
        propertyFeeAddBindingModel.setPeriodEnd(LocalDate.parse("2023-11-30"));
        propertyFeeAddBindingModel.setDescription("test");
        propertyFeeAddBindingModel.setPaid(true);
        return propertyFeeAddBindingModel;
    }

    @Test
    void testChangePaymentStatusToPaid() {
        ResidentialEntity residentialEntity = createResidentialEntity();
        residentialEntityRepository.save(residentialEntity);

        Property testProperty = createTestProperty();
        testProperty.setResidentialEntity(residentialEntity);
        propertyRepository.save(testProperty);

        PropertyFee propertyFee = createTestPropertyFee();
        propertyFee.setProperty(testProperty);
        propertyFee.setPaid(false);
        propertyFeeRepository.save(propertyFee);

        propertyFeeServiceToTest.changePaymentStatus(propertyFee);

        Long id = null;
        List<PropertyFee> all = propertyFeeRepository.findAll();
        for (PropertyFee fee : all) {
            id = fee.getId();
        }

        Optional<PropertyFee> editedPropertyFee = propertyFeeRepository.findById(id);

        assertTrue(editedPropertyFee.get().isPaid());
    }

    @Test
    void testChangePaymentStatusToUnpaid() {
        ResidentialEntity residentialEntity = createResidentialEntity();
        residentialEntityRepository.save(residentialEntity);

        Property testProperty = createTestProperty();
        testProperty.setResidentialEntity(residentialEntity);
        propertyRepository.save(testProperty);

        PropertyFee propertyFee = createTestPropertyFee();
        propertyFee.setProperty(testProperty);
        propertyFee.setPaid(true);
        propertyFeeRepository.save(propertyFee);

        propertyFeeServiceToTest.changePaymentStatus(propertyFee);

        Long id = null;
        List<PropertyFee> all = propertyFeeRepository.findAll();
        for (PropertyFee fee : all) {
            id = fee.getId();
        }

        Optional<PropertyFee> editedPropertyFee = propertyFeeRepository.findById(id);

        assertFalse(editedPropertyFee.get().isPaid());
    }


    @Test
    void testMapPropertyFeeToBindingModel() {
        PropertyFee propertyFee = createTestPropertyFee();
        propertyFee.setPaid(true);
        propertyFeeRepository.save(propertyFee);

        Long id = null;
        List<PropertyFee> all = propertyFeeRepository.findAll();
        for (PropertyFee fee : all) {
            id = fee.getId();
        }

        PropertyFeeEditBindingModel propertyFeeEditBindingModel = propertyFeeServiceToTest.mapPropertyFeeToBindingModel(id);

        assertEquals(propertyFee.getPeriodStart(), propertyFeeEditBindingModel.getPeriodStart());
        assertEquals(propertyFee.getPeriodEnd(), propertyFeeEditBindingModel.getPeriodEnd());
        assertEquals(0, propertyFee.getFeeAmount().compareTo(propertyFeeEditBindingModel.getFeeAmount()));
        assertEquals(propertyFee.getDescription(), propertyFeeEditBindingModel.getDescription());
        assertEquals(propertyFee.isPaid(), propertyFeeEditBindingModel.isPaid());
    }

    private Property createTestProperty() {
        Property property = new Property();
        property.setFloor(String.valueOf(1));
        property.setNumber(1);
        property.setNumberOfAdults(2);
        property.setNumberOfChildren(2);
        property.setNumberOfPets(2);
        property.setValidated(false);
        property.setMonthlyFeeFundMm(BigDecimal.valueOf(10));
        property.setMonthlyFeeFundRepair(BigDecimal.valueOf(10));
        property.setAdditionalPropertyFee(BigDecimal.valueOf(0));
        return property;
    }

    private PropertyFee createTestPropertyFee() {
        PropertyFee propertyFee = new PropertyFee();
        propertyFee.setFeeAmount(BigDecimal.valueOf(0));
        propertyFee.setPeriodStart(LocalDate.parse("2023-12-01"));
        propertyFee.setPeriodEnd(LocalDate.parse("2023-12-07"));
        propertyFee.setDueAmount(BigDecimal.valueOf(0.00));
        propertyFee.setFundMmAmount(BigDecimal.valueOf(0.00));
        propertyFee.setFundRepairAmount(BigDecimal.valueOf(0.00));
        propertyFee.setOverpaidAmountStart(BigDecimal.valueOf(5.00));
        propertyFee.setOverpaidAmountEnd(BigDecimal.valueOf(6.00));
        return propertyFee;
    }

    private ResidentialEntity createResidentialEntity() {
        UserEntity manager = getManager();
        ResidentialEntity residentialEntity = new ResidentialEntity();
        residentialEntity.setManager(manager);
        residentialEntity.setId(100L);
        residentialEntity.setAccessCode("test");
        residentialEntity.setCity(cityRepository.findByName(CityName.valueOf("София")));
        residentialEntity.setStreetName("test");
        residentialEntity.setStreetNumber(String.valueOf(1));
        residentialEntity.setIncomesFundMm(BigDecimal.ZERO);
        residentialEntity.setIncomesFundRepair(BigDecimal.ZERO);
        residentialEntity.setIncomesTotalAmount(BigDecimal.ZERO);
        residentialEntityRepository.save(residentialEntity);
        return residentialEntity;
    }

    private UserEntity getManager() {
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
}