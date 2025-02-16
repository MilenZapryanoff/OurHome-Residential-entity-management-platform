package com.example.OurHome.controller;

import com.example.OurHome.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class NotificationRestController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationRestController.class);
    private final NotificationService notificationService;

    public NotificationRestController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/delete-notification/{id}")
    public ResponseEntity<String> deleteSingleNotification(@PathVariable Long id) {
        try {
            notificationService.deleteSingleNotification(id);
            return ResponseEntity.ok("OK!");
        } catch (IllegalArgumentException e) {
            logger.warn("❌ Error deleting notification: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
