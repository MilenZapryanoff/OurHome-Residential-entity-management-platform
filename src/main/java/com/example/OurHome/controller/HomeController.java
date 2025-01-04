package com.example.OurHome.controller;

import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.ViewModels.UserViewModel;
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

    public HomeController( UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public ModelAndView home(@CookieValue(value = "lang", defaultValue = "bg") String lang) {
        return getIndexModelAndView(lang);
    }

    @GetMapping("/index")
    public ModelAndView index(@CookieValue(value = "lang", defaultValue = "bg") String lang) {
        return getIndexModelAndView(lang);
    }

    @PostMapping("/setLanguage")
    public ModelAndView uploadAvatar(@RequestParam("lang") String lang,
                                     HttpServletResponse response) {

        Cookie cookie = new Cookie("lang", lang);
        cookie.setMaxAge(60 * 60 * 24 * 90); // 90 days
        cookie.setPath("/"); // Cookie accessible across the app
        response.addCookie(cookie);

        //setting system messages language according to general language selection if user is not a guest (anonymous).
        if (!SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
            userService.setSystemMessagesLanguage(lang, getUserViewModel());
        }

        return new ModelAndView("redirect:/index");
    }

    private ModelAndView getIndexModelAndView(String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/index") : new ModelAndView("en/index");

        if (SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
            return view;
        }
        return view.addObject("userViewModel", getUserViewModel());
    }

    private UserViewModel getUserViewModel() {
        UserEntity loggedUser = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return userService.getUserViewData(loggedUser);
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
