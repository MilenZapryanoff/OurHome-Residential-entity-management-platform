package com.example.OurHome.service;

import com.example.OurHome.model.Entity.*;

import java.math.BigDecimal;

public interface NotificationService {

    void newEventNotificationToAllVerifiedPropertyOwners(Event event, ResidentialEntity residentialEntity);

    void newFeeNotificationToPropertyOwner(Property property, BigDecimal feeAmount, BigDecimal totalDueAmount);

    void propertyPendingRegistrationMessageToManager(ResidentialEntity residentialEntity);

    void newMessageNotification(UserEntity receiver);

    void propertyModificationNotificationToManager(Property property);

    void propertyModificationNotificationToOwner(Property property);

    void propertyRegistrationApprovedNotification(Property property);

    void propertyRejectedNotification(Property property);

    void propertyChangeRequestApprovedNotification(Property property);

    void propertyChangeRequestRejectedMessage(Property property);

    void deleteAllUserNotifications(Long userId);

    void deleteSingleNotification(Long id) throws IllegalArgumentException;

    void newReportNotificationToManager(UserEntity creator, ResidentialEntity residentialEntity);

    void newReportNotificationToAllOwners(UserEntity creator, ResidentialEntity residentialEntity);

    void reportUpdateNotificationToCreator(UserEntity processedBy, Report report);
}
