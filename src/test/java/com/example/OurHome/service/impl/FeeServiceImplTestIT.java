package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.Fee;
import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.Fee.FeeEditBindingModel;
import com.example.OurHome.model.dto.BindingModels.User.UserRegisterBindingModel;
import com.example.OurHome.repo.FeeRepository;
import com.example.OurHome.repo.ResidentialEntityRepository;
import com.example.OurHome.repo.UserRepository;
import com.example.OurHome.service.FeeService;
import com.example.OurHome.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FeeServiceImplTestIT {

    @Autowired
    private FeeService feeServiceToTest;

    @Autowired
    private FeeRepository feeRepository;

    @Autowired
    private ResidentialEntityRepository residentialEntityRepository;

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
}