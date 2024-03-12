package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.PropertyFee;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.PropertyFeeAddBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyFee.PropertyFeeEditBindingModel;
import com.example.OurHome.repo.PropertyFeeRepository;
import com.example.OurHome.repo.PropertyRepository;
import com.example.OurHome.service.PropertyFeeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PropertyFeeServiceImplTestIT {

    @Autowired
    private PropertyFeeService propertyFeeServiceToTest;

    @Autowired
    private PropertyFeeRepository propertyFeeRepository;

    @Autowired
    private PropertyRepository propertyRepository;

    @BeforeEach
    void setUp() {
        propertyFeeRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        propertyFeeRepository.deleteAll();
    }

    @Test
    void testCreateMonthlyFeeWithoutOverpayment() {
        Property property = createTestProperty();
        propertyRepository.save(property);

        propertyFeeServiceToTest.createPeriodicalMonthlyFee(property);

        Long id = null;
        List<PropertyFee> all = propertyFeeRepository.findAll();
        for (PropertyFee propertyFee : all) {
            id = propertyFee.getId();
        }

        Optional<PropertyFee> propertyFee = propertyFeeRepository.findById(id);

        assertEquals(0, propertyFee.get().getFeeAmount().compareTo(BigDecimal.TEN));
    }

    @Test
    void testCreateMonthlyFeeWithOverpaymentLowerThanMonthlyFee() {
        Property property = createTestProperty();
        property.setOverpayment(BigDecimal.valueOf(5));
        propertyRepository.save(property);

        propertyFeeServiceToTest.createPeriodicalMonthlyFee(property);

        Long id = null;
        List<PropertyFee> all = propertyFeeRepository.findAll();
        for (PropertyFee propertyFee : all) {
            id = propertyFee.getId();
        }

        Optional<PropertyFee> propertyFee = propertyFeeRepository.findById(id);

        assertEquals(0, propertyFee.get().getFeeAmount().compareTo(BigDecimal.valueOf(5)));
    }

    @Test
    void testCreateMonthlyFeeWithOverpaymentEqualToMonthlyFee() {
        Property property = createTestProperty();
        property.setOverpayment(BigDecimal.valueOf(10));
        propertyRepository.save(property);

        propertyFeeServiceToTest.createPeriodicalMonthlyFee(property);

        Long id = null;
        List<PropertyFee> all = propertyFeeRepository.findAll();
        for (PropertyFee propertyFee : all) {
            id = propertyFee.getId();
        }

        Optional<PropertyFee> propertyFee = propertyFeeRepository.findById(id);

        assertEquals(0, propertyFee.get().getFeeAmount().compareTo(BigDecimal.TEN));
    }

    @Test
    void testCreateMonthlyFeeWithOverpaymentHigherThanMonthlyFee() {
        Property property = createTestProperty();
        property.setOverpayment(BigDecimal.valueOf(15));
        propertyRepository.save(property);

        propertyFeeServiceToTest.createPeriodicalMonthlyFee(property);

        Long id = null;
        List<PropertyFee> all = propertyFeeRepository.findAll();
        for (PropertyFee propertyFee : all) {
            id = propertyFee.getId();
        }

        Optional<PropertyFee> propertyFee = propertyFeeRepository.findById(id);

        assertEquals(0, propertyFee.get().getFeeAmount().compareTo(BigDecimal.TEN));
    }


    @Test
    void testModifyFee() {
        PropertyFee propertyFee = createTestPropertyFee();
        propertyFeeRepository.save(propertyFee);

        PropertyFeeEditBindingModel propertyFeeEditBindingModel = new PropertyFeeEditBindingModel();
        propertyFeeEditBindingModel.setFeeAmount(BigDecimal.valueOf(10));
        propertyFeeEditBindingModel.setPeriodStart(LocalDate.parse("2023-11-01"));
        propertyFeeEditBindingModel.setPeriodEnd(LocalDate.parse("2023-11-30"));

        Long id = null;
        List<PropertyFee> all = propertyFeeRepository.findAll();
        for (PropertyFee fee : all) {
            id = fee.getId();
        }

        propertyFeeServiceToTest.editMonthlyFee(id, propertyFeeEditBindingModel);

        Optional<PropertyFee> modifiedPropertyFee = propertyFeeRepository.findById(id);

        assertEquals(0, modifiedPropertyFee.get().getFeeAmount().compareTo(BigDecimal.TEN));
    }


    @Test
    void testDeleteFee() {
        PropertyFee propertyFee = createTestPropertyFee();
        PropertyFee propertyFee2 = createTestPropertyFee();
        propertyFeeRepository.save(propertyFee);
        propertyFeeRepository.save(propertyFee2);

        propertyFeeServiceToTest.deleteMonthlyFee(propertyFee);

        List<PropertyFee> propertyFees = propertyFeeRepository.findAll();

        assertEquals(1, propertyFees.size());
    }

    @Test
    void testAddFee() {
        Property property = createTestProperty();
        propertyRepository.save(property);

        PropertyFeeAddBindingModel propertyFeeAddBindingModel = new PropertyFeeAddBindingModel();
        propertyFeeAddBindingModel.setFeeAmount(BigDecimal.valueOf(10));
        propertyFeeAddBindingModel.setPeriodStart(LocalDate.parse("2023-11-01"));
        propertyFeeAddBindingModel.setPeriodEnd(LocalDate.parse("2023-11-30"));
        propertyFeeAddBindingModel.setDescription("test");
        propertyFeeAddBindingModel.setPaid(true);

        propertyFeeServiceToTest.createSingleFee(property, propertyFeeAddBindingModel);

        Long id = null;
        List<PropertyFee> all = propertyFeeRepository.findAll();
        for (PropertyFee fee : all) {
            id = fee.getId();
        }

        Optional<PropertyFee> addedPropertyFee = propertyFeeRepository.findById(id);

        assertEquals(propertyFeeAddBindingModel.getDescription(), addedPropertyFee.get().getDescription());
    }

    @Test
    void testChangePaymentStatusToPaid() {
        PropertyFee propertyFee = createTestPropertyFee();
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
        PropertyFee propertyFee = createTestPropertyFee();
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
        property.setNumber(String.valueOf(1));
        property.setNumberOfAdults(2);
        property.setNumberOfChildren(2);
        property.setNumberOfPets(2);
        property.setValidated(false);
        property.setMonthlyFee(BigDecimal.valueOf(10));
        property.setAdditionalPropertyFee(BigDecimal.valueOf(0));
        return property;
    }

    private PropertyFee createTestPropertyFee() {
        PropertyFee propertyFee = new PropertyFee();
        propertyFee.setFeeAmount(BigDecimal.valueOf(0));
        propertyFee.setPeriodStart(LocalDate.parse("2023-12-01"));
        propertyFee.setPeriodEnd(LocalDate.parse("2023-12-07"));
        return propertyFee;
    }
}