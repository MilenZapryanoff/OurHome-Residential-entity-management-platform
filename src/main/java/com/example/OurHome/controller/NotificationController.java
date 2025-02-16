package com.example.OurHome.controller;

import com.example.OurHome.service.NotificationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/clearnotifications/{id}")
    public ModelAndView clearNotifications(@PathVariable("id") Long id,
                                           @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        notificationService.deleteAllUserNotifications(id);
        return new ModelAndView("redirect:/");
    }
}



