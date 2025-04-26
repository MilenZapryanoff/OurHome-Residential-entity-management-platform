package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.repo.PropertyRepository;
import com.example.OurHome.repo.ResidentialEntityRepository;
import com.example.OurHome.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private ResidentialEntityRepository residentialEntityRepository;
    @Mock
    private FeeServiceImpl mockFeeService;
    @Mock
    private MessageServiceImpl mockMessageService;
    @Mock
    private PasswordEncoder mockPasswordEncoder;
    @Mock
    private ApplicationEventPublisher mockAapplicationEventPublisher;
    @Mock
    private PropertyRegisterRequestService mockPropertyRegisterRequestService;
    @Mock
    private PropertyChangeRequestService mockPropertyChangeRequestService;
    @Mock
    private NotificationService mockNotificationService;
    @Mock
    private LogService mockLogService;


    @BeforeEach
    void setUp() {
        serviceToTest = new PropertyServiceImpl(mockModelMapper, mockPropertyRepository,  mockMessageService, mockFeeService, mockAapplicationEventPublisher,
                residentialEntityRepository, mockPropertyRegisterRequestService, mockPropertyChangeRequestService, mockNotificationService, mockLogService);
    }

    @Test
    void findPropertyById() {
        Property property = new Property();
        property.setNumber(99);
        when(mockPropertyRepository.findById(1L)).thenReturn(Optional.of(property));

        Property propertyById = serviceToTest.findPropertyById(1L);

        assertEquals(99, propertyById.getNumber());
    }
}