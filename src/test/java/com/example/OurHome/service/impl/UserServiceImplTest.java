package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.Role;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.Entity.dto.BindingModels.User.ProfileEditBindingModel;
import com.example.OurHome.model.Entity.dto.BindingModels.User.UserAuthBindingModel;
import com.example.OurHome.model.Entity.dto.BindingModels.User.UserRegisterBindingModel;
import com.example.OurHome.model.Entity.dto.ViewModels.UserViewModel;
import com.example.OurHome.repo.ResidentialEntityRepository;
import com.example.OurHome.repo.RoleRepository;
import com.example.OurHome.repo.UserRepository;
import com.example.OurHome.service.MessageService;
import com.example.OurHome.service.email.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ResidentialEntityRepository residentialEntityRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private MessageService messageService;
    @Mock
    private EmailService emailService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testPasswordsMatch() {
        assertTrue(userService.passwordsMatch("password", "password"));
        assertFalse(userService.passwordsMatch("password", "differentpassword"));
    }

    @Test
    void testPreRegistrationEmailCheck() {
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new UserEntity()));

        assertTrue(userService.preRegistrationEmailCheck(email));
        assertFalse(userService.preRegistrationEmailCheck("nonexistent@example.com"));
    }

    @Test
    void testDuplicatedUsernameCheck() {
        String username = "testuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new UserEntity()));

        assertTrue(userService.duplicatedUsernameCheck(username));
        assertFalse(userService.duplicatedUsernameCheck("nonexistentuser"));
    }

    @Test
    void testRegisterUser() {
        UserRegisterBindingModel userRegisterBindingModel = new UserRegisterBindingModel();
        userRegisterBindingModel.setUsername("testuser");
        userRegisterBindingModel.setPassword("password");

        ResidentialEntity residentialEntity = new ResidentialEntity();
        residentialEntity.setId(1L);

        when(residentialEntityRepository.findById(1L)).thenReturn(Optional.of(residentialEntity));
        when(roleRepository.findRoleByName("RESIDENT")).thenReturn(new Role());

        userService.registerUser(userRegisterBindingModel, 1L);

        // Verify that userRepository.save() was called with the new user entity
        verify(userRepository, times(1)).save(any(UserEntity.class));

        // You can add more assertions here based on the expected behavior after user registration
    }

    // Add tests for other methods like registerManager, residentialValidation, getUserViewData, etc.

    @Test
    void testCleanUpPasswordRestoreVerificationCodes() {
        UserEntity userWithVerificationCode = new UserEntity();
        userWithVerificationCode.setValidationCode("verificationCode");

        List<UserEntity> usersWithVerificationCode = new ArrayList<>();
        usersWithVerificationCode.add(userWithVerificationCode);

        when(userRepository.findAllUsersWithVerificationCode()).thenReturn(usersWithVerificationCode);

        userService.cleanUpPasswordRestoreVerificationCodes();

        // Verify that validation codes were cleaned up
        assertNull(userWithVerificationCode.getValidationCode());
    }

    @Test
    void testResidentialValidation() {
        ResidentialEntity residentialEntity = new ResidentialEntity();
        residentialEntity.setId(1L);
        residentialEntity.setAccessCode(passwordEncoder.encode("123456"));

        when(residentialEntityRepository.findResidentialEntityById(1L)).thenReturn(residentialEntity);
        when(residentialEntityRepository.countById(1L)).thenReturn(1L);

        assertTrue(userService.residentialValidation(1L, "123456"));
        assertFalse(userService.residentialValidation(1L, "wrongcode"));
        assertFalse(userService.residentialValidation(2L, "123456"));
    }

    @Test
    void testGetUserViewData() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("testuser");

        UserViewModel userViewModel = userService.getUserViewData(userEntity);

        assertNotNull(userViewModel);
        assertEquals(userEntity.getId(), userViewModel.getId());
        assertEquals(userEntity.getUsername(), userViewModel.getUsername());
        // Add more assertions based on the mapping logic in this method
    }

    @Test
    void testFindUserByEmail() {
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new UserEntity()));

        assertNotNull(userService.findUserByEmail(email));
        assertNull(userService.findUserByEmail("nonexistent@example.com"));
    }

    @Test
    void testCreateModerator() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        ResidentialEntity residentialEntity = new ResidentialEntity();
        residentialEntity.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(residentialEntityRepository.findById(1L)).thenReturn(Optional.of(residentialEntity));

        userService.createModerator(1L, 1L);

        assertTrue(userEntity.getModeratedResidentialEntities().contains(residentialEntity));
        verify(userRepository, times(1)).save(userEntity);
        // Add more assertions based on the expected behavior after creating a moderator
    }

    @Test
    void testRemoveModerator() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        ResidentialEntity residentialEntity = new ResidentialEntity();
        residentialEntity.setId(1L);

        userEntity.getModeratedResidentialEntities().add(residentialEntity);

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(residentialEntityRepository.findById(1L)).thenReturn(Optional.of(residentialEntity));

        userService.removeModerator(1L, 1L);

        assertFalse(userEntity.getModeratedResidentialEntities().contains(residentialEntity));
        verify(userRepository, times(1)).save(userEntity);
        // Add more assertions based on the expected behavior after removing a moderator
    }

    @Test
    void testSendVerificationCode() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        userService.sendVerificationCode(userEntity);

        assertNotNull(userEntity.getValidationCode());
        verify(userRepository, times(1)).save(userEntity);
        // Add more assertions based on the expected behavior after sending verification code
    }

    @Test
    void testResetPassword() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        userService.resetPassword(userEntity, "newPassword");

        assertEquals("newPassword", userEntity.getPassword());
        assertNull(userEntity.getValidationCode());
        assertTrue(userEntity.isValidated());
        verify(userRepository, times(1)).save(userEntity);
        // Add more assertions based on the expected behavior after resetting the password
    }

    @Test
    void testJoinUserToNewResidentialEntity() {
        UserAuthBindingModel userAuthBindingModel = new UserAuthBindingModel();
        userAuthBindingModel.setResidentialId("1");

        UserEntity loggedUser = new UserEntity();
        loggedUser.setId(1L);

        ResidentialEntity newResidentialEntity = new ResidentialEntity();
        newResidentialEntity.setId(1L);

        when(residentialEntityRepository.findById(1L)).thenReturn(Optional.of(newResidentialEntity));
        when(userRepository.findById(1L)).thenReturn(Optional.of(loggedUser));

        userService.joinUserToNewResidentialEntity(userAuthBindingModel, loggedUser);

        assertTrue(loggedUser.getResidentialEntities().contains(newResidentialEntity));
        verify(userRepository, times(1)).save(loggedUser);
        // Add more assertions based on the expected behavior after joining the user to a new residential entity
    }

    @Test
    void testSaveAvatar() {
        MultipartFile file = mock(MultipartFile.class);
        Long userId = 1L;

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(file.isEmpty()).thenReturn(false);
        when(file.getSize()).thenReturn(3000000L);
        when(file.getOriginalFilename()).thenReturn("avatar.jpg");

        assertThrows(IOException.class, () -> userService.saveAvatar(file, userId));
        // Add more assertions based on different scenarios for saving an avatar
    }
    @Test
    void testGetProfileEditBindingModel() {
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        ProfileEditBindingModel profileEditBindingModel = userService.getProfileEditBindingModel(userId);

        assertNotNull(profileEditBindingModel);
        // Add assertions based on the expected mapping and behavior of this method
    }

    @Test
    void testEditProfile() {
        Long userId = 1L;
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);

        ProfileEditBindingModel profileEditBindingModel = new ProfileEditBindingModel();
        profileEditBindingModel.setFirstName("John");
        profileEditBindingModel.setLastName("Doe");

        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        userService.editProfile(userId, profileEditBindingModel, false);

        assertEquals("John", userEntity.getFirstName());
        assertEquals("Doe", userEntity.getLastName());
        // Add more assertions based on the expected behavior after profile edit
    }

}