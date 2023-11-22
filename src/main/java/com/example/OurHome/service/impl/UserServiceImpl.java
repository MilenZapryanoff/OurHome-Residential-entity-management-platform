package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.Entity.dto.BindingModels.ManagerRegisterBindingModel;
import com.example.OurHome.model.Entity.dto.BindingModels.UserAuthBindingModel;
import com.example.OurHome.model.Entity.dto.BindingModels.UserRegisterBindingModel;
import com.example.OurHome.model.Entity.dto.ViewModels.UserViewModel;
import com.example.OurHome.repo.ResidentialEntityRepository;
import com.example.OurHome.repo.RoleRepository;
import com.example.OurHome.repo.UserRepository;
import com.example.OurHome.service.EmailService;
import com.example.OurHome.service.MessageService;
import com.example.OurHome.service.UserService;
import com.example.OurHome.service.tokens.ResidentialEntityToken;
import com.example.OurHome.service.tokens.UserToken;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
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

    public UserServiceImpl(PasswordEncoder passwordEncoder, ModelMapper modelMapper, UserRepository userRepository, ResidentialEntityToken residentialEntityToken, UserToken userToken, ResidentialEntityRepository residentialEntityRepository, RoleRepository roleRepository, MessageService messageService, EmailService emailService) {
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.residentialEntityToken = residentialEntityToken;
        this.userToken = userToken;
        this.residentialEntityRepository = residentialEntityRepository;
        this.roleRepository = roleRepository;
        this.messageService = messageService;
        this.emailService = emailService;
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
    public boolean preRegistrationUserCheck(String username) {
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
     * Residential Entity Token invalidation method.
     * When registering as a resident the user have to provide valid ResidentialEntity ID.
     * It is an 8-digit randomly generated code used as residential entity id in the DB.
     * The ID is used as registration token in UserRegToken class which gets invalidated after
     * registration.
     */

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


