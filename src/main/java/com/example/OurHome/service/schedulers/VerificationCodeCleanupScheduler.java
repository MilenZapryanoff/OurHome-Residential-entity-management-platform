package com.example.OurHome.service.schedulers;

import com.example.OurHome.service.PropertyFeeService;
import com.example.OurHome.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class VerificationCodeCleanupScheduler {

    private final UserService userService;
    private final PropertyFeeService propertyFeeService;

    public VerificationCodeCleanupScheduler(UserService userService, PropertyFeeService propertyFeeService) {
        this.userService = userService;
        this.propertyFeeService = propertyFeeService;
    }

    // This expression triggers the method every night at 00:00 o`clock
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanUp() {
        userService.cleanUpPasswordRestoreVerificationCodes();
    }
}
