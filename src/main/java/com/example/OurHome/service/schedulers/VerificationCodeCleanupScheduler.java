package com.example.OurHome.service.schedulers;

import com.example.OurHome.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class VerificationCodeCleanupScheduler {

    private final UserService userService;

    public VerificationCodeCleanupScheduler(UserService userService) {
        this.userService = userService;
    }

    // This expression triggers the method everynight at 00:00 o`clock
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanUp() {

        userService.cleanUpPasswordRestoreVerificationCodes();

        System.out.println("Test");
    }

// This expression triggers the method on the first day of every month
//    @Scheduled(cron = "0 0 0 * * ?")


}
