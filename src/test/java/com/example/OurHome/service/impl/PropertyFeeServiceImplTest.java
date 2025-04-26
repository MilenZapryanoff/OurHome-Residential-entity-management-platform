package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.PropertyFee;
import com.example.OurHome.repo.PropertyFeeRepository;
import com.example.OurHome.repo.PropertyRepository;
import com.example.OurHome.service.LogService;
import com.example.OurHome.service.MessageService;
import com.example.OurHome.service.NotificationService;
import com.example.OurHome.service.ResidentialEntityService;
import com.example.OurHome.service.email.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    @Mock
    private NotificationService mockNotificationService;
    @Mock
    private ResidentialEntityService mockResidentialEntityService;
    @Mock
    private LogService mockLogService;
    @Mock
    private EmailService mockemailService;

    @BeforeEach
    void setUp() {
        serviceToTest = new PropertyFeeServiceImpl(mockPropertyFeeRepository, mockPropertyRepository, mockModelMapper,
                mockMessageService, mockResidentialEntityService, mockNotificationService, mockLogService, mockemailService);
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