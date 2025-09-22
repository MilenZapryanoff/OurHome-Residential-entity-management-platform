package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.*;
import com.example.OurHome.repo.MessageRepository;
import com.example.OurHome.service.LogService;
import com.example.OurHome.service.MessageService;
import com.example.OurHome.service.NotificationService;
import com.example.OurHome.service.email.EmailService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private final NotificationService notificationService;
    private static final String WELCOME_MSG_USER_BG = "Благодарим Ви за регистрацията в платформата OurHome! За да получите достъп до Вашата дигитална етажна собственост първо трябва да заявите достъп до информация на самостоятелен обект. След като Вашата регистрация бъде завършена ще получите пълен достъп до наличната информация!";
    private static final String WELCOME_MSG_USER_ENG = "Thanks for your registration! To access your digital Condominium data you should first add a property. After your Condominium manager verifies your request you will get full access.";
    private static final String PENDING_REGISTRATION_BG = "Нова заявка за регистрация на дигитален имот, която изисква верификация от Ваша страна! Можете да достъпите заявката през меню Администрация -> Потребители -> Чакащи заявки";
    private static final String PENDING_REGISTRATION_ENG = "New digital property registration needs you verification! You can access the request via Administration panel";
    private static final String SUCCESS_REGISTRATION_BG = "Нова успешна регистрация на имот във Вашата дигитална етажна собственост! Заявката не изисква допълнителна верификация от Ваша страна!";
    private static final String SUCCESS_REGISTRATION_ENG = "New successful digital property registration!. Request is auto-confirmed as there is no data change in the registration request.No action needed from your side!";
    private static final String PROMOTED_MODERATOR_BG = "Вие получихте роля Модератор на етажна собственост. Този достъп Ви осигурява достъп до допълнителна информация и функционалности свързани с Вашата дигитална етажна собственост!";
    private static final String PROMOTED_MODERATOR_ENG = "You have been promoted as Moderator of Condominium. You can access all the data related to this Condominium via the Administration section!";
    private static final String EVENT_MSG_BG = "Добавено е ново събитие в календара на Вашата етажна собственост. Информация за събитието ще откриете в меню Събития в административния панел!";
    private static final String EVENT_MSG_ENG = "A new event has been added to the calendar of your condominium. You can find information about the event in the Events menu in the administrative panel!";

    private final MessageRepository messageRepository;
    private final EmailService emailService;
    private final LogService logService;

    public MessageServiceImpl(NotificationService notificationService, MessageRepository messageRepository, EmailService emailService, LogService logService) {
        this.notificationService = notificationService;
        this.messageRepository = messageRepository;
        this.emailService = emailService;
        this.logService = logService;
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
        try {
            Message message = messageRepository.findById(id).orElse(null);
            if (message != null) {
                message.setArchived(true);
                message.setRead(true);
                messageRepository.save(message);
            }
            logService.info("✅[archiveMessage] Message id:{} successfully ARCHIVED!", id);
        } catch (Exception e) {
            logService.error("❌[archiveMessage] Failed to archive message id:{}! {}", id, e);
        }


    }

    /**
     * Delete message method
     *
     * @param id - message id
     */
    @Override
    public void deleteMessage(Long id) {
        try {
            messageRepository.deleteById(id);
            logService.info("✅[deleteMessage] Message id:{} successfully DELETED!", id);
        } catch (Exception e) {
            logService.error("❌[deleteMessage] Failed to delete message id:{}! {}", id, e);
        }
    }

    /**
     * Delete ALL messages method
     *
     * @param id - UserEntity id
     */
    @Override
    public void deleteAllMessages(Long id) {
        try {
            List<Message> notArchivedMessages = messageRepository.findArchivedMessagesByUserId(id);
            if (notArchivedMessages != null && !notArchivedMessages.isEmpty()) {
                messageRepository.deleteAll(notArchivedMessages);
            }
            logService.info("✅[deleteAllMessages] All messages of user:{} successfully DELETED!", id);
        } catch (Exception e) {
            logService.error("❌[deleteAllMessages] Failed to DELETE all messages of user:{}! {}", id, e);
        }
    }

    /**
     * Read ALL messages method
     *
     * @param id - UserEntity id
     */
    @Override
    public void readAllMessages(Long id) {
        try {
            List<Message> notArchivedMessages = messageRepository.findNotArchivedMessagesByUserId(id);

            if (notArchivedMessages != null && !notArchivedMessages.isEmpty()) {
                notArchivedMessages.forEach(message -> message.setRead(true));
                messageRepository.saveAll(notArchivedMessages);
            }
            logService.info("✅[readAllMessages] All messages of user:{} successfully MARKED as read!", id);
        } catch (Exception e) {
            logService.error("❌[readAllMessages] Failed to mark all messages of user:{} as read! {}", id, e);
        }
    }

    /**
     * Archive ALL messages method
     *
     * @param id - UserEntity id
     */
    @Override
    public void archiveAllMessages(Long id) {
        try {
            List<Message> notArchivedMessages = messageRepository.findNotArchivedMessagesByUserId(id);

            if (notArchivedMessages != null && !notArchivedMessages.isEmpty()) {
                notArchivedMessages.forEach(message -> {
                    message.setArchived(true);
                    message.setRead(true);
                });
                messageRepository.saveAll(notArchivedMessages);
            }
            logService.info("✅[archiveAllMessages] All messages of user:{} successfully ARCHIVED!", id);
        } catch (Exception e) {
            logService.error("❌[archiveAllMessages] Failed to archive all messages of user: {}! {}", id, e);
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
        try {
            messageRepository.save(
                    new Message(
                            LocalDate.now(),
                            Time.valueOf(LocalTime.now()),
                            message,
                            receiver,
                            sender,
                            false,
                            false));
            logService.info("✅[sendMessage ->] New message successfully created. Receiver id: {}, Sender id: {}", receiver.getId(), sender.getId());
        } catch (Exception e) {
            logService.error("❌[sendMessage->] Failed to create new message! Receiver id: {}, Sender id: {}", receiver.getId(), sender.getId());
            return;
        }

        //send message receiver notification
        notificationService.newMessageNotification(receiver);
        //send message receiver email
        if (receiver.isMessageEmail()) {
            emailService.newMessageEmailNotification(receiver, sender, message);
        }
    }

    /**
     * Send SYSTEM welcome message to each new user.
     */
    @Override
    public void sendRegistrationMessageToUser(UserEntity userEntity) {

        Message message = createMessage(userEntity);
        message.setText("Здравейте, " + userEntity.getFirstName() + "! " + WELCOME_MSG_USER_BG);
        message.setTextEn("Hello, " + userEntity.getFirstName() + "! " + WELCOME_MSG_USER_ENG);

        messageRepository.save(message);
    }

    /**
     * Send SYSTEM message (+ notification) to MANAGER of RE when new property (MANUAL-CONFIRM) registration happens.
     */
    @Override
    public void propertyPendingRegistrationMessageToManager(ResidentialEntity residentialEntity) {

        Message message = createMessage(residentialEntity.getManager());
        message.setText("Етажна собственост " + residentialEntity.getId() + ": " + PENDING_REGISTRATION_BG);
        message.setTextEn("Condominium " + residentialEntity.getId() + ": " + PENDING_REGISTRATION_ENG);
        messageRepository.save(message);

        //send notification to Condominium manager for pending registration approval
        notificationService.propertyPendingRegistrationMessageToManager(residentialEntity);
    }

    /**
     * Send SYSTEM message to MANAGER of RE when new property (AUTO-CONFIRM) registration happens.
     * happens.
     */
    @Override
    public void propertyRegistrationMessageToManager(ResidentialEntity residentialEntity) {

        Message message = createMessage(residentialEntity.getManager());
        message.setText("Етажна собственост " + residentialEntity.getId() + ": " + SUCCESS_REGISTRATION_BG);
        message.setTextEn("Condominium " + residentialEntity.getId() + ": " + SUCCESS_REGISTRATION_ENG);

        messageRepository.save(message);
    }

    /**
     * Send SYSTEM message (+ notification) to MANAGER of RE when property modification performed by resident
     */
    @Override
    public void propertyModificationMessageToManager(Property property) {

        Message message = createMessage(property.getResidentialEntity().getManager());
        message.setText("Получена заявка за промяна на параметри по сомостоятелен обект №" + property.getNumber() + " в състава на етажна собственост с идентификатор "
                + property.getResidentialEntity().getId() + ". Информация за заявката е налична в меню Администриране!");
        message.setTextEn("Data change for property № " + property.getNumber() + " in Condominium ID: "
                + property.getResidentialEntity().getId() + ". You can access the request via Administration panel!");

        messageRepository.save(message);

        //send notification to Condominium manager for pending property change-request
        notificationService.propertyModificationNotificationToManager(property);
    }

    /**
     * Send SYSTEM message (+ notification) to RESIDENT when manager changes property data
     */
    @Override
    public void propertyModificationMessageToOwner(Property property) {

        Message message = createMessage(property.getOwner());
        message.setText("Извършена е промяна в параметрите на самостоятелен обект №" + property.getNumber() + " в състава на етажна собственост с идентификатор: " + property.getResidentialEntity().getId() +
                ". Актуалини данни за вашия имот може да откриете в меню Моят имот");
        message.setTextEn("A data change has been made for  individual property No. " + property.getNumber() +
                " within the condominium property with ID: " + property.getResidentialEntity().getId() +
                ". You can find more information in the 'My Property' menu.");
        messageRepository.save(message);

        //send notification to property owner for property data change
        notificationService.propertyModificationNotificationToOwner(property);
    }

    /**
     * Send SYSTEM message (notification) to the RESIDENT, when his role has been changed to Moderator of RE.
     *
     * @param userEntity        carries information about the resident
     * @param residentialEntity carries information about the RE
     */
    @Override
    public void newModeratorMessage(UserEntity userEntity, ResidentialEntity residentialEntity) {

        Message message = createMessage(userEntity);
        message.setText("Здравейте, " + userEntity.getFirstName() + "! " + PROMOTED_MODERATOR_BG);
        message.setTextEn("Hello, " + userEntity.getFirstName() + "! " + PROMOTED_MODERATOR_ENG);

        messageRepository.save(message);
    }

    /**
     * Send SYSTEM message (+ notification) to the OWNER, when his property registration request is approved by RE Manager.
     *
     * @param property carries information about the property. Allows message extend.
     */
    @Override
    public void propertyRegistrationApprovedMessage(Property property) {

        Message message = createMessage(property.getOwner());
        message.setText("Вашата заявка за достъп до информация за самостоятелен обект № " + property.getNumber() + " в състава на етажна собственост с идентификатор: "
                + property.getResidentialEntity().getId() + " е ОДОБРЕНА! Разполагате с пълен достъп до информация касаеща Вашия самостоятелен обект и етажна собственост!");
        message.setTextEn("Your data access request for property № " + property.getNumber() + " in Condominium ID: "
                + property.getResidentialEntity().getId() + " has been APPROVED! You can now access your data and reports.");

        messageRepository.save(message);

        //send notification to property owner for property approval
        notificationService.propertyRegistrationApprovedNotification(property);
    }

    /**
     * Send SYSTEM message (+ notification) to OWNER when property change-request is APPROVED (with no changes)
     *
     * @param property hold the information about the property
     */
    @Override
    public void propertyRegistrationApprovedWithNoChangesMessage(Property property) {
        Message message = createMessage(property.getOwner());

        message.setText("Вашата заявка за достъп до информация за самостоятелен обект № " + property.getNumber() + ", част от етажна собственост с идентификатор: "
                + property.getResidentialEntity().getId() + " е ОДОБРЕНА. Установено е разминаване между данните от вашата заявка и тези, дефинирани в системата за този имот. " +
                "Регистрацията Ви е одобрена, без да се взимат предвид данните от Вашата заявка! В случай на необходимост, може да проверите несъответствията с " +
                "домоуправителя. При необходимост да изпратите заявка за промяна на параметри!");
        message.setTextEn("Your data access request for property № " + property.getNumber() + " in Condominium ID: "
                + property.getResidentialEntity().getId() + " has been APPROVED. Please note that there was a discrepancy " +
                "between the data you provided and the system's preset data for this property. " +
                "Your input data has been ignored, and no changes have been made to the system records. " +
                "If necessary, you can verify the inconsistencies with the condominium manager or submit a request for data correction.");

        messageRepository.save(message);

        //send notification to property owner for property approval
        notificationService.propertyRegistrationApprovedNotification(property);
    }


    /**
     * Send SYSTEM message (+ notification) to the OWNER, when his property is REJECTED from RE.
     *
     * @param property carries information about the property. Allows message extend.
     */
    @Override
    public void propertyRejectedMessage(Property property) {
        try {
            Message message = createMessage(property.getOwner());
            message.setText("Вашата заявка за достъп до самостоятелен обект  № " + property.getNumber() + " в състава на етажна собственост с идентификатор : "
                    + property.getResidentialEntity().getId() + " е ОТХВЪРЛЕНА! За да разберете причината за това, моля да се свържите с Вашия домоуправител. " +
                    "В случай, че все още имате достъп до етажната собственост, може да коригирате и изпратите повторно заявката за регистрация!");
            message.setTextEn("Your data access request for property № " + property.getNumber() + " in Condominium ID: "
                    + property.getResidentialEntity().getId() + " has been REJECTED. You can contact your Condominium manager for more details " +
                    "about the reason for this action. If you still have an access to the Condominium, You can edit the record and submit " +
                    "it again.");

            messageRepository.save(message);

            logService.info("✅[-> propertyRejectedMessage] Registration rejection MESSAGE to user:{}, owner of property: {}", property.getOwner().getId(), property.getId());
        } catch (Exception e) {
            logService.error("❌[-> propertyRejectedMessage] Failed to sent registration rejection MESSAGE to user:{}, owner of property: {}! {}", property.getOwner().getId(), property.getId(), e);
        }
    }

    /**
     * Send message to the OWNER, when manager REMOVES property owner!
     *
     * @param property carries information about the property. Allows message extend.
     */
    @Override
    public void ownerRemovedMessageToOwner(Property property, UserEntity currentOwner) {

        Message message = createMessage(currentOwner);

        message.setText("Вашият достъп до информация за самостоятелен обект № " + property.getNumber() + " в състатва на етажна собственост с идентификатор: "
                + property.getResidentialEntity().getId() + " е преустановен. За информация относно причината за това действие, моля да се обърнете към домоуправителя на дигиталната етажна собственост!");
        message.setTextEn("Your access to individual unit No." + property.getNumber() + " in Condominium ID: "
                + property.getResidentialEntity().getId() + " has been removed. You can contact your Condominium manager for more details " +
                "about the reason for this action!");
        messageRepository.save(message);
    }


    /**
     * Send message to the OWNER, when his property is DELETED from RE.
     *
     * @param property carries information about the property. Allows message extend.
     */
    @Override
    public void propertyDeletedMessageToOwner(Property property) {

        Message message = createMessage(property.getOwner());

        message.setText("Самостоятелен обект № " + property.getNumber() + " е премахнат от етажна собственост с идентификатор: "
                + property.getResidentialEntity().getId() + ". За информация относно за причината за това действие, моля да се обърнете към домоуправителя на дигиталната етажна собственост!");
        message.setTextEn("Your property № " + property.getNumber() + " has been REMOVED from Condominium ID: "
                + property.getResidentialEntity().getId() + ". You can contact your Condominium manager for more details " +
                "about the reason for this action. If you still have an access to the Condominium, You can submit new " +
                "property registration request.");
        messageRepository.save(message);
    }


    /**
     * Send SYSTEM message to MANAGER, when property owner unlinks property from his profile
     *
     * @param property carries information about the property.
     */
    @Override
    public void propertyDeletedMessageToManager(Property property, UserEntity currentOwner) {

        String ownerName = currentOwner.getFirstName() + " " + currentOwner.getLastName();

        Message message = createMessage(property.getResidentialEntity().getManager());
        message.setText("Отказ на потребител " + ownerName + " от самостоятелен обект №" + property.getNumber() + " в състава на етажна собственост с идентификатор: "
                + property.getResidentialEntity().getId() + ". До регистрацията на потребител, данните за този имот остават видими единствено за вас!");
        message.setTextEn("User " + ownerName + " has withdrawn from the individual property No. " + property.getNumber() +
                " within the condominium ID: " + property.getResidentialEntity().getId() +
                ". Until an user is registered, the data for this property will remain visible only to you!");

        messageRepository.save(message);
    }


    /**
     * Send SYSTEM message (+ notification) to OWNER when property change-request is APPROVED
     *
     * @param property hold the information about the property
     */
    @Override
    public void propertyChangeRequestApprovedMessage(Property property) {
        try {
            Message message = createMessage(property.getOwner());
            message.setText("Вашата заявка за промяна на параметри по самостоятелен обект № " + property.getNumber() + " в състава на етажна собственост с идентификатор: "
                    + property.getResidentialEntity().getId() + " е одобрена. Промените са приложени!");
            message.setTextEn("Your change request for property № " + property.getNumber() + " in Condominium ID: "
                    + property.getResidentialEntity().getId() + " has been APPROVED. Changes applied to your property!");

            messageRepository.save(message);
            logService.info("✅[-> propertyChangeRequestApprovedMessage] Approve change request MESSAGE for property id: {} sent", property.getId());
        } catch (Exception e) {
            logService.error("❌[-> propertyChangeRequestApprovedMessage] Failed to send change request approve MESSAGE for property id: {}! {}", property.getId(), e);
        }
    }

    /**
     * Send SYSTEM message (+ notification) to OWNER when property change-request is REJECTED
     *
     * @param property hold the information about the property
     */
    @Override
    public void propertyChangeRequestRejectedMessage(Property property) {

        try {
            Message message = createMessage(property.getOwner());
            message.setText("Вашата заявка за промяна на параметри по самостоятелен обект № " + property.getNumber() + " в състава на етажна собственост с идентификатор: "
                    + property.getResidentialEntity().getId() + " е отхвърлена. Направените от Вас променя няма да влязат в сила! Може да изпратите повторна заявка в случай, че това е необходимо!");
            message.setTextEn("Your change request for property № " + property.getNumber() + " in Condominium ID: "
                    + property.getResidentialEntity().getId() + " has been REJECTED. Your changes will not take affect. You can still send new change request if needed!");

            messageRepository.save(message);

            logService.info("✅[-> propertyChangeRequestRejectedMessage] Rejected change request MESSAGE for property id: {} sent", property.getId());
        } catch (Exception e) {
            logService.error("❌[-> propertyChangeRequestRejectedMessage] Failed to send change request reject MESSAGE for property id: {}! {}", property.getId(), e);
        }
    }


    /**
     * New message (+ notification) to property OWNER for new monthly fee.
     *
     * @param property       Property
     * @param monthlyFee     Monthly fee amount
     * @param totalDueAmount Total due amount
     */
    @Override
    public void newFeeMessageToPropertyOwner(Property property, BigDecimal monthlyFee, BigDecimal totalDueAmount) {

        UserEntity propertyOwner = property.getOwner();

        try {
            Month month = LocalDate.now().getMonth();
            int year = LocalDate.now().getYear();

            String messageText;
            String messageTextEn;
            Message message = createMessage(propertyOwner);

            if (totalDueAmount != null) {
                messageText = "Имате нова месечна такса за " + month + " " + year + " за сумата от " +
                        monthlyFee + " EUR за самостоятелн обект № " + property.getNumber() + "." +
                        "\n" +
                        "Информация за начислените такси и дължими суми може да откриете в меню Месечни такси! " +
                        "Общата дължима сума за този самостоятелен обект е " + totalDueAmount + " EUR";
                messageTextEn = "You have new monthly fee for " + month + " " + year + " for the amount of " +
                        monthlyFee + " EUR for property № " + property.getNumber() + "." +
                        "\n" +
                        "You can check details in your Property section." +
                        "Total due amount for your property is " + totalDueAmount + " EUR";
            } else {
                messageText = "Имате нова месечна такса за " + month + " " + year + " за сумата от " +
                        monthlyFee + " EUR за самостоятелн обект № " + property.getNumber() + "." +
                        "\n" +
                        "Информация за начислените такси и дължими суми може да откриете в меню Месечни такси!" +
                        "Към момента нямате натрупани текущи задължения!";
                messageTextEn = "You have new monthly fee for " + month + " " + year + " for the amount of " +
                        monthlyFee + " EUR for property № " + property.getNumber() + "." +
                        "\n" +
                        "You can check details in your Property section." +
                        "You have no due amount";
            }
            message.setText(messageText);
            message.setTextEn(messageTextEn);

            messageRepository.save(message);

            logService.info("✅[-> newFeeMessageToPropertyOwner] New monthly fee MESSAGE successfully created for user: {}, owner of property id: {}", propertyOwner.getId(), property.getId());
        } catch (Exception e) {
            logService.error("❌[-> newFeeMessageToPropertyOwner] Failed to create a MESSAGE for new monthly fee for user: {}, owner of property id: {}! {}", propertyOwner.getId(), property.getId(), e);
        }
    }


    /**
     * New EVENT message (+ notification) for every VERIFIED property owner
     *
     * @param event             is the actual event triggering the notification
     * @param residentialEntity is the current Residential entity where the event happens.
     */
    @Override
    public void newEventMessageToAllVerifiedPropertyOwners(Event event, ResidentialEntity residentialEntity) {
        try {
            List<Message> newMessages = residentialEntity.getProperties().stream()
                    .filter(property -> property.isObtained() && property.getOwner() != null)
                    .map(property -> {
                        Message message = createMessage(property.getOwner());
                        message.setText(EVENT_MSG_BG);
                        message.setTextEn(EVENT_MSG_ENG);
                        return message;
                    })
                    .toList();

            messageRepository.saveAll(newMessages);
            logService.info("✅[-> newEventMessageToAllVerifiedPropertyOwners] Successfully created new event MESSAGE for all users of condominium id: {}", residentialEntity.getId());
        } catch (Exception e) {
            logService.error("❌[-> newEventMessageToAllVerifiedPropertyOwners] Failed to create new event MESSAGE for all users in condominium id: {}", residentialEntity.getId(), e);
        }
    }


    /**
     * New message template creation
     *
     * @return new message
     */
    private static Message createMessage(UserEntity receiver) {
        Message message = new Message();
        message.setDate(LocalDate.now());
        message.setTime(Time.valueOf(LocalTime.now()));
        message.setRead(false);
        message.setArchived(false);
        message.setReceiver(receiver);
        return message;
    }

    @Override
    public Message findMessageById(Long id) {
        return messageRepository.findById(id).orElse(null);
    }
}


