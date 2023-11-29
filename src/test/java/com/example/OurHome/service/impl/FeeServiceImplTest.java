package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.Fee;
import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.dto.BindingModels.Fee.FeeEditBindingModel;
import com.example.OurHome.repo.FeeRepository;
import com.example.OurHome.repo.PropertyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FeeServiceImplTest {

    @Mock
    private FeeRepository feeRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private FeeServiceImpl feeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindById() {
        // Mocking data
        Long feeId = 1L;
        Fee mockFee = new Fee();
        when(feeRepository.findById(feeId)).thenReturn(Optional.of(mockFee));

        // Testing findById method
        Fee foundFee = feeService.findById(feeId);

        assertEquals(mockFee, foundFee);
        verify(feeRepository, times(1)).findById(feeId);
    }

    @Test
    void testCreateFee() {
        ResidentialEntity residentialEntity = new ResidentialEntity();
        Fee createdFee = feeService.createFee(residentialEntity);

        assertEquals(BigDecimal.ZERO, createdFee.getFixedFeeHabitable());
        assertEquals(BigDecimal.ZERO, createdFee.getAdultFee());
        // Add assertions for other properties of Fee

        verify(feeRepository, times(1)).save(any(Fee.class));
    }

    @Test
    void testCalculateMonthlyFee() {
        ResidentialEntity residentialEntity = new ResidentialEntity();
        Property property = new Property();
        // Set up residentialEntity and property objects

        // Mocking behavior
        Fee mockFee = new Fee();
        mockFee.setFixedFeeHabitable(BigDecimal.valueOf(100));
        mockFee.setAdultFee(BigDecimal.valueOf(10));
        // Set other properties of mockFee
        when(residentialEntity.getFee()).thenReturn(mockFee);
        when(property.isNotHabitable()).thenReturn(false); // Modify this based on your scenario

        // Testing calculateMonthlyFee method
        BigDecimal calculatedFee = feeService.calculateMonthlyFee(residentialEntity, property);

        assertEquals(BigDecimal.valueOf(100), calculatedFee); // Modify this based on your calculation logic

        verify(property, times(1)).getNumberOfAdults();
        // ...
    }

    @Test
    void testMapFeeToBindingModel() {
        Fee mockFee = new Fee();
        FeeEditBindingModel mockBindingModel = new FeeEditBindingModel();
        when(modelMapper.map(mockFee, FeeEditBindingModel.class)).thenReturn(mockBindingModel);

        FeeEditBindingModel result = feeService.mapFeeToBindingModel(mockFee);

        assertEquals(mockBindingModel, result);
        verify(modelMapper, times(1)).map(mockFee, FeeEditBindingModel.class);
    }

    @Test
    void testChangeFee() {
        ResidentialEntity residentialEntity = new ResidentialEntity();
        FeeEditBindingModel feeEditBindingModel = new FeeEditBindingModel();
        Fee mockFee = new Fee();
        when(residentialEntity.getFee()).thenReturn(mockFee);

        // Testing changeFee method
        feeService.changeFee(residentialEntity, feeEditBindingModel);

        verify(feeRepository, times(1)).save(mockFee);
        verify(propertyRepository, atLeastOnce()).save(any(Property.class)); // Modify this based on your scenario
    }

}