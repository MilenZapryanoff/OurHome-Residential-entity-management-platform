package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.PropertyFee;
import com.example.OurHome.repo.PropertyFeeRepository;
import com.example.OurHome.repo.PropertyRepository;
import com.example.OurHome.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PropertyFeeServiceImplTest {

    private PropertyFeeServiceImpl serviceToTest;
    @Mock
    private PropertyFeeRepository mockPropertyFeeRepository;
    @Mock
    private PropertyRepository mockPropertyRepository;
    @Mock
    private ModelMapper mockModelMapper;
    @Mock
    private MessageService mockMessageService;

    @BeforeEach
    void setUp() {
        serviceToTest = new PropertyFeeServiceImpl(mockPropertyFeeRepository,mockPropertyRepository,
                mockModelMapper, mockMessageService);
    }

    @Test
    void findPropertyFeeById() {
        PropertyFee propertyFee = new PropertyFee();
        propertyFee.setDescription("test");

        when(mockPropertyFeeRepository.findById(1L)).thenReturn(Optional.of(propertyFee));

        PropertyFee propertyFeeById = serviceToTest.findPropertyFeeById(1L);

        assertEquals("test", propertyFeeById.getDescription());
    }
}