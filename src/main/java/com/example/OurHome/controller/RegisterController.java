package com.example.OurHome.controller;

import com.example.OurHome.model.dto.BindingModels.User.ManagerRegisterBindingModel;
import com.example.OurHome.model.dto.BindingModels.User.UserRegisterBindingModel;
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

    public RegisterController(UserService userService, ResidentialEntityToken residentialEntityToken) {
        this.userService = userService;
        this.residentialEntityToken = residentialEntityToken;
    }

    @GetMapping("/register")
    public ModelAndView preRegistration(@ModelAttribute("managerRegisterBindingModel") ManagerRegisterBindingModel managerRegisterBindingModel,
                                        @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        return resolveView(lang) ?
                new ModelAndView("bg/register") : new ModelAndView("en/register");
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

        return resolveView(lang) ?
                new ModelAndView("bg/register-user") : new ModelAndView("en/register-user");
    }

    @PostMapping("/register/auth/user")
    public ModelAndView register(@ModelAttribute("userRegisterBindingModel")
                                 @Valid UserRegisterBindingModel userRegisterBindingModel,
                                 BindingResult bindingResult,
                                 @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        if (!residentialEntityToken.isValid()) {
            return new ModelAndView("redirect:/register/auth");
        }

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/register-user") : new ModelAndView("en/register-user");

        if (bindingResult.hasErrors()) {
            return view;
        } else if (userService.preRegistrationEmailCheck(userRegisterBindingModel.getEmail())) {
            return view.addObject("duplicatedEmail", true);
        } else if (!userService.passwordsMatch(userRegisterBindingModel.getPassword(), userRegisterBindingModel.getConfirmPassword())) {
            return view.addObject("noPasswordMatch", true);
        } else {
            userService.registerUser(userRegisterBindingModel, residentialEntityToken.getTokenId());
            view = resolveView(lang) ?
                    new ModelAndView("bg/registration-success") : new ModelAndView("en/registration-success");
            return view;
        }
    }

    /**
     * MANAGER registration
     */

    @GetMapping("/register/manager")
    public ModelAndView registerManager(@ModelAttribute("managerRegisterBindingModel") ManagerRegisterBindingModel managerRegisterBindingModel,
                                        @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        return resolveView(lang) ?
                new ModelAndView("bg/register-manager") : new ModelAndView("en/register-manager");
    }

    @PostMapping("/register/manager")
    public ModelAndView register(@ModelAttribute("managerRegisterBindingModel")
                                 @Valid ManagerRegisterBindingModel managerRegisterBindingModel,
                                 BindingResult bindingResult,
                                 @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/register-manager") : new ModelAndView("en/register-manager");

        if (bindingResult.hasErrors()) {
            return view;
        } else if (userService.preRegistrationEmailCheck(managerRegisterBindingModel.getEmail())) {
            return view.addObject("duplicatedEmail", true);
        } else if (!userService.passwordsMatch(managerRegisterBindingModel.getPassword(), managerRegisterBindingModel.getConfirmPassword())) {
            return view.addObject("noPasswordMatch", true);
        } else {
            userService.registerManager(managerRegisterBindingModel);

            view = resolveView(lang) ?
                    new ModelAndView("bg/registration-success") : new ModelAndView("en/registration-success");
            return view;
        }
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
