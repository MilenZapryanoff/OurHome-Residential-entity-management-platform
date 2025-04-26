package com.example.OurHome.service.schedulers;

import com.example.OurHome.service.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RestoreCodeCleanupScheduler {

    private final UserService userService;

    public RestoreCodeCleanupScheduler(UserService userService) {
        this.userService = userService;
    }

    // This expression triggers the method every night at 00:00 o`clock
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanUp() {
        userService.cleanUpPasswordResetCodes();
    }
}
