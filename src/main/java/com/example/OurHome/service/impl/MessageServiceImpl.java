package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.Message;
import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.ReportBug.ReportBugBindingModel;
import com.example.OurHome.repo.MessageRepository;
import com.example.OurHome.service.MessageService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private static final String WELCOME_MSG_USER_BG = "Благодарим Ви, за регистрацията в платформата OurHome! За да получите достъп до Вашата дигитална етажна собственост първо трябва да се регистрирате, като собственик на имот. След като Вашата регистрация бъде завършена ще получите пълен достъп до наличната информация!";
    private static final String WELCOME_MSG_USER_ENG = "Thanks for your registration! To access your digital Condominium data you should first add a property. After your Condominium manager verifies your request you will get full access.";
    private static final String PENDING_REGISTRATION_BG = "Нова заявка за регистрация на дигитален имот, която изисква верификация от Ваша страна! Можете да достъпите заявката през меню Администрация -> Потребители -> Чакащи заявки";
    private static final String PENDING_REGISTRATION_ENG = "New digital property registration needs you verification! You can access the request via Administration panel";
    private static final String SUCCESS_REGISTRATION_BG = "Нова успешна регистрация на имот във Вашата дигитална етажна собственост! Заявката не изисква допълнителна верификация от Ваша страна!";
    private static final String SUCCESS_REGISTRATION_ENG = "New successful digital property registration!. Request is auto-confirmed as there is no data change in the registration request.No action needed from your side!";
    private static final String PROMOTED_MODERATOR_BG = "Вие получихте роля Модератор на етажна собственост. Този достъп Ви осигурява достъп до допълнителна информация и функционалности свързани с Вашата дигитална етажна собственост!";
    private static final String PROMOTED_MODERATOR_ENG = "You have been promoted as Moderator of Condominium. You can access all the data related to this Condominium via the Administration section!";


    private final MessageRepository messageRepository;

    public MessageServiceImpl(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    /**
     * Send a welcome message (notification) of each new user.
     */
    @Override
    public void sendRegistrationMessageToUser(UserEntity userEntity) {

        Message message = createMessage();
        message.setReceiver(userEntity);

        if (userEntity.getLanguage().getDescription().equals("bulgarian")) {
            message.setText("Здравейте, " + userEntity.getFirstName() + "! " + WELCOME_MSG_USER_BG);
        } else if (userEntity.getLanguage().getDescription().equals("english")) {
            message.setText("Hello, " + userEntity.getFirstName() + "! " + WELCOME_MSG_USER_ENG);
        }

        messageRepository.save(message);
    }

    private static Message createMessage() {
        Message message = new Message();
        message.setDate(LocalDate.now());
        message.setTime(Time.valueOf(LocalTime.now()));
        message.setRead(false);
        message.setArchived(false);
        return message;
    }

    /**
     * Send message (notification) to MANAGER of RE when new property (MANUAL-CONFIRM) registration happens.
     * happens.
     */
    @Override
    public void propertyPendingRegistrationMessageToManager(ResidentialEntity residentialEntity) {


        Message message = createMessage();

        UserEntity manager = residentialEntity.getManager();
        message.setReceiver(manager);

        if (manager.getLanguage().getDescription().equals("bulgarian")) {
            message.setText("Етажна собственост " + residentialEntity.getId() + ": " +  PENDING_REGISTRATION_BG);
        } else if (manager.getLanguage().getDescription().equals("english")) {
            message.setText("Condominium " + residentialEntity.getId() + ": " +  PENDING_REGISTRATION_ENG);
            message.setText(PENDING_REGISTRATION_ENG);
        }

        messageRepository.save(message);
    }

    /**
     * Send message (notification) to MANAGER of RE when new property (AUTO-CONFIRM) registration happens.
     * happens.
     */
    @Override
    public void propertyRegistrationMessageToManager(ResidentialEntity residentialEntity) {

        Message message = createMessage();

        UserEntity manager = residentialEntity.getManager();
        message.setReceiver(manager);

        if (manager.getLanguage().getDescription().equals("bulgarian")) {

            message.setText("Етажна собственост " + residentialEntity.getId() + ": " +  SUCCESS_REGISTRATION_BG);
        } else if (manager.getLanguage().getDescription().equals("english")) {
            message.setText("Condominium " + residentialEntity.getId() + ": " +  SUCCESS_REGISTRATION_ENG);
        }

        messageRepository.save(message);
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
                        "Data change for property № " + property.getNumber() + " in Condominium ID: "
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
                        "Your Condominium manager made changes for property №" + property.getNumber() + ". " +
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

        Message message = createMessage();

        UserEntity manager = residentialEntity.getManager();
        message.setReceiver(userEntity);
        if (userEntity.getLanguage().getDescription().equals("bulgarian")) {
            message.setText("Здравейте, " + userEntity.getFirstName() + "! " + PROMOTED_MODERATOR_BG);
        } else if (userEntity.getLanguage().getDescription().equals("english")) {
            message.setText("Hello, " + userEntity.getFirstName() + "! " + PROMOTED_MODERATOR_ENG);
        }
        messageRepository.save(message);
    }

    /**
     * Send message (notification) to the OWNER, when his property registration request is approved by RE Manager.
     *
     * @param property carries information about the property. Allows message extend.
     */
    @Override
    public void propertyRegistrationApprovedMessage(Property property) {
        messageRepository.save(
                new Message(
                        LocalDate.now(),
                        Time.valueOf(LocalTime.now()),
                        "Your registration/modification request for property № " + property.getNumber() + " id Condominium ID: "
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
                        "Your registration request for property № " + property.getNumber() + " id Condominium ID: "
                                + property.getResidentialEntity().getId() + " has been REJECTED. You can contact your Condominium manager for more details " +
                                "about the reason for this action. If you still have an access to the Condominium, You can edit the record and submit " +
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
                        "Your property № " + property.getNumber() + " has been REMOVED from Condominium ID: "
                                + property.getResidentialEntity().getId() + ". You can contact your Condominium manager for more details " +
                                "about the reason for this action. If you still have an access to the Condominium, You can submit new " +
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
                        "Registration request for property № " + property.getNumber() + " in Condominium ID: "
                                + property.getResidentialEntity().getId() + " has been Canceled by user.",
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
            messageRepository.deleteAll(notArchivedMessages);
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
            notArchivedMessages.forEach(message -> message.setRead(true));
            messageRepository.saveAll(notArchivedMessages);
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
            notArchivedMessages.forEach(message -> {
                message.setArchived(true);
                message.setRead(true);
            });
            messageRepository.saveAll(notArchivedMessages);
        }
    }

    /**
     * User-to-user message
     *
     * @param receiver UserEntity
     * @param sender   UserEntity
     * @param message  message body
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

    /**
     * New message to property owner for new monthly fee.
     *
     * @param property   Property
     * @param monthlyFee Monthly fee amount
     * @param dueAmount  Total due amount
     */
    @Override
    public void newFeeMessageToPropertyOwner(Property property, BigDecimal monthlyFee, BigDecimal dueAmount) {

        Month month = LocalDate.now().getMonth();
        int year = LocalDate.now().getYear();

        String messageText;

        if (dueAmount != null) {
            messageText = "You have new monthly fee for " + month + " " + year + " for the amount of " +
                    monthlyFee + "лв. for property № " + property.getNumber() + "." +
                    "\n" +
                    "You can check details in your Property section." +
                    "Total due amount for your property is " + dueAmount + " лв.";
        } else {
            messageText = "You have new monthly fee for " + month + " " + year + " for the amount of " +
                    monthlyFee + "лв. for property № " + property.getNumber() + "." +
                    "\n" +
                    "You can check details in your Property section." +
                    "You have no due amount";
        }


        messageRepository.save(
                new Message(
                        LocalDate.now(),
                        Time.valueOf(LocalTime.now()),
                        messageText,
                        property.getOwner(),
                        false,
                        false));
    }

    @Override
    public void propertyRegistrationApprovedWithNoChangesMessage(Property property) {
        messageRepository.save(
                new Message(
                        LocalDate.now(),
                        Time.valueOf(LocalTime.now()),
                        "Your registration request for property № " + property.getNumber() + " in Condominium ID: "
                                + property.getResidentialEntity().getId() + " has been APPROVED. Please note, that there " +
                                "was a difference between your input data and the preset data for this property. " +
                                " Your input data was ignored and no changes were made for this property." +
                                "You can now access your property data and reports.",
                        property.getOwner(),
                        false,
                        false));
    }

    @Override
    public void propertyChangeRequestApproved(Property property) {
        messageRepository.save(
                new Message(
                        LocalDate.now(),
                        Time.valueOf(LocalTime.now()),
                        "Your change request for property № " + property.getNumber() + " in Condominium ID: "
                                + property.getResidentialEntity().getId() + " has been APPROVED. Changes applied to your property!",
                        property.getOwner(),
                        false,
                        false));
    }

    @Override
    public void propertyChangeRequestRejected(Property property) {
        messageRepository.save(
                new Message(
                        LocalDate.now(),
                        Time.valueOf(LocalTime.now()),
                        "Your change request for property № " + property.getNumber() + " in Condominium ID: "
                                + property.getResidentialEntity().getId() + " has been REJECTED. Your changes will not take affect. You can still send new change request if needed!",
                        property.getOwner(),
                        false,
                        false));
    }

    @Override
    public Message findMessageById(Long id) {
        return messageRepository.findById(id).orElse(null);
    }
}


