package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.User.ProfileEditBindingModel;
import com.example.OurHome.model.dto.BindingModels.User.UserAuthBindingModel;
import com.example.OurHome.repo.ResidentialEntityRepository;
import com.example.OurHome.repo.RoleRepository;
import com.example.OurHome.repo.UserRepository;
import com.example.OurHome.service.MessageService;
import com.example.OurHome.service.email.EmailService;
import com.example.OurHome.service.tokens.ResidentialEntityToken;
import com.example.OurHome.service.tokens.UserToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private UserServiceImpl serviceToTest;
    @Mock
    private PasswordEncoder mockPasswordEncoder;
    @Mock
    private ModelMapper mockModelMapper;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private ResidentialEntityToken mockResidentialEntityToken;
    @Mock
    private UserToken mockUserToken;
    @Mock
    private ResidentialEntityRepository mockResidentialEntityRepository;
    @Mock
    private RoleRepository mockRoleRepository;
    @Mock
    private MessageService mockMessageService;
    @Mock
    private EmailService mockEmailService;
    @Captor
    private ArgumentCaptor<UserEntity> userEntityCaptor;

    @BeforeEach
    void setUp() {
        serviceToTest = new UserServiceImpl(mockPasswordEncoder, mockModelMapper, mockUserRepository,
                mockResidentialEntityToken, mockUserToken, mockResidentialEntityRepository, mockRoleRepository,
                mockMessageService, mockEmailService);
    }


    @Test
    void passwordsMatch() {
        assertTrue(serviceToTest.passwordsMatch("password", "password"));
        assertFalse(serviceToTest.passwordsMatch("password", "differentPassword"));
    }

    @Test
    void preRegistrationEmailCheck() {
        when(mockUserRepository.findByEmail("test@test.bg")).thenReturn(Optional.of(new UserEntity()));

        assertTrue(serviceToTest.preRegistrationEmailCheck("test@test.bg"), "Email already exists");
        assertFalse(serviceToTest.preRegistrationEmailCheck("test@test.com"), "Email does not exist");
    }

    @Test
    void duplicatedUsernameCheck() {
        when(mockUserRepository.findByUsername("testUser")).thenReturn(Optional.of(new UserEntity()));

        assertTrue(serviceToTest.duplicatedUsernameCheck("testUser"), "Duplicated user");
        assertFalse(serviceToTest.duplicatedUsernameCheck("testUser1"), "Not duplicated user");
    }

    @Test
    void findUserByEmail() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("Test");

        when(mockUserRepository.findByEmail("test@test.bg")).thenReturn(Optional.of(userEntity));

        UserEntity user = serviceToTest.findUserByEmail("test@test.bg");

        assertEquals("Test", user.getUsername());
    }

    @Test
    void findUserById() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("Test");

        when(mockUserRepository.findById(101L)).thenReturn(Optional.of(userEntity));

        UserEntity user = serviceToTest.findUserById(101L);

        assertEquals("Test", user.getUsername());
    }

    @Test
    void createModerator() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("Test");

        ResidentialEntity residentialEntity = new ResidentialEntity();
        residentialEntity.setAccessCode("TestAccessCode");

        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(mockResidentialEntityRepository.findById(101L)).thenReturn(Optional.of(residentialEntity));

        ArgumentCaptor<UserEntity> userEntityCaptor = ArgumentCaptor.forClass(UserEntity.class);
        ArgumentCaptor<ResidentialEntity> residentialEntityCaptor = ArgumentCaptor.forClass(ResidentialEntity.class);

        serviceToTest.createModerator(1L, 101L);

        assertFalse(userEntity.getModeratedResidentialEntities().isEmpty());

        verify(mockUserRepository).save(userEntity);
        verify(mockMessageService).newModeratorMessage(userEntityCaptor.capture(), residentialEntityCaptor.capture());

        assertEquals(userEntity, userEntityCaptor.getValue());
        assertEquals(residentialEntity, residentialEntityCaptor.getValue());
    }

    @Test
    void removeModerator() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("Test");

        ResidentialEntity residentialEntity = new ResidentialEntity();
        residentialEntity.setId(101L);

        userEntity.getModeratedResidentialEntities().add(residentialEntity);

        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(mockResidentialEntityRepository.findById(101L)).thenReturn(Optional.of(residentialEntity));

        serviceToTest.removeModerator(1L, 101L);

        assertTrue(userEntity.getModeratedResidentialEntities().isEmpty());

        verify(mockUserRepository).save(userEntity);
    }

    @Test
    void removeResidentFromResidentialEntity() {
        Long userId = 1L;
        Long residentialEntityId = 10L;

        UserEntity userEntity = new UserEntity(); // Create a UserEntity object
        userEntity.setId(userId);
        List<ResidentialEntity> residentialEntities = new ArrayList<>();
        ResidentialEntity residentialEntity = new ResidentialEntity();
        residentialEntity.setId(residentialEntityId);
        residentialEntities.add(residentialEntity);
        userEntity.setResidentialEntities(residentialEntities);

        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(mockResidentialEntityRepository.findById(residentialEntityId)).thenReturn(Optional.of(residentialEntity));

        serviceToTest.removeResidentFromResidentialEntity(userId, residentialEntity);

        assertFalse(userEntity.getResidentialEntities().contains(residentialEntity));
    }

    @Test
    void resetPassword() {
        String newPassword = "newPassword123";
        UserEntity user = new UserEntity();
        user.setValidationCode("validationCode");
        user.setValidated(false);

        when(mockPasswordEncoder.encode(newPassword)).thenReturn("encodedPassword");
        when(mockUserRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        serviceToTest.resetPassword(user, newPassword);

        assertEquals("encodedPassword", user.getPassword());
        assertNull(user.getValidationCode());
        assertTrue(user.isValidated());
        verify(mockUserRepository, times(1)).save(user);
    }

    @Test
    void verificationCodeMatch() {
        UserEntity userEntity = new UserEntity();
        userEntity.setValidationCode(mockPasswordEncoder.encode("password"));

        assertFalse(serviceToTest.verificationCodeMatch(userEntity, "differentPassword"));
    }

    @Test
    void joinUserToNewResidentialEntity() {
        Long residentialId = 101L;

        UserEntity loggedUser = getUserEntity();
        ResidentialEntity residentialEntity = new ResidentialEntity();
        residentialEntity.setId(residentialId);

        UserAuthBindingModel userAuthBindingModel = new UserAuthBindingModel();
        userAuthBindingModel.setResidentialId(String.valueOf(residentialId));

        List<ResidentialEntity> userResidentialEntities = new ArrayList<>();
        loggedUser.setResidentialEntities(userResidentialEntities);

        when(mockResidentialEntityRepository.findById(residentialId)).thenReturn(Optional.of(residentialEntity));

        serviceToTest.joinUserToNewResidentialEntity(userAuthBindingModel, loggedUser);

        assertEquals(1, loggedUser.getResidentialEntities().size());
        assertTrue(loggedUser.getResidentialEntities().contains(residentialEntity));
        verify(mockUserRepository, times(1)).save(loggedUser);
    }


    @Test
    void updateUserAvatar() {
        UserEntity userEntity = new UserEntity();

        serviceToTest.updateUserAvatar(userEntity,"/test/avatarPath");

        assertEquals("/test/avatarPath", userEntity.getAvatarPath());
    }

    @Test
    public void testUpdateUserAvatar_NullUserEntity() {
        String avatarPath = "/test/avatarPath";

        assertThrows(IllegalArgumentException.class, ()
                -> serviceToTest.updateUserAvatar(null, avatarPath));
    }

    @Test
    void getProfileEditBindingModel() {
        UserEntity userEntity = getUserEntity();

        ProfileEditBindingModel expectedProfileEditBindingModel = new ProfileEditBindingModel();
        expectedProfileEditBindingModel.setFirstName(userEntity.getFirstName());
        expectedProfileEditBindingModel.setLastName(userEntity.getLastName());
        expectedProfileEditBindingModel.setUsername(userEntity.getUsername());
        expectedProfileEditBindingModel.setPhoneNumber(userEntity.getPhoneNumber());

        when(mockUserRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        when(mockModelMapper.map(userEntity, ProfileEditBindingModel.class))
                .thenReturn(expectedProfileEditBindingModel);

        ProfileEditBindingModel profileEditBindingModel = serviceToTest.getProfileEditBindingModel(1L);

        assertNotNull(profileEditBindingModel);
        assertEquals("User", profileEditBindingModel.getFirstName());
        assertEquals("Test", profileEditBindingModel.getLastName());
        assertEquals("tester", profileEditBindingModel.getUsername());
        assertEquals("0885", profileEditBindingModel.getPhoneNumber());
    }

    @Test
    public void testEditProfile() {
        // Arrange
        Long userId = 1L;
        ProfileEditBindingModel profileEditBindingModel = new ProfileEditBindingModel();
        profileEditBindingModel.setNewPassword("newPassword");

        UserEntity existingUser = new UserEntity();
        existingUser.setId(userId);
        existingUser.setUsername("existingUser");

        when(mockUserRepository.findById(userId)).thenReturn(java.util.Optional.of(existingUser));
        when(mockPasswordEncoder.encode(profileEditBindingModel.getNewPassword())).thenReturn("encodedPassword");

        serviceToTest.editProfile(userId, profileEditBindingModel, true);


        verify(mockUserRepository, times(1)).findById(userId);
        verify(mockPasswordEncoder, times(1)).encode(profileEditBindingModel.getNewPassword());
        verify(mockUserRepository, times(1)).save(existingUser);

        assertEquals("encodedPassword", existingUser.getPassword());
    }

    @Test
    void testCleanUpPasswordRestoreVerificationCodes() {

        List<UserEntity> usersWithCodes = new ArrayList<>();
        UserEntity user1 = new UserEntity();
        user1.setId(1L);
        user1.setValidationCode("someVerificationCode1");
        usersWithCodes.add(user1);

        UserEntity user2 = new UserEntity();
        user2.setId(2L);
        user2.setValidationCode("someVerificationCode2");
        usersWithCodes.add(user2);

        when(mockUserRepository.findAllUsersWithVerificationCode()).thenReturn(usersWithCodes);

        serviceToTest.cleanUpPasswordRestoreVerificationCodes();

        verify(mockUserRepository, times(2)).save(userEntityCaptor.capture());

        List<UserEntity> capturedUsers = userEntityCaptor.getAllValues();
        assertTrue(capturedUsers.stream().allMatch(user -> user.getValidationCode() == null));
    }

    @Test
    public void testSaveAvatarValidImage() throws IOException {
        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setUsername("testUser");

        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(user));

        String avatarPath = serviceToTest.saveAvatar(file, userId);

        assertNotNull(avatarPath);
        assertTrue(avatarPath.startsWith("/avatars/"));
    }

    @Test
    public void testSaveAvatarInvalidImageType() {
        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setUsername("testUser");

        MockMultipartFile file = new MockMultipartFile(
                "image",
                "test.txt",
                "text/plain",
                "test image content".getBytes()
        );

        when(mockUserRepository.findById(userId)).thenReturn(Optional.of(user));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> serviceToTest.saveAvatar(file, userId));

        assertEquals("Invalid file type!", exception.getMessage());

        verify(mockUserRepository, times(1)).findById(userId);
        verify(mockPasswordEncoder, never()).encode(anyString());
    }




    private static UserEntity getUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setFirstName("User");
        userEntity.setLastName("Test");
        userEntity.setUsername("tester");
        userEntity.setPhoneNumber("0885");
        userEntity.setPassword("testPassword");
        return userEntity;
    }
}
