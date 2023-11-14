package com.example.OurHome.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

    @GetMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }

    @PostMapping("/login/error")
    public ModelAndView onFailure(@ModelAttribute("email") String email) {

        return new ModelAndView("login")
                .addObject("email", email)
                .addObject("bad_credentials", true);
    }
}



