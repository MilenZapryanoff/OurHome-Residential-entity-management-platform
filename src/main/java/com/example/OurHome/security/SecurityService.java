package com.example.OurHome.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public interface SecurityService {
    boolean checkResidentialEntityModeratorAccess(Long residentialEntityId, Authentication authentication);
    boolean checkPropertyModeratorAccess(Long propertyId, Authentication authentication);
    boolean checkResidentModeratorAccess(Long residentId, Authentication authentication);
    boolean checkMessageUserAccess(Long messageId, Authentication authentication);
    boolean checkMessageSender(Long messageId, Long senderId, Authentication authentication);
    boolean checkMassMessagesUserAccess(Long userId, Authentication authentication);
    boolean checkProfileEditAccess(Long userId, Authentication authentication);
    boolean checkPropertyFeeModeratorAccess(Long propertyFeeId, Authentication authentication);
}

