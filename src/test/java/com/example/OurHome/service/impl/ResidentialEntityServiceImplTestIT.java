package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.*;
import com.example.OurHome.model.dto.BindingModels.ResidentialEntity.ResidentialEntityEditBindingModel;
import com.example.OurHome.model.dto.BindingModels.ResidentialEntity.ResidentialEntityRegisterBindingModel;
import com.example.OurHome.model.enums.CityName;
import com.example.OurHome.repo.*;
import com.example.OurHome.service.ResidentialEntityService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ResidentialEntityServiceImplTestIT {

    @Autowired
    private ResidentialEntityService residentialEntityServiceToTest;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private ResidentialEntityRepository residentialEntityRepository;
    @Autowired
    private PropertyRepository propertyRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LanguageRepository languageRepository;

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
    void testNewResidentialEntity() {
        ResidentialEntityRegisterBindingModel residentialEntityRegisterBindingModel = new ResidentialEntityRegisterBindingModel();

        residentialEntityRegisterBindingModel.setAccessCode("test");
        residentialEntityRegisterBindingModel.setEntrance("A");
        residentialEntityRegisterBindingModel.setStreetName("Test");
        residentialEntityRegisterBindingModel.setStreetNumber("111");
        residentialEntityRegisterBindingModel.setNumberOfApartments(100L);
        residentialEntityRegisterBindingModel.setCity(CityName.valueOf("София"));

        residentialEntityServiceToTest.newResidentialEntity(residentialEntityRegisterBindingModel, getManager());

        List<ResidentialEntity> all = residentialEntityRepository.findAll();

        assertFalse(all.isEmpty());
    }

    @Test
    void testDeleteResidentialEntity() {
        createResidentialEntity();

        residentialEntityServiceToTest.deleteResidentialEntity(100L);

        List<ResidentialEntity> all = residentialEntityRepository.findAll();

        assertTrue(all.isEmpty());
    }



    @Test
    void testFindResidentialEntityById() {
        createResidentialEntity();
        List<ResidentialEntity> residentialEntities = residentialEntityRepository.findAll();
        Long id = residentialEntities.get(0).getId();

        Optional<ResidentialEntity> residentialEntityResult = residentialEntityServiceToTest.findResidentialEntityById(id);

        assertEquals(id, residentialEntityResult.get().getId());
    }

    @Test
    void testEditResidentialEntity() {
        createResidentialEntity();

        ResidentialEntityEditBindingModel residentialEntityEditBindingModel = new ResidentialEntityEditBindingModel();
        residentialEntityEditBindingModel.setAccessCode("modified");
        residentialEntityEditBindingModel.setCity(CityName.valueOf("Пловдив"));
        residentialEntityEditBindingModel.setStreetName("testPass");
        residentialEntityEditBindingModel.setStreetNumber(String.valueOf(2));

        residentialEntityServiceToTest.editResidentialEntity(100L, residentialEntityEditBindingModel);

        Optional<ResidentialEntity> editedResidentialEntity = residentialEntityRepository.findById(100L);

        assertEquals(residentialEntityEditBindingModel.getStreetName(), editedResidentialEntity.get().getStreetName());
        assertEquals(residentialEntityEditBindingModel.getStreetNumber(), editedResidentialEntity.get().getStreetNumber());
    }

    @Test
    void testFindResidentialEntitiesByManagerId() {
        createResidentialEntity();

        List<ResidentialEntity> all = residentialEntityRepository.findAll();
        Long managerId = all.get(0).getManager().getId();

        List<ResidentialEntity> residentialEntitiesByManagerId = residentialEntityServiceToTest.findResidentialEntitiesByManagerId(managerId);

        assertFalse(residentialEntitiesByManagerId.isEmpty());
        assertEquals(1, residentialEntitiesByManagerId.size());
    }

    @Test
    void testMapEntityToEditBindingModel() {
        ResidentialEntity residentialEntity = createResidentialEntity();

        ResidentialEntityEditBindingModel residentialEntityEditBindingModel = residentialEntityServiceToTest.mapEntityToEditBindingModel(residentialEntity);

        assertNotNull(residentialEntityEditBindingModel);
        assertEquals(residentialEntity.getAccessCode(), residentialEntityEditBindingModel.getAccessCode());
        assertEquals(residentialEntity.getStreetName(), residentialEntityEditBindingModel.getStreetName());
        assertEquals(residentialEntity.getStreetNumber(), residentialEntityEditBindingModel.getStreetNumber());
        assertEquals(residentialEntity.getEntrance(), residentialEntityEditBindingModel.getEntrance());
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
        Language language = languageRepository.findLanguageByDescription("bulgarian");
        languageRepository.save(language);

        manager.setEmail("test@test.test");
        manager.setFirstName("Test");
        manager.setLastName("Test");
        manager.setPassword("testPassword");
        manager.setPhoneNumber("0777777777");
        manager.setRegistrationDateTime(LocalDateTime.now());
        manager.setRole(role);
        manager.setLanguage(language);
        userRepository.save(manager);
        return manager;
    }

}