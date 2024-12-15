package com.example.OurHome.controller;

import com.example.OurHome.service.LanguageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

    public UserController(LanguageService languageService) {
        this.languageService = languageService;
    }

    private final LanguageService languageService;

    @GetMapping("/login")
    public ModelAndView login(@CookieValue(value = "lang",defaultValue = "bg") String lang) {

            return new ModelAndView(languageService.resolveView(lang, "login"));
    }

    @PostMapping("/login/error")
    public ModelAndView onFailure(@ModelAttribute("email") String email) {

        return new ModelAndView("login")
                .addObject("email", email)
                .addObject("bad_credentials", true);
    }
}



