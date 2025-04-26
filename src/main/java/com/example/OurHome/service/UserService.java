package com.example.OurHome.service;

import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.User.*;
import com.example.OurHome.model.dto.ViewModels.UserViewModel;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {

    boolean passwordsMatch(String password, String confirmPassword);

    boolean preRegistrationEmailCheck(String email);

    void registerUser(UserRegisterBindingModel userRegisterBindingModel, Long residentialEntityId);

    void registerManager(ManagerRegisterBindingModel managerRegisterBindingModel);

    boolean residentialValidation(Long residentialId, String validationCode);

    void joinUserToNewResidentialEntity(UserAuthBindingModel userAuthBindingModel, UserEntity loggedUser);

    UserViewModel getUserViewData(UserEntity userEntity);

    UserEntity findUserByEmail(String email);

    UserEntity findUserById(Long id);

    void createModerator(Long residentId, Long entityId);

    void removeModerator(Long residentId, Long entityId);

    void removeResidentFromResidentialEntity(Long id, ResidentialEntity residentialEntity);

    void sendResetCode(UserEntity user);

    void resetPassword(UserEntity user, String newPassword);

    boolean resetCodeMatch(UserEntity user, String resetCode);

    String saveAvatar(MultipartFile file, Long userId) throws IOException;

    void updateUserAvatar(UserEntity loggedUser, String relativePath);

    ProfileEditBindingModel getProfileEditBindingModel(Long id);

    void editProfile(Long id, ProfileEditBindingModel profileEditBindingModel, Boolean passwordChange);

    void cleanUpPasswordResetCodes();

    void removeAvatar(Long loggedUserId) throws IOException;

    void setSystemMessagesLanguage(String lang, UserViewModel userViewModel);

    ProfileNotificationsEditBindingModel mapProfileNotificationEditBindingModel(UserViewModel currentUser);

    void updateEmailNotificationsSettings(Long id, ProfileNotificationsEditBindingModel profileNotificationsEditBindingModel);
}
