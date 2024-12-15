package com.example.OurHome.controller;

import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.ViewModels.UserViewModel;
import com.example.OurHome.service.LanguageService;
import com.example.OurHome.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
    private final UserService userService;
    private final LanguageService languageService;

    public HomeController(UserService userService, LanguageService languageService) {
        this.userService = userService;
        this.languageService = languageService;
    }

    @GetMapping("/")
    public ModelAndView home(@CookieValue(value = "lang",defaultValue = "bg") String lang) {
        return getIndexModelAndView(lang);
    }

    @GetMapping("/index")
    public ModelAndView index(@CookieValue(value = "lang",defaultValue = "bg") String lang) {
        return getIndexModelAndView(lang);
    }

    @PostMapping("/setLanguage")
    public ModelAndView uploadAvatar(@RequestParam("lang") String lang,
                                     HttpServletResponse response) {

        Cookie cookie = new Cookie("lang", lang);
        cookie.setMaxAge(60 * 60 * 24 * 30); // 30 days
        cookie.setPath("/"); // Cookie accessible across the app
        response.addCookie(cookie);

        return new ModelAndView("redirect:/index");
    }


    private ModelAndView getIndexModelAndView(String lang) {

        String indexPage = languageService.resolveView(lang, "index");

        // Handle anonymous users
        if (SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
            return new ModelAndView(indexPage);
        }

        // For authenticated users, add userViewModel
        return new ModelAndView(indexPage, "userViewModel", getUserViewModel());
    }

    private UserViewModel getUserViewModel() {
        UserEntity loggedUser = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return userService.getUserViewData(loggedUser);
    }
}
