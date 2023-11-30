package com.example.OurHome.controller;

import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.Entity.dto.BindingModels.Contact.ContactFormBindingModel;
import com.example.OurHome.model.Entity.dto.ViewModels.UserViewModel;
import com.example.OurHome.service.email.EmailService;
import com.example.OurHome.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ContactController {

    private final UserService userService;
    private final EmailService emailService;

    public ContactController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @GetMapping("/contact")
    public ModelAndView contact(@ModelAttribute("contactFormBindingModel") ContactFormBindingModel contactFormBindingModel) {
        ModelAndView modelAndView = new ModelAndView("contact");

        if (SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
            return modelAndView;
        }
        return modelAndView.addObject("userViewModel", getUserViewModel());
    }


    @PostMapping("/contact")
    public ModelAndView submitContactForm(@ModelAttribute("contactFormBindingModel")
                                          @Valid ContactFormBindingModel contactFormBindingModel,
                                          BindingResult bindingResult) {

        boolean guestUser = SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser");

        ModelAndView modelAndView = new ModelAndView("contact");
        if (bindingResult.hasErrors()) {
            if (guestUser) {
                return modelAndView;
            }
            return modelAndView.addObject("userViewModel", getUserViewModel());
        }

        //TODO: if contact via email feature is not used the next line of code must be commented.
        emailService.sendContactFormEmail(contactFormBindingModel.getName(), contactFormBindingModel.getEmail(), contactFormBindingModel.getSubject(), contactFormBindingModel.getMessage());
        modelAndView.addObject("mailSent", true);

        if (guestUser) {
            return modelAndView;
        }
        return modelAndView
                .addObject("userViewModel", getUserViewModel());
    }

    private UserViewModel getUserViewModel() {
        UserEntity loggedUser = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return userService.getUserViewData(loggedUser);
    }
}
