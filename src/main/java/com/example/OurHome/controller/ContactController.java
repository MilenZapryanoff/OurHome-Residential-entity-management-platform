package com.example.OurHome.controller;

import com.example.OurHome.model.dto.BindingModels.Contact.ContactFormBindingModel;
import com.example.OurHome.service.email.EmailService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ContactController {

    private final EmailService emailService;

    public ContactController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/contact")
    public ModelAndView contact(@ModelAttribute("contactFormBindingModel") ContactFormBindingModel contactFormBindingModel,
                                @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/contact") : new ModelAndView("en/contact");

        return view;
    }

    @PostMapping("/contact")
    public ModelAndView submitContactForm(@ModelAttribute("contactFormBindingModel")
                                          @Valid ContactFormBindingModel contactFormBindingModel,
                                          BindingResult bindingResult,
                                          @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/contact") : new ModelAndView("en/contact");

        if (bindingResult.hasErrors()) {
            return view;
        }

        //TODO: if contact via email feature is not used the next line of code must be commented.
        emailService.sendContactFormEmail(contactFormBindingModel.getName(), contactFormBindingModel.getEmail(), contactFormBindingModel.getSubject(), contactFormBindingModel.getMessage());
        view.addObject("mailSent", true);
        return view;
    }

    /**
     * Language resolver
     *
     * @param lang This value shows the language
     * @return boolean
     */
    private boolean resolveView(String lang) {
        return "bg".equals(lang);
    }
}
