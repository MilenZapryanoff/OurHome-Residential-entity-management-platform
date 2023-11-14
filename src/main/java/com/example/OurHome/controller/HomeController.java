package com.example.OurHome.controller;

import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.Entity.dto.ViewModels.UserViewModel;
import com.example.OurHome.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public ModelAndView home() {
        return getIndexModelAndView();
    }

    @GetMapping("/index")
    public ModelAndView index() {
        return getIndexModelAndView();
    }

    private ModelAndView getIndexModelAndView() {

        if (SecurityContextHolder.getContext().getAuthentication().getName().equals("anonymousUser")) {
            return new ModelAndView("index");
        }

        return new ModelAndView("index", "userViewModel", getUserViewModel());
    }


    private UserViewModel getUserViewModel() {
        UserEntity loggedUser = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return userService.getUserViewData(loggedUser);
    }
}
