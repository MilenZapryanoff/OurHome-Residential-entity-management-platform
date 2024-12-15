package com.example.OurHome.controller;

import com.example.OurHome.model.dto.BindingModels.User.ManagerRegisterBindingModel;
import com.example.OurHome.model.dto.BindingModels.User.UserRegisterBindingModel;
import com.example.OurHome.service.LanguageService;
import com.example.OurHome.service.UserService;
import com.example.OurHome.service.tokens.ResidentialEntityToken;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RegisterController {

    private final UserService userService;
    private final ResidentialEntityToken residentialEntityToken;
    private final LanguageService languageService;

    public RegisterController(UserService userService, ResidentialEntityToken residentialEntityToken, LanguageService languageService) {
        this.userService = userService;
        this.residentialEntityToken = residentialEntityToken;
        this.languageService = languageService;
    }

    @GetMapping("/register")
    public ModelAndView preRegistration(@ModelAttribute("managerRegisterBindingModel") ManagerRegisterBindingModel managerRegisterBindingModel,
                                        @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        return new ModelAndView(languageService.resolveView(lang, "register"));
    }

    /**
     * USER registration
     */

    @GetMapping("/register/auth/user")
    public ModelAndView register(@ModelAttribute("userRegisterBindingModel")
                                 UserRegisterBindingModel userRegisterBindingModel,
                                 @CookieValue(value = "lang", defaultValue = "bg") String lang) {


        if (!residentialEntityToken.isValid()) {
            return new ModelAndView("redirect:/register/auth");
        }

        return new ModelAndView(languageService.resolveView(lang, "register-user"));
    }

    @PostMapping("/register/auth/user")
    public ModelAndView register(@ModelAttribute("userRegisterBindingModel")
                                 @Valid UserRegisterBindingModel userRegisterBindingModel,
                                 BindingResult bindingResult,
                                 @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        if (!residentialEntityToken.isValid()) {
            return new ModelAndView("redirect:/register/auth");
        }

        ModelAndView modelAndView = new ModelAndView(languageService.resolveView(lang, "register-user"));

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
            return new ModelAndView(languageService.resolveView(lang, "registration-success"));
        }
    }

    /**
     * MANAGER registration
     */

    @GetMapping("/register/manager")
    public ModelAndView registerManager(@ModelAttribute("managerRegisterBindingModel") ManagerRegisterBindingModel managerRegisterBindingModel,
                                        @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        return new ModelAndView(languageService.resolveView(lang, "register-manager"));
    }

    @PostMapping("/register/manager")
    public ModelAndView register(@ModelAttribute("managerRegisterBindingModel")
                                 @Valid ManagerRegisterBindingModel managerRegisterBindingModel,
                                 BindingResult bindingResult,
                                 @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView modelAndView = new ModelAndView(languageService.resolveView(lang, "register-manager"));

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
            return new ModelAndView(languageService.resolveView(lang, "registration-success"));
        }
    }
}
