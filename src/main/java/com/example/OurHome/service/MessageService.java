package com.example.OurHome.service;

import com.example.OurHome.model.Entity.Message;
import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;

import java.math.BigDecimal;

public interface MessageService {
    void sendRegistrationMessageToUser(UserEntity newUserEntity);

    void deleteMessage(Long id);

    void newModeratorMessage(UserEntity userEntity, ResidentialEntity residentialEntity);

    void propertyRegistrationMessageToManager(ResidentialEntity residentialEntity);

    void propertyModificationMessageToManager(Property property);

    void propertyModificationMessageToResident(Property property);

    void propertyApprovedMessage(Property property);

    void propertyRejectedMessage(Property property);

    void propertyDeletedMessageToOwner(Property property);

    void propertyDeletedMessageToManager(Property property);

    void readMessage(Long id);

    void archiveMessage(Long id);

    void deleteAllMessages(Long id);

    void readAllMessages(Long id);

    void archiveAllMessages(Long id);

    Message findMessageById(Long id);

    void sendMessage(UserEntity receiver, UserEntity sender, String message);

    void newFeeMessageToPropertyOwner(Property property, BigDecimal monthlyFee, BigDecimal dueAmount);
}
