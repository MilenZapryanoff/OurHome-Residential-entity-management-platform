package com.example.OurHome.controller;

import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.Entity.dto.BindingModels.ContactFormBindingModel;
import com.example.OurHome.model.Entity.dto.ViewModels.UserViewModel;
import com.example.OurHome.service.EmailService;
import com.example.OurHome.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
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
    public ModelAndView contact(@ModelAttribute("ContactFormBindingModel") ContactFormBindingModel ContactFormBindingModel) {
        if (SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
            return new ModelAndView("contact").addObject(new ContactFormBindingModel());
        }

        return new ModelAndView("contact", "userViewModel", getUserViewModel());
    }


    @PostMapping("/contact")
    public ModelAndView submitContactForm(@ModelAttribute("ContactFormBindingModel") ContactFormBindingModel ContactFormBindingModel) {
        // Process the submitted contact form
        // For example, you can access form fields like contactForm.getName(), contactForm.getEmail(), etc.

        // Send an email notification
        emailService.sendContactFormEmail(ContactFormBindingModel.getName(), ContactFormBindingModel.getEmail(), ContactFormBindingModel.getMessage());

        // Redirect to a success page or return to the contact page with a success message
        return new ModelAndView("redirect:/contact?success=true");
    }

    private UserViewModel getUserViewModel() {
        UserEntity loggedUser = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return userService.getUserViewData(loggedUser);
    }
}
