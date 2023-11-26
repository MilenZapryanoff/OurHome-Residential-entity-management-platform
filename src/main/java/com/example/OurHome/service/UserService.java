package com.example.OurHome.service;

import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.Entity.dto.BindingModels.ManagerRegisterBindingModel;
import com.example.OurHome.model.Entity.dto.BindingModels.ProfileEditBindingModel;
import com.example.OurHome.model.Entity.dto.BindingModels.UserAuthBindingModel;
import com.example.OurHome.model.Entity.dto.BindingModels.UserRegisterBindingModel;
import com.example.OurHome.model.Entity.dto.ViewModels.UserViewModel;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService {

    boolean passwordsMatch(String password, String confirmPassword);

    boolean preRegistrationEmailCheck(String email);

    boolean duplicatedUsernameCheck(String username);

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

    void sendVerificationCode(UserEntity user);

    void resetPassword(UserEntity user, String newPassword);

    boolean verificationCodeMatch(UserEntity user, String verificationCode);

    String saveAvatar(MultipartFile file, Long userId) throws IOException;

    void updateUserAvatar(UserEntity loggedUser, String relativePath);

    ProfileEditBindingModel getProfileEditBindingModel(Long id);

    void editProfile(Long id, ProfileEditBindingModel profileEditBindingModel, Boolean passwordChange);
}
