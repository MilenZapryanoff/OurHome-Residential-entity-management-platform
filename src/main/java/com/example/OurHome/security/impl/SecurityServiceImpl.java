package com.example.OurHome.security.impl;

import com.example.OurHome.model.Entity.Message;
import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.security.SecurityService;
import com.example.OurHome.service.MessageService;
import com.example.OurHome.service.PropertyService;
import com.example.OurHome.service.ResidentialEntityService;
import com.example.OurHome.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("securityService")
public class SecurityServiceImpl implements SecurityService {

    private final UserService userService;
    private final ResidentialEntityService residentialEntityService;
    private final PropertyService propertyService;
    private final MessageService messageService;

    public SecurityServiceImpl(UserService userService, ResidentialEntityService residentialEntityService, PropertyService propertyService, MessageService messageService) {
        this.userService = userService;
        this.residentialEntityService = residentialEntityService;
        this.propertyService = propertyService;
        this.messageService = messageService;
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
     * @return boolean
     */
    @Override
    public boolean checkMessageSender(Long messageId, Long senderId, Authentication authentication) {
        Message message = messageService.findMessageById(messageId);
        return message != null && message.getReceiver().getId().equals(getUserEntity(authentication).getId());
    }

    @Override
    public boolean checkMassMessagesUserAccess(Long userId, Authentication authentication) {
        return userId.equals(getUserEntity(authentication).getId());
    }

    private UserEntity getUserEntity(Authentication authentication) {
        return userService.findUserByEmail(authentication.getName());
    }

    public boolean checkMessageSenderAndReceiver(Long propertyId, Long senderId, Long receivedId) {
        Property property = propertyService.findPropertyById(propertyId);

        if (property != null) {
            return property.getOwner().getId().equals(senderId) && property.getResidentialEntity().getManager().getId().equals(receivedId);
        }
        return false;
    }
}
