package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.*;
import com.example.OurHome.model.dto.BindingModels.Fee.FeeEditBindingModel;
import com.example.OurHome.model.enums.CityName;
import com.example.OurHome.repo.*;
import com.example.OurHome.service.FeeService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FeeServiceImplTestIT {

    @Autowired
    private FeeService feeServiceToTest;

    @Autowired
    private FeeRepository feeRepository;

    @Autowired
    private CityRepository cityRepository;

    @BeforeEach
    void setUp() {
        feeRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        feeRepository.deleteAll();
    }

    @Test
    void testFindById() {
        Fee fee = new Fee();
        Fee savedFee = feeRepository.save(fee);

        Fee foundFee = feeServiceToTest.findById(savedFee.getId());

        assertNotNull(foundFee);
        assertEquals(savedFee.getId(), foundFee.getId());
    }

    @Test
    void testCreateFee() {
        ResidentialEntity residentialEntity = new ResidentialEntity(); // Create a mock ResidentialEntity if needed
        Fee createdFee = feeServiceToTest.createFee(residentialEntity);

        assertNotNull(createdFee);

        assertEquals(BigDecimal.valueOf(0.00), createdFee.getFixedFeeHabitable());
        assertEquals(BigDecimal.valueOf(0.0), createdFee.getAdultFee());
        assertEquals(BigDecimal.valueOf(0.0), createdFee.getChildFee());
        assertEquals(BigDecimal.valueOf(0.0), createdFee.getPetFee());
        assertEquals(BigDecimal.valueOf(0.0), createdFee.getAdditionalFeeHabitable());
        assertEquals(BigDecimal.valueOf(0.0), createdFee.getFixedFeeNonHabitable());
        assertEquals(BigDecimal.valueOf(0.0), createdFee.getAdditionalFeeNonHabitable());
    }

    @Test
    void testCalculateMonthlyFee() {
        Property property = createTestData();

        BigDecimal monthlyFee = feeServiceToTest.calculateMonthlyFee(property.getResidentialEntity(), property);

        assertEquals(BigDecimal.valueOf(50),monthlyFee);
    }

    @Test
    void testChangeFee() {
        Property property = createTestData();

        FeeEditBindingModel feeEditBindingModel = new FeeEditBindingModel();
        feeEditBindingModel.setFixedFeeHabitable(BigDecimal.valueOf(20));
        feeEditBindingModel.setFixedFeeNonHabitable(BigDecimal.valueOf(20));
        feeEditBindingModel.setAdditionalFeeHabitable(BigDecimal.valueOf(20));
        feeEditBindingModel.setAdditionalFeeNonHabitable(BigDecimal.valueOf(20));
        feeEditBindingModel.setAdultFee(BigDecimal.valueOf(20));
        feeEditBindingModel.setChildFee(BigDecimal.valueOf(20));
        feeEditBindingModel.setPetFee(BigDecimal.valueOf(20));

        feeServiceToTest.changeFee(property.getResidentialEntity(), feeEditBindingModel);

        List<Fee> allFees = feeRepository.findAll();
        Fee fee = allFees.get(0);

        assertEquals(0,BigDecimal.valueOf(20).compareTo(fee.getAdultFee()));
        assertEquals(0,BigDecimal.valueOf(20).compareTo(fee.getChildFee()));
        assertEquals(0,BigDecimal.valueOf(20).compareTo(fee.getPetFee()));
        assertEquals(0,BigDecimal.valueOf(20).compareTo(fee.getFixedFeeHabitable()));
        assertEquals(0,BigDecimal.valueOf(20).compareTo(fee.getFixedFeeNonHabitable()));
        assertEquals(0,BigDecimal.valueOf(20).compareTo(fee.getAdditionalFeeHabitable()));
        assertEquals(0,BigDecimal.valueOf(20).compareTo(fee.getAdditionalFeeNonHabitable()));
    }

    private Property createTestData() {

        ResidentialEntity residentialEntity = new ResidentialEntity();
        residentialEntity.setId(100L);
        residentialEntity.setAccessCode("test");
        residentialEntity.setCity(cityRepository.findByName(CityName.valueOf("София")));
        residentialEntity.setFee(createTestFee());

        Property property = new Property();
        property.setNumber(String.valueOf(1));
        property.setFloor(String.valueOf(1));
        property.setNumberOfAdults(1);
        property.setNumberOfChildren(1);
        property.setNumberOfPets(1);
        property.setResidentialEntity(residentialEntity);
        property.setNotHabitable(false);

        return property;
    }

    private Fee createTestFee() {
        Fee fee = new Fee();
        fee.setFixedFeeHabitable(BigDecimal.valueOf(10));
        fee.setFixedFeeNonHabitable(BigDecimal.valueOf(10));
        fee.setAdditionalFeeHabitable(BigDecimal.valueOf(10));
        fee.setAdditionalFeeNonHabitable(BigDecimal.valueOf(10));
        fee.setAdultFee(BigDecimal.valueOf(10));
        fee.setChildFee(BigDecimal.valueOf(10));
        fee.setPetFee(BigDecimal.valueOf(10));
        feeRepository.save(fee);
        return fee;
    }
}