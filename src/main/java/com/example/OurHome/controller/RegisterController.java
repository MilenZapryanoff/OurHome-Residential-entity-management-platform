package com.example.OurHome.controller;

import com.example.OurHome.model.dto.BindingModels.User.ManagerRegisterBindingModel;
import com.example.OurHome.model.dto.BindingModels.User.UserRegisterBindingModel;
import com.example.OurHome.service.tokens.ResidentialEntityToken;
import com.example.OurHome.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RegisterController {

    private final UserService userService;
    private final ResidentialEntityToken residentialEntityToken;

    public RegisterController(UserService userService, ResidentialEntityToken residentialEntityToken) {
        this.userService = userService;
        this.residentialEntityToken = residentialEntityToken;
    }

    @GetMapping("/register")
    public ModelAndView preRegistration(@ModelAttribute("managerRegisterBindingModel") ManagerRegisterBindingModel managerRegisterBindingModel) {
        return new ModelAndView("register");
    }

    /**
     * USER registration
     */

    @GetMapping("/register/auth/user")
    public ModelAndView register(@ModelAttribute("userRegisterBindingModel")
                                 UserRegisterBindingModel userRegisterBindingModel) {
        if (!residentialEntityToken.isValid()) {
            return new ModelAndView("redirect:/register/auth");
        }
        return new ModelAndView("register-user");
    }

    @PostMapping("/register/auth/user")
    public ModelAndView register(@ModelAttribute("userRegisterBindingModel")
                                 @Valid UserRegisterBindingModel userRegisterBindingModel,
                                 BindingResult bindingResult) {

        if (!residentialEntityToken.isValid()) {
            return new ModelAndView("redirect:/register/auth");
        }

        ModelAndView modelAndView = new ModelAndView("register-user");

        if (bindingResult.hasErrors()) {
            return modelAndView;
        } else if (userService.duplicatedUsernameCheck(userRegisterBindingModel.getUsername())) {
            modelAndView.addObject("duplicatedUser", true);
            return modelAndView;
        } else if (userService.preRegistrationEmailCheck(userRegisterBindingModel.getEmail())) {
            modelAndView.addObject("duplicatedEmail", true);
            return modelAndView;
        } else if (!userService.passwordsMatch(userRegisterBindingModel.getPassword(), userRegisterBindingModel.getConfirmPassword())) {
            modelAndView.addObject("noPasswordMatch", true);
            return modelAndView;
        } else {
            userService.registerUser(userRegisterBindingModel, residentialEntityToken.getTokenId());
            return new ModelAndView("redirect:/login");
        }
    }


    /**
     * MANAGER registration
     */

    @GetMapping("/register/manager")
    public ModelAndView registerManager(@ModelAttribute("managerRegisterBindingModel") ManagerRegisterBindingModel managerRegisterBindingModel) {
        return new ModelAndView("register-manager");
    }

    @PostMapping("/register/manager")
    public ModelAndView register(@ModelAttribute("managerRegisterBindingModel")
                                     @Valid ManagerRegisterBindingModel managerRegisterBindingModel, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView("register-manager");

        if (bindingResult.hasErrors()) {
            return modelAndView;
        } else if (userService.duplicatedUsernameCheck(managerRegisterBindingModel.getUsername())) {
            modelAndView.addObject("duplicatedUser", true);
            return modelAndView;
        } else if (userService.preRegistrationEmailCheck(managerRegisterBindingModel.getEmail())) {
            modelAndView.addObject("duplicatedEmail", true);
            return modelAndView;
        } else if (!userService.passwordsMatch(managerRegisterBindingModel.getPassword(), managerRegisterBindingModel.getConfirmPassword())) {
            modelAndView.addObject("noPasswordMatch", true);
            return modelAndView;
        } else {
            userService.registerManager(managerRegisterBindingModel);
            return new ModelAndView("redirect:/login");
        }
    }
}
