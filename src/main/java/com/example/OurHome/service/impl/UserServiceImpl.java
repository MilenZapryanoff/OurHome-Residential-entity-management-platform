package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.Language;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.User.ManagerRegisterBindingModel;
import com.example.OurHome.model.dto.BindingModels.User.ProfileEditBindingModel;
import com.example.OurHome.model.dto.BindingModels.User.UserAuthBindingModel;
import com.example.OurHome.model.dto.BindingModels.User.UserRegisterBindingModel;
import com.example.OurHome.model.dto.ViewModels.UserViewModel;
import com.example.OurHome.repo.LanguageRepository;
import com.example.OurHome.repo.ResidentialEntityRepository;
import com.example.OurHome.repo.RoleRepository;
import com.example.OurHome.repo.UserRepository;
import com.example.OurHome.service.MessageService;
import com.example.OurHome.service.UserService;
import com.example.OurHome.service.email.EmailService;
import com.example.OurHome.service.tokens.ResidentialEntityToken;
import com.example.OurHome.service.tokens.UserToken;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final ResidentialEntityToken residentialEntityToken;
    private final UserToken userToken;
    private final ResidentialEntityRepository residentialEntityRepository;
    private final RoleRepository roleRepository;
    private final MessageService messageService;
    private final EmailService emailService;
    private final LanguageRepository languageRepository;

    public UserServiceImpl(PasswordEncoder passwordEncoder, ModelMapper modelMapper, UserRepository userRepository, ResidentialEntityToken residentialEntityToken, UserToken userToken, ResidentialEntityRepository residentialEntityRepository, RoleRepository roleRepository, MessageService messageService, EmailService emailService, LanguageRepository languageRepository) {
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.residentialEntityToken = residentialEntityToken;
        this.userToken = userToken;
        this.residentialEntityRepository = residentialEntityRepository;
        this.roleRepository = roleRepository;
        this.messageService = messageService;
        this.emailService = emailService;
        this.languageRepository = languageRepository;
    }

    /**
     * PRE-REGISTRATION:
     * password match check
     *
     * @return boolean
     */
    @Override
    public boolean passwordsMatch(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }

    /**
     * PRE-REGISTRATION:
     * email duplicate check
     *
     * @return boolean
     */

    @Override
    public boolean preRegistrationEmailCheck(String email) {
        UserEntity checkEmail = userRepository.findByEmail(email).orElse(null);
        return checkEmail != null;
    }

    /**
     * PRE-REGISTRATION:
     * username duplicate check
     *
     * @return boolean
     */

    @Override
    public boolean duplicatedUsernameCheck(String username) {
        UserEntity checkUser = userRepository.findByUsername(username).orElse(null);
        return checkUser != null;
    }

    /**
     * RESIDENT REGISTRATION implementation
     */
    @Override
    public void registerUser(UserRegisterBindingModel userRegisterBindingModel, Long residentialEntityId) {
        ResidentialEntity residentialEntity = residentialEntityRepository.findById(residentialEntityId).orElse(null);

        UserEntity newUserEntity = modelMapper.map(userRegisterBindingModel, UserEntity.class);
        newUserEntity.setPassword(passwordEncoder.encode(userRegisterBindingModel.getPassword()));
        newUserEntity.getResidentialEntities().add(residentialEntity);
        newUserEntity.setRole(roleRepository.findRoleByName("RESIDENT"));
        newUserEntity.setValidated(true);
        newUserEntity.setRegistrationDateTime(LocalDateTime.now());
        newUserEntity.setLanguage(languageRepository.findLanguageByDescription("bulgarian"));
        newUserEntity.setAvatarPath("/avatars/default.jpg");

        userRepository.save(newUserEntity);

        //sending a Welcome message to user
        messageService.sendRegistrationMessageToUser(newUserEntity);

        invalidateResidentialEntityToken();
    }

    /**
     * MANAGER REGISTRATION implementation
     */
    @Override
    public void registerManager(ManagerRegisterBindingModel managerRegisterBindingModel) {

        UserEntity newManager = modelMapper.map(managerRegisterBindingModel, UserEntity.class);
        newManager.setPassword(passwordEncoder.encode(managerRegisterBindingModel.getPassword()));
        newManager.setValidated(true);
        newManager.setRole(roleRepository.findRoleByName("MANAGER"));
        newManager.setAvatarPath("/avatars/default-manager.jpg");
        newManager.setRegistrationDateTime(LocalDateTime.now());
        newManager.setLanguage(languageRepository.findLanguageByDescription("bulgarian"));

        userRepository.save(newManager);
    }

    /**
     * Method for checking residential entity id and access code, when accessing a new residential entity. .
     *
     * @return : boolean
     */
    @Override
    public boolean residentialValidation(Long residentialId, String accessCode) {
        ResidentialEntity residentialEntity = residentialEntityRepository.findResidentialEntityById(residentialId);

        return residentialEntityRepository.countById(residentialId) > 0 && passwordEncoder.matches(accessCode, residentialEntity.getAccessCode());
    }

    /**
     * UserViewModel initialization method.
     *
     * @return : UserViewModel
     */
    @Override
    public UserViewModel getUserViewData(UserEntity userEntity) {
        return modelMapper.map(userEntity, UserViewModel.class);
    }

    /**
     * method : Finds UserEntity by email from DB
     *
     * @return : UserEntity
     */

    @Override
    public UserEntity findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public UserEntity findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * This method gives the user MODERATOR rights in residential entities.
     */
    @Override
    public void createModerator(Long residentId, Long entityId) {
        UserEntity userEntity = userRepository.findById(residentId).orElse(null);
        ResidentialEntity residentialEntity = residentialEntityRepository.findById(entityId).orElse(null);

        if (residentialEntity != null && userEntity != null) {
            userEntity.getModeratedResidentialEntities().add(residentialEntity);
            userRepository.save(userEntity);
            messageService.newModeratorMessage(userEntity, residentialEntity);
        }
    }


    /**
     * This method removes the user MODERATOR rights in residential entities.
     */
    @Override
    public void removeModerator(Long residentId, Long entityId) {
        UserEntity userEntity = userRepository.findById(residentId).orElse(null);
        ResidentialEntity residentialEntity = residentialEntityRepository.findById(entityId).orElse(null);

        if (residentialEntity != null && userEntity != null) {
            List<ResidentialEntity> moderatedEntities = userEntity.getModeratedResidentialEntities();

            // Iterate through the list and remove the entity with the matching ID
            Iterator<ResidentialEntity> iterator = moderatedEntities.iterator();
            while (iterator.hasNext()) {
                ResidentialEntity entity = iterator.next();
                if (entity.getId().equals(entityId)) {
                    iterator.remove();
                    break;
                }
            }
            userRepository.save(userEntity);
        }
    }

    /**
     * This method removes the user from residential entities. Also removes owner's property from the
     * residential entity.
     *
     * @param id, residentialEntity
     */
    @Override
    public void removeResidentFromResidentialEntity(Long id, ResidentialEntity residentialEntity) {
        UserEntity userEntity = userRepository.findById(id).orElse(null);

        if (userEntity != null) {
            removeModerator(id, residentialEntity.getId());

            List<ResidentialEntity> residentialEntities = userEntity.getResidentialEntities();
            // Iterate through the list and remove the entity with the matching ID
            Iterator<ResidentialEntity> iterator = residentialEntities.iterator();
            while (iterator.hasNext()) {
                ResidentialEntity entity = iterator.next();
                if (entity.getId().equals(residentialEntity.getId())) {
                    iterator.remove();
                    break;
                }
            }
            userRepository.save(userEntity);
        }
    }

    @Override
    public void sendVerificationCode(UserEntity user) {
        Long verificationCode = new Random().nextLong(99999999, 1000000000);

        user.setValidationCode(passwordEncoder.encode(String.valueOf(verificationCode)));

        //TODO: on some stage to change isValidated field to hasActiveRestorePassword because this stands behind the idea of this boolean
        user.setValidated(false);
        userRepository.save(user);

        emailService.sendResetPasswordEmail(user.getEmail(), String.valueOf(verificationCode));
    }

    @Override
    public void resetPassword(UserEntity user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setValidationCode(null);
        user.setValidated(true);
        userRepository.save(user);

        invalidateUserToken();
    }

    @Override
    public boolean verificationCodeMatch(UserEntity user, String verificationCode) {
        return passwordEncoder.matches(verificationCode, user.getValidationCode());
    }


    /**
     * Method for joining user to new Residential entity.
     */
    @Override
    public void joinUserToNewResidentialEntity(UserAuthBindingModel userAuthBindingModel, UserEntity loggedUser) {

        ResidentialEntity newResidentialEntity = residentialEntityRepository.findById(Long.valueOf(userAuthBindingModel.getResidentialId())).orElse(null);

        if (newResidentialEntity != null) {
            //Check if this residential entity already preset for this user
            List<ResidentialEntity> userResidentialEntities = loggedUser.getResidentialEntities();
            for (ResidentialEntity residentialEntityList : userResidentialEntities) {
                if (residentialEntityList.getId().equals(newResidentialEntity.getId())) {
                    return;
                }
            }
            loggedUser.getResidentialEntities().add(newResidentialEntity);
            userRepository.save(loggedUser);
        }
    }

    /**
     * @param file   upload file
     * @param userId logged user id
     * @return String avatarPath
     * @throws IOException IOException or IllegalArgumentException
     */
    @Override
    public String saveAvatar(MultipartFile file, Long userId) throws IOException {

        UserEntity user = userRepository.findById(userId).orElse(null);

        if (file != null && !file.isEmpty()) {

            if (file.getSize() > 3 * 1024 * 1024) {
                throw new IllegalArgumentException("File size exceeds the allowed limit (3MB)");
            }

            assert user != null;
            String uploadDirectory = "src/main/resources/static/avatars";
            File directory = new File(uploadDirectory);

            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    throw new IOException("Failed to create directory!");
                }
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null) {
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));

                // Validate file type (allow only image files)
                if (!fileExtension.matches("\\.(jpg|jpeg|png|gif)$")) {
                    throw new IllegalArgumentException("Invalid file type!");
                }

                String fileName = "avatar-" + user.getId() + "-" + user.getUsername() + fileExtension;
                Path filePath = Paths.get(uploadDirectory, fileName);

                try {
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    // Update the user's avatarPath in the database
                    String avatarPath = "/avatars/" + fileName;
                    // Update user entity with the avatar path
                    updateUserAvatar(userRepository.findById(userId).orElseThrow(), avatarPath);
                    return avatarPath;
                } catch (IOException e) {
                    throw new IOException("Failed to save the file!");
                }
            } else {
                throw new IOException("Invalid file name!");
            }
        } else {
            throw new IllegalArgumentException("File is null or empty!");
        }
    }

    /**
     * Update of avatarPath in the DB.
     *
     * @param userEntity logged user
     * @param avatarPath the path of the user picture stored in DB
     */
    @Override
    public void updateUserAvatar(UserEntity userEntity, String avatarPath) {
        if (userEntity != null) {
            userEntity.setAvatarPath(avatarPath);
            userRepository.save(userEntity);
        } else {
            throw new IllegalArgumentException("UserEntity is null!");
        }
    }

    @Override
    public ProfileEditBindingModel getProfileEditBindingModel(Long id) {
        UserEntity userEntity = userRepository.findById(id).orElse(null);

        if (userEntity != null) {
            return modelMapper.map(userEntity, ProfileEditBindingModel.class);
        }
        return new ProfileEditBindingModel();
    }

    /**
     * Profile edit method.
     *
     * @param id                      logged user id
     * @param profileEditBindingModel bearing new data
     */
    @Override
    public void editProfile(Long id, ProfileEditBindingModel profileEditBindingModel, Boolean passwordChange) {

        UserEntity userEntity = userRepository.findById(id).orElse(null);
        if (userEntity != null) {

            modelMapper.map(profileEditBindingModel, userEntity);
            if (passwordChange) {
                userEntity.setPassword(passwordEncoder.encode(profileEditBindingModel.getNewPassword()));
            }
            userRepository.save(userEntity);
        }
    }

    /**
     * When user request password reset he receives a verification code on his email. Using this code the user can
     * reset his password.
     * This method cleans up issued and not user verification codes every night at 0:00 o`clock. If user request again a password
     * restore a new verification code will be issued and send to user's email.
     */
    @Override
    public void cleanUpPasswordRestoreVerificationCodes() {
        List<UserEntity> allUsersWithVerificationCode = userRepository.findAllUsersWithVerificationCode();
        for (UserEntity userEntity : allUsersWithVerificationCode) {
            userEntity.setValidationCode(null);
            userEntity.setValidated(true);
            userRepository.save(userEntity);
        }
    }

    @Override
    public void removeAvatar(Long loggedUserId) throws IOException {

        UserEntity loggedUser = userRepository.findById(loggedUserId).orElseThrow(() -> new IllegalArgumentException("User not found!"));

        // Get the current avatar path
        String avatarPath = loggedUser.getAvatarPath();

        // Check if the user has a custom avatar
        if (avatarPath != null && avatarPath.matches("/avatars/avatar-" + loggedUser.getId() + "-.*\\.(jpg|jpeg|png|gif)$")) {
            String uploadDirectory = "src/main/resources/static";
            Path filePath = Paths.get(uploadDirectory, avatarPath);

            // Delete the file from the file system
            try {
                Files.deleteIfExists(filePath);

                // Set the avatarPath to the default value based on the user's role
                String defaultAvatarPath = loggedUser.getRole().getName().equals("MANAGER") ? "/avatars/default-manager.jpg" : "/avatars/default.jpg";
                loggedUser.setAvatarPath(defaultAvatarPath);

                userRepository.save(loggedUser);
            } catch (IOException e) {
                throw new IOException("Failed to delete the file!", e);
            }
        } else {
            throw new IllegalArgumentException("User does not have a custom avatar set!");
        }
    }

    /**
     * Method for setting system messages languege. This setting is different from global language!
     * @param lang is the selected FE language
     * @param userViewModel is the current instance of users logged in
     */
    @Override
    public void setSystemMessagesLanguage(String lang, UserViewModel userViewModel) {

        UserEntity user = userRepository.findByEmail(userViewModel.getEmail()).orElse(null);

        if ("bg".equals(lang) && user != null) {
            user.setLanguage(languageRepository.findLanguageByDescription("bulgarian"));
        } else if ("en".equals(lang) && user != null) {
            user.setLanguage(languageRepository.findLanguageByDescription("english"));
        }

        userRepository.save(user);
    }


    private void invalidateResidentialEntityToken() {
        residentialEntityToken.setValid(false);
    }

    /**
     * User Token invalidation method.
     * Used in the process of resetting password
     */


    private void invalidateUserToken() {
        userToken.setValid(false);
    }

}


