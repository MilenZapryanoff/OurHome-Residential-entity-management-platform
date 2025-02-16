package com.example.OurHome.service;

import com.example.OurHome.model.Entity.*;
import com.example.OurHome.repo.NotificationRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }


    /**
     * Create notification for OWNERS when a new EVENT is created
     *
     * @param event             contains the data for the common event
     * @param residentialEntity is the condominium where the event happens
     */
    @Override
    public void newEventNotificationToAllVerifiedPropertyOwners(Event event, ResidentialEntity residentialEntity) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM, yyyy");
        String eventDate = event.getDate().format(formatter);

        List<Notification> newNotifications = residentialEntity.getProperties().stream()
                .filter(property -> property.getOwner() != null && property.isObtained())
                .map(property -> createNotification(
                        property.getOwner(),
                        property.getId(),
                        "new-event",
                        "Календар",
                        "Ново събитие за " + eventDate + " в календара на вашата етажна собственост!",
                        "Calendar",
                        "New condominium event has been created for " + eventDate + "!"
                ))
                .collect(Collectors.toList());

        notificationRepository.saveAll(newNotifications);
    }

    /**
     * Creating notification for a new monthly fee.
     *
     * @param property       the property for which the fee is applied.
     * @param feeAmount      the amount of the fee
     * @param totalDueAmount total due amount for this property
     */
    @Override
    public void newFeeNotificationToPropertyOwner(Property property, BigDecimal feeAmount, BigDecimal totalDueAmount) {
        notificationRepository.save(createNotification(
                property.getOwner(),
                property.getId(),
                "new-fee",
                "Такси",
                "Имате начислена нова такса в размер на " + feeAmount + "лв.",
                "Fees",
                "New monthly fee for " + feeAmount + " BGN."
        ));
    }


    /**
     * Create notification for condominium MANAGER when a new registration request created
     *
     * @param residentialEntity is the condominium where the property is registered
     */
    @Override
    public void propertyPendingRegistrationMessageToManager(ResidentialEntity residentialEntity) {
        notificationRepository.save(createNotification(
                residentialEntity.getManager(),
                residentialEntity.getId(),
                "registration-request",
                "Заявка за регистрация",
                "Нова заявка за регистрация на собственик в състава на етажна собственост с идентификатор " + residentialEntity.getId(),
                "Request for registration",
                "New request for registration of an owner within a condominium with identifier " + residentialEntity.getId()
        ));
    }

    /**
     * Create notification for new message by user
     *
     * @param receiver is the receiver of the message
     */
    @Override
    public void newMessageNotification(UserEntity receiver) {
        notificationRepository.save(createNotification(
                receiver,
                null,
                "new-message",
                "Съобщения",
                "Имате получено ново съобщение",
                "Messages",
                "You have a new message"
        ));
    }

    /**
     * Create notification for condominium MANAGER when owner creates a data change request
     *
     * @param property is the property entity
     */
    @Override
    public void propertyModificationNotificationToManager(Property property) {
        notificationRepository.save(createNotification(
                property.getResidentialEntity().getManager(),
                property.getResidentialEntity().getId(),
                "change-request",
                "Имоти",
                "Нова заявка за промяна на параметри на самостоятелен обект в състава на етажна собственост с идентификатор " + property.getResidentialEntity().getId(),
                "Properties",
                "New property data change request within a condominium with identifier " + property.getResidentialEntity().getId()
        ));
    }


    /**
     * Create notification for property OWNER when manager changes property data
     *
     * @param property is the property entity
     */
    @Override
    public void propertyModificationNotificationToOwner(Property property) {
        notificationRepository.save(createNotification(
                property.getOwner(),
                property.getId(),
                "data-change",
                "Имот",
                "Промяна в параметри на самостоятелен обект",
                "Property",
                "Individual property data change"
        ));
    }


    /**
     * Create notification for property OWNER when manager approves registration request
     *
     * @param property is the property entity
     */
    @Override
    public void propertyRegistrationApprovedNotification(Property property) {
        notificationRepository.save(createNotification(
                property.getOwner(),
                property.getId(),
                "registration-approval",
                "Регистрация",
                "Одобрена заявка за регистрация на самостоятелен обек №" + property.getNumber() + " в състава на етажна собственост с идентификатор: " + property.getResidentialEntity().getId(),
                "Registration",
                "Approved registration for property " + property.getNumber() + " in condominium ID " + property.getResidentialEntity().getId()
        ));
    }


    /**
     * Create notification for property OWNER when manager rejects registration request
     *
     * @param property is the property entity
     */
    @Override
    public void propertyRejectedNotification(Property property) {
        notificationRepository.save(createNotification(
                property.getOwner(),
                property.getId(),
                "registration-rejection",
                "Регистрация",
                "Отхвърлена заявка за регистрация на самостоятелен обек №" + property.getNumber() + " в състава на етажна собственост с идентификатор: " + property.getResidentialEntity().getId(),
                "Registration",
                "Rejected registration for property " + property.getNumber() + " in condominium ID " + property.getResidentialEntity().getId()
        ));
    }

    /**
     * Create notification for property OWNER when manager approve property change request
     *
     * @param property is the property entity
     */
    @Override
    public void propertyChangeRequestApprovedNotification(Property property) {
        notificationRepository.save(createNotification(
                property.getOwner(),
                property.getId(),
                "change-approval",
                "Имот",
                "Одобрена заявка за промяна по параметри на самостоятелен обек №" + property.getNumber() + " в състава на етажна собственост с идентификатор: " + property.getResidentialEntity().getId(),
                "Property",
                "Rejected registration for property " + property.getNumber() + " in condominium ID " + property.getResidentialEntity().getId()
        ));
    }

    /**
     * Create notification for property OWNER when manager reject property change request
     *
     * @param property is the property entity
     */
    @Override
    public void propertyChangeRequestRejectedMessage(Property property) {
        notificationRepository.save(createNotification(
                property.getOwner(),
                property.getId(),
                "change-rejection",
                "Имот",
                "Отхвърлена заявка за промяна по параметри на самостоятелен обек №" + property.getNumber() + " в състава на етажна собственост с идентификатор: " + property.getResidentialEntity().getId(),
                "Property",
                "Rejected registration for property " + property.getNumber() + " in condominium ID " + property.getResidentialEntity().getId()
        ));
    }

    /**
     * Create notification for condominium manager when a user creates a report.
     *
     * @param creator           contains information about the creator of the report
     * @param residentialEntity is the condominium unit where the report is created
     */
    @Override
    public void newReportNotificationToManager(UserEntity creator, ResidentialEntity residentialEntity) {
        notificationRepository.save(createNotification(
                residentialEntity.getManager(),
                residentialEntity.getId(),
                "new-report",
                "Сигнали",
                "Получен е нов сигнал за нередност от " + creator.getFirstName() + " " + creator.getLastName() + " свързан с етажна собственост с идентификатор " + residentialEntity.getId(),
                "Reports",
                "New report from " + creator.getFirstName() + " " + creator.getLastName() + " received about condominium ID: " + residentialEntity.getId()
        ));
    }


    /**
     * Create notification for a new report for all property onwers in condominium.
     *
     * @param creator           contains information about the creator of the report
     * @param residentialEntity is the condominium unit where the report is created
     */
    @Override
    public void newReportNotificationToAllOwners(UserEntity creator, ResidentialEntity residentialEntity) {

        Map<UserEntity, Long> owners = residentialEntity.getProperties().stream()
                .filter(property -> property.getOwner() != null && property.isObtained()) // Проверяваме дали имотът е валиден
                .filter(property -> !property.getOwner().getId().equals(creator.getId())) // Сравняваме по ID вместо по обект
                .collect(Collectors.toMap(
                        Property::getOwner,    // Ключ -> Собственик
                        Property::getId,       // Стойност -> ID на първия имот
                        (existing, replacement) -> existing // Ако има дублиране, запазваме първия
                ));

        List<Notification> notifications = owners.entrySet().stream()
                .map(entry -> createNotification(
                        entry.getKey(),  // Собственикът
                        entry.getValue(), // ID на първия имот, който е намерен за него
                        "report-notification",
                        "Сигнали",
                        "Получен е нов сигнал за нередност от " + creator.getFirstName() + " " + creator.getLastName() +
                                " свързан с етажна собственост с идентификатор " + residentialEntity.getId(),
                        "Reports",
                        "New report from " + creator.getFirstName() + " " + creator.getLastName() +
                                " received about condominium ID: " + residentialEntity.getId()
                ))
                .collect(Collectors.toList());

        notificationRepository.saveAll(notifications);
    }


    /**
     * Create notification for report CREATOR when manager responds to report.
     *
     * @param processedBy contains information about the user processing/responding report
     * @param report      is the actual report entity
     */
    @Override
    public void reportUpdateNotificationToCreator(UserEntity processedBy, Report report) {
        notificationRepository.save(createNotification(
                report.getCreator(),
                report.getId(),
                "report-update",
                "Сигнали",
                "Обновен статус по ваш сигнал за нередност от свързан с етажна собственост с идентификатор " + report.getCreator().getId(),
                "Reports",
                "Updated status of your report related to the condominium with ID " + report.getResidentialEntity().getId()
        ));
    }


    /**
     * Method for deletion of all user notifications!
     *
     * @param userId is the id of the user
     */
    @Override
    public void deleteAllUserNotifications(Long userId) {

        List<Notification> allNotificationsByUserId = notificationRepository.findAllNotificationsByUserId(userId);

        notificationRepository.deleteAll(allNotificationsByUserId);
    }

    @Override
    public void deleteSingleNotification(Long id) {

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification with ID " + id + " not found."));

        if (notification != null) {
            notificationRepository.deleteById(id);
        }
    }


    /**
     * Private method for creating the notification
     *
     * @param user          is the User Entity that will have the notification
     * @param eventId       is the ID of the parent entity element. Used in thymeleaf to redirect to the correct front-end page.
     * @param category      is the category of the notification. Used in thymeleaf to set different colors for different events categories
     * @param titleBG       is the title of the notification in Bulgarian language.
     * @param descriptionBG is the description of notification in Bulgarian language
     * @param titleEN       is the title of the notification in English language.
     * @param descriptionEN is the description of notification in English language
     */
    private Notification createNotification(UserEntity user, Long eventId, String category, String titleBG, String descriptionBG, String titleEN, String descriptionEN) {

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setEventId(eventId);
        notification.setCategory(category);
        notification.setCreationDateTime(LocalDateTime.now());
        notification.setCleared(false);
        notification.setTitleBG(titleBG);
        notification.setDescriptionBG(descriptionBG);
        notification.setTitleEN(titleEN);
        notification.setDescriptionEN(descriptionEN);

        return notification;
    }


}
