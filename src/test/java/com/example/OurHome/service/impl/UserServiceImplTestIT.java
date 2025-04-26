package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.User.ManagerRegisterBindingModel;
import com.example.OurHome.model.dto.BindingModels.User.UserRegisterBindingModel;
import com.example.OurHome.repo.MessageRepository;
import com.example.OurHome.repo.RoleRepository;
import com.example.OurHome.repo.UserRepository;
import com.example.OurHome.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceImplTestIT {

    @Autowired
    private UserService userServiceToTest;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void testPasswordsMatch() {
        String password = "password";
        String confirmPassword = "password";

        boolean result = userServiceToTest.passwordsMatch(password, confirmPassword);

        assertTrue(result, "Passwords should match");
    }

    @Test
    void testUserRegistration() {
        UserRegisterBindingModel userRegisterBindingModel = new UserRegisterBindingModel();
        userRegisterBindingModel.setPassword("password");
        userRegisterBindingModel.setFirstName("Test");
        userRegisterBindingModel.setLastName("Test");
        userRegisterBindingModel.setEmail("Test@test.bg");

        ResidentialEntity residentialEntity = new ResidentialEntity();
        residentialEntity.setId(10203040L);

        userServiceToTest.registerUser(userRegisterBindingModel, residentialEntity.getId());

        UserEntity registeredUser = userRepository.findByEmail("Test@test.bg").orElse(null);
        assertNotNull(registeredUser, "User should be registered");
    }

    @Test
    void testRegisterManager() {
        ManagerRegisterBindingModel managerRegisterBindingModel = new ManagerRegisterBindingModel();
        managerRegisterBindingModel.setPassword("password");
        managerRegisterBindingModel.setFirstName("Test");
        managerRegisterBindingModel.setLastName("Test");
        managerRegisterBindingModel.setEmail("Test@test.test");

        userServiceToTest.registerManager(managerRegisterBindingModel);

        List<UserEntity> users = userRepository.findAll();
        UserEntity manager = users.get(0);

        assertNotNull(manager);
        assertEquals("MANAGER", manager.getRole().getName());
    }
   }