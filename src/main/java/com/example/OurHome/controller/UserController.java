package com.example.OurHome.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

    @GetMapping("/login")
    public ModelAndView login(@CookieValue(value = "lang", defaultValue = "bg") String lang) {

        return resolveView(lang) ?
                new ModelAndView("bg/login") : new ModelAndView("en/login");
    }

    @PostMapping("/login/error")
    public ModelAndView onFailure(@ModelAttribute("email") String email,
                                  @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/login") : new ModelAndView("en/login");

        return view
                .addObject("email", email)
                .addObject("bad_credentials", true);
    }

    /**
     * Language resolver
     * @param lang This value shows the language
     * @return boolean
     */
    private boolean resolveView(String lang) {
        return "bg".equals(lang);
    }
}



