package com.example.OurHome.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public interface SecurityService {
    /**
     * Profile access check
     */
    boolean checkProfileEditAccess(Long userId, Authentication authentication);

    /**
     * Condominium access check
     */
    boolean checkResidentialEntityModeratorAccess(Long residentialEntityId, Authentication authentication);

    boolean checkResidentModeratorAccess(Long residentId, Authentication authentication);

    /**
     * Message operations access check
     */
    boolean checkMessageUserAccess(Long messageId, Authentication authentication);

    boolean checkMessageSender(Long messageId, Long senderId, Authentication authentication);

    boolean checkMassMessagesUserAccess(Long userId, Authentication authentication);

    boolean checkMessageSenderAndReceiver(Long propertyId, Long senderId, Long receivedId);

    /**
     * Property Fee access check
     */
    boolean checkPropertyFeeModeratorAccess(Long propertyFeeId, Authentication authentication);

    /**
     * Expense access check
     */
    boolean checkExpenseModeratorAccess(Long expenseId, Authentication authentication);

    boolean checkExpenseUserAccess(Long expenseId, Authentication authentication);

    /**
     * Property access check
     */
    boolean checkPropertyModeratorAccess(Long propertyId, Authentication authentication);

    boolean checkPropertyOwnerAccess(Long propertyId, Authentication authentication);

    boolean checkPropertyOwnerFullAccess(Long propertyId, Authentication authentication);

    /**
     * PropertyType access check
     */
    boolean checkPropertyTypeModeratorAccess(Long propertyTypeId, Authentication authentication);

    boolean checkReportModeratorAccess(Long reportId, Authentication authentication);

    boolean checkReportOwnerAccess(Long reportId, Authentication authentication);

    boolean checkReportUserViewAccess(Long reportId, Authentication authentication);

    boolean checkReportImageViewAccess(Long reportId, Authentication authentication);

    boolean checkEventModeratorAccess(Long reportId, Authentication authentication);

    boolean checkResidentDetailsModeratorAccess(Long userId, Authentication authentication);

    boolean checkAllUserNotificationsControl(Long userId, Authentication authentication);

    boolean checkSingleNotificationsControl(Long notificationId, Authentication authentication);
}

