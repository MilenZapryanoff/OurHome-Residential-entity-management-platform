package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.Expense;
import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.repo.ExpensesRepository;
import com.example.OurHome.repo.PropertyRepository;
import com.example.OurHome.repo.ResidentialEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PropertyServiceImplTest {

    private PropertyServiceImpl serviceToTest;
    @Mock
    private PropertyRepository mockPropertyRepository;
    @Mock
    private ModelMapper mockModelMapper;
    @Mock
    private ResidentialEntityServiceImpl mockResidentialEntityService;
    @Mock
    private FeeServiceImpl mockFeeService;
    @Mock
    private MessageServiceImpl mockMessageService;
    @Mock
    private PasswordEncoder mockPasswordEncoder;
    @Mock
    private ApplicationEventPublisher mockAapplicationEventPublisher;

    @BeforeEach
    void setUp() {
        serviceToTest = new PropertyServiceImpl(mockModelMapper, mockResidentialEntityService,
                mockPropertyRepository, mockMessageService, mockFeeService, mockAapplicationEventPublisher);
    }

    @Test
    void findPropertyById() {
        Property property = new Property();
        property.setNumber(String.valueOf(99));
        when(mockPropertyRepository.findById(1L)).thenReturn(Optional.of(property));

        Property propertyById = serviceToTest.findPropertyById(1L);

        assertEquals("99", propertyById.getNumber());
    }
}