package com.example.OurHome.security.impl;

import com.example.OurHome.model.Entity.*;
import com.example.OurHome.security.SecurityService;
import com.example.OurHome.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("securityService")
public class SecurityServiceImpl implements SecurityService {

    private final UserService userService;
    private final ResidentialEntityService residentialEntityService;
    private final PropertyService propertyService;
    private final PropertyFeeService propertyFeeService;
    private final MessageService messageService;
    private final FinancialService financialService;
    private final PropertyTypeService propertyTypeService;

    public SecurityServiceImpl(UserService userService, ResidentialEntityService residentialEntityService, PropertyService propertyService, PropertyFeeService propertyFeeService, MessageService messageService, FinancialService financialService, PropertyTypeService propertyTypeService) {
        this.userService = userService;
        this.residentialEntityService = residentialEntityService;
        this.propertyService = propertyService;
        this.propertyFeeService = propertyFeeService;
        this.messageService = messageService;
        this.financialService = financialService;
        this.propertyTypeService = propertyTypeService;
    }

    @Override
    public boolean checkResidentialEntityModeratorAccess(Long residentialEntityId, Authentication authentication) {
        ResidentialEntity residentialEntity = residentialEntityService.findResidentialEntityById(residentialEntityId).orElse(null);

        return residentialEntity != null && residentialEntity.getManager().getId().equals(getUserEntity(authentication).getId());
    }

    @Override
    public boolean checkPropertyModeratorAccess(Long propertyId, Authentication authentication) {
        Property property = propertyService.findPropertyById(propertyId);

        return property != null && property.getResidentialEntity().getManager().getId().equals(getUserEntity(authentication).getId());
    }

    @Override
    public boolean checkResidentModeratorAccess(Long residentId, Authentication authentication) {

        UserEntity resident = userService.findUserById(residentId);

        List<ResidentialEntity> managerResidentialEntities = residentialEntityService.findResidentialEntitiesByManagerId(getUserEntity(authentication).getId());
        for (ResidentialEntity managerResidentialEntity : managerResidentialEntities) {
            List<ResidentialEntity> residentResidentialEntities = resident.getResidentialEntities();
            for (ResidentialEntity residentResidentialEntity : residentResidentialEntities) {
                if (managerResidentialEntity.getId().equals(residentResidentialEntity.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean checkMessageUserAccess(Long messageId, Authentication authentication) {
        Message message = messageService.findMessageById(messageId);

        return message != null && message.getReceiver().getId().equals(getUserEntity(authentication).getId());
    }

    /**
     * Validation of message sender.
     *
     * @param messageId      message id
     * @param senderId       sender id
     * @param authentication UserEntity
     * @return TRUE if message found and received id equals logged user. FALSE in other cases
     */
    @Override
    public boolean checkMessageSender(Long messageId, Long senderId, Authentication authentication) {
        Message message = messageService.findMessageById(messageId);
        return message != null && message.getReceiver().getId().equals(getUserEntity(authentication).getId());
    }

    /**
     * @param userId         id of the user that is going to be modified
     * @param authentication logged user data
     * @return TRUE if the id of the edited user equals the id of the logged user. FALSE if id do not match.
     */
    @Override
    public boolean checkProfileEditAccess(Long userId, Authentication authentication) {

        UserEntity user = userService.findUserById(userId);
        return user.getId().equals(getUserEntity(authentication).getId());
    }

    @Override
    public boolean checkPropertyFeeModeratorAccess(Long propertyFeeId, Authentication authentication) {
        PropertyFee propertyFee = propertyFeeService.findPropertyFeeById(propertyFeeId);
        return propertyFee.getProperty().getResidentialEntity().getManager().getId().equals(getUserEntity(authentication).getId());
    }

    @Override
    public boolean checkExpenseModeratorAccess(Long expenseId, Authentication authentication) {
        Expense expense = financialService.findById(expenseId);
        return expense.getResidentialEntity().getManager().getId().equals(getUserEntity(authentication).getId());
    }

    @Override
    public boolean checkExpenseUserAccess(Long expenseId, Authentication authentication) {

        Expense expense = financialService.findById(expenseId);

        UserEntity loggedUser = getUserEntity(authentication);

        //grant access if logged user has apartment in member of expense's residential entity
        List<ResidentialEntity> residentialEntities = loggedUser.getResidentialEntities();
        for (ResidentialEntity residentialEntity : residentialEntities) {

            //check if : Expense is in this RE
            if (residentialEntity.getId().equals(expense.getResidentialEntity().getId())
                    // AND (RE expenses are set to visible OR logged User is Moderator)
                    && (residentialEntity.isExpensesVisible() || residentialEntityService.checkIfUserIsResidentialEntityModerator(residentialEntity.getId(), loggedUser.getId()))) {
                return true;
            }
        }
        //grant access if logged user is residential entity manager
        return loggedUser.getId().equals(expense.getResidentialEntity().getManager().getId());
    }

    @Override
    public boolean checkMassMessagesUserAccess(Long userId, Authentication authentication) {
        return userId.equals(getUserEntity(authentication).getId());
    }

    private UserEntity getUserEntity(Authentication authentication) {
        return userService.findUserByEmail(authentication.getName());
    }

    @Override
    public boolean checkMessageSenderAndReceiver(Long propertyId, Long senderId, Long receivedId) {
        Property property = propertyService.findPropertyById(propertyId);

        if (property != null) {
            return property.getOwner().getId().equals(senderId) && property.getResidentialEntity().getManager().getId().equals(receivedId);
        }
        return false;
    }

    @Override
    public boolean checkPropertyOwnerAccess(Long propertyId, Authentication authentication) {
        Property property = propertyService.findPropertyById(propertyId);
        UserEntity loggedUser = getUserEntity(authentication);
        return property.getOwner().getId().equals(loggedUser.getId());
    }

    @Override
    public boolean checkPropertyOwnerAccessToFinancialData(Long propertyId, Authentication authentication) {
        Property property = propertyService.findPropertyById(propertyId);
        UserEntity loggedUser = getUserEntity(authentication);
        return property.getOwner().getId().equals(loggedUser.getId()) && property.isObtained();
    }


    @Override
    public boolean checkPropertyTypeModeratorAccess(Long propertyTypeId, Authentication authentication) {
        ResidentialEntity residentialEntityByPropertyType = propertyTypeService.findResidentialEntityByPropertyType(propertyTypeId);
        return residentialEntityByPropertyType != null && residentialEntityByPropertyType.getManager().getId().equals(getUserEntity(authentication).getId());
    }
}
