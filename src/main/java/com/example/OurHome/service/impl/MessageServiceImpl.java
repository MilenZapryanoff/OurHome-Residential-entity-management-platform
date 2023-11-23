package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.Message;
import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.repo.MessageRepository;
import com.example.OurHome.service.MessageService;
import com.example.OurHome.service.UserService;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    /**
     * Send a welcome message (notification) of each new user.
     */
    @Override
    public void sendRegistrationMessageToUser(UserEntity userEntity) {
        messageRepository.save(
                new Message(
                        LocalDate.now(),
                        Time.valueOf(LocalTime.now()),
                        "Thanks for your registration! To access your residential entity data you should first" +
                                " add a property. After your residential entity manager verifies your request " +
                                "you will get full access.",
                        userEntity,
                        false,
                        false));
    }

    /**
     * Send message (notification) to MANAGER of RE when new property registration
     * happens.
     */
    @Override
    public void propertyRegistrationMessageToManager(ResidentialEntity residentialEntity) {
        messageRepository.save(
                new Message(
                        LocalDate.now(),
                        Time.valueOf(LocalTime.now()),
                        "New property registration in Residential entity ID: "
                                + residentialEntity.getId() + ". You can access the request via Administration panel",
                        residentialEntity.getManager(),
                        false,
                        false));
    }

    /**
     * Send message (notification) to MANAGER of RE when property modification performed by resident
     */
    @Override
    public void propertyModificationMessageToManager(Property property) {
        messageRepository.save(
                new Message(
                        LocalDate.now(),
                        Time.valueOf(LocalTime.now()),
                        "Data change for property № " + property.getNumber() + " in Residential entity ID: "
                                + property.getResidentialEntity().getId() + ". You can access the request via Administration panel",
                        property.getResidentialEntity().getManager(),
                        false,
                        false));
    }

    /**
     * Send message (notification) to RESIDENT when manager changes property data
     */
    @Override
    public void propertyModificationMessageToResident(Property property) {
        messageRepository.save(
                new Message(
                        LocalDate.now(),
                        Time.valueOf(LocalTime.now()),
                        "Your Residential entity manager made changes for property №" + property.getNumber() + ". " +
                                "You can track changes in Property section",
                        property.getOwner(),
                        false,
                        false));
    }

    /**
     * Send message (notification) to the RESIDENT, when his role has been changed to Moderator of RE.
     *
     * @param userEntity        carries information about the resident
     * @param residentialEntity carries information about the RE
     */
    @Override
    public void newModeratorMessage(UserEntity userEntity, ResidentialEntity residentialEntity) {
        messageRepository.save(
                new Message(
                        LocalDate.now(),
                        Time.valueOf(LocalTime.now()),
                        "You have been promoted as Moderator of Residential entity ID: "
                                + residentialEntity.getId() + ". You can access all the data related to this Residential entity via the Administration section",
                        userEntity,
                        false,
                        false));
    }

    /**
     * Send message (notification) to the OWNER, when his property registration request is approved by RE Manager.
     *
     * @param property carries information about the property. Allows message extend.
     */
    @Override
    public void propertyApprovedMessage(Property property) {
        messageRepository.save(
                new Message(
                        LocalDate.now(),
                        Time.valueOf(LocalTime.now()),
                        "Your registration/modification request for property № " + property.getNumber() + " id Residential entity ID: "
                                + property.getResidentialEntity().getId() + " has been APPROVED. You can now access your data and reports.",
                        property.getOwner(),
                        false,
                        false));
    }

    /**
     * Send message (notification) to the OWNER, when his property is REJECTED from RE.
     *
     * @param property carries information about the property. Allows message extend.
     */
    @Override
    public void propertyRejectedMessage(Property property) {
        messageRepository.save(
                new Message(
                        LocalDate.now(),
                        Time.valueOf(LocalTime.now()),
                        "Your registration request for property № " + property.getNumber() + " id Residential entity ID: "
                                + property.getResidentialEntity().getId() + " has been REJECTED. You can contact your Residential entity manager for more details " +
                                "about the reason for this action. If you still have an access to the Residential entity, You can edit the record and submit " +
                                "it again.",
                        property.getOwner(),
                        false,
                        false));
    }

    /**
     * Send message (notification) to the OWNER, when his property is DELETED from RE.
     *
     * @param property carries information about the property. Allows message extend.
     */
    @Override
    public void propertyDeletedMessageToOwner(Property property) {
        messageRepository.save(
                new Message(
                        LocalDate.now(),
                        Time.valueOf(LocalTime.now()),
                        "Your property № " + property.getNumber() + " has been REMOVED from Residential entity ID: "
                                + property.getResidentialEntity().getId() + ". You can contact your Residential entity manager for more details " +
                                "about the reason for this action. If you still have an access to the Residential entity, You can submit new " +
                                "property registration request.",
                        property.getOwner(),
                        false,
                        false));
    }

    @Override
    public void propertyDeletedMessageToManager(Property property) {
        messageRepository.save(
                new Message(
                        LocalDate.now(),
                        Time.valueOf(LocalTime.now()),
                        "Property № " + property.getNumber() + " has been REMOVED from Residential entity ID: "
                                + property.getResidentialEntity().getId() + " by his owner.",
                        property.getResidentialEntity().getManager(),
                        false,
                        false));
    }


    /**
     * Mark message as read method
     *
     * @param id - message id
     */
    @Override
    public void readMessage(Long id) {
        Message message = messageRepository.findById(id).orElse(null);
        if (message != null) {
            message.setRead(true);
            messageRepository.save(message);
        }
    }

    /**
     * Archive message method
     *
     * @param id - message id
     */
    @Override
    public void archiveMessage(Long id) {
        Message message = messageRepository.findById(id).orElse(null);
        if (message != null) {
            message.setArchived(true);
            message.setRead(true);
            messageRepository.save(message);
        }
    }

    /**
     * Delete message method
     *
     * @param id - message id
     */
    @Override
    public void deleteMessage(Long id) {
        messageRepository.deleteById(id);
    }

    /**
     * Delete ALL messages method
     *
     * @param id - UserEntity id
     */
    @Override
    public void deleteAllMessages(Long id) {
        List<Message> notArchivedMessages = messageRepository.findArchivedMessagesByUserId(id);
        if (notArchivedMessages != null && !notArchivedMessages.isEmpty()) {
            for (Message message : notArchivedMessages) {
                messageRepository.deleteById(message.getId());
            }
        }
    }

    /**
     * Read ALL messages method
     *
     * @param id - UserEntity id
     */
    @Override
    public void readAllMessages(Long id) {
        List<Message> notArchivedMessages = messageRepository.findNotArchivedMessagesByUserId(id);
        if (notArchivedMessages != null && !notArchivedMessages.isEmpty()) {
            for (Message message : notArchivedMessages) {
                message.setRead(true);
                messageRepository.save(message);
            }
        }
    }

    /**
     * Archive ALL messages method
     *
     * @param id - UserEntity id
     */
    @Override
    public void archiveAllMessages(Long id) {
        List<Message> notArchivedMessages = messageRepository.findNotArchivedMessagesByUserId(id);
        if (notArchivedMessages != null && !notArchivedMessages.isEmpty()) {
            for (Message message : notArchivedMessages) {
                message.setArchived(true);
                message.setRead(true);
                messageRepository.save(message);
            }
        }
    }

    /**
     * User-to-user message
     * @param receiver UserEntity
     * @param sender UserEntity
     * @param message message body
     */
    @Override
    public void sendMessage(UserEntity receiver, UserEntity sender, String message) {
        messageRepository.save(
                new Message(
                        LocalDate.now(),
                        Time.valueOf(LocalTime.now()),
                        message,
                        receiver,
                        sender,
                        false,
                        false));
    }

    @Override
    public Message findMessageById(Long id) {
        return messageRepository.findById(id).orElse(null);
    }
}


