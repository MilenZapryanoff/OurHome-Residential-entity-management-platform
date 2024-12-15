package com.example.OurHome.controller;

import com.example.OurHome.model.dto.BindingModels.User.UserAuthBindingModel;
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
public class AuthenticationController {

    private final UserService userService;
    private final ResidentialEntityToken residentialEntityToken;
    private final LanguageService languageService;

    public AuthenticationController(UserService userService, ResidentialEntityToken residentialEntityToken, LanguageService languageService) {
        this.userService = userService;
        this.residentialEntityToken = residentialEntityToken;
        this.languageService = languageService;
    }

    /**
     * USER pre-registration validation
     */

    @GetMapping("/register/auth")
    public ModelAndView authenticateUser(@ModelAttribute("userAuthBindingModel")
                                         UserAuthBindingModel userAuthBindingModel,
                                         @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        return new ModelAndView(languageService.resolveView(lang, "auth-user"));
    }

    @PostMapping("/register/auth")
    public ModelAndView authenticateUser(@ModelAttribute("userAuthBindingModel")
                                         @Valid UserAuthBindingModel userAuthBindingModel,
                                         BindingResult bindingResult,
                                         @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        // Choose template based on the cookie value
        String page = languageService.resolveView(lang, "auth-user");

        if (bindingResult.hasErrors()) {
            return new ModelAndView(page);
        } else if (!userService.residentialValidation(userAuthBindingModel.parseResidentialIdToLong(),
                userAuthBindingModel.getResidentialAccessCode())) {
            return new ModelAndView(page)
                    .addObject("badResidentialEntity", true);
        }

        residentialEntityToken.setTokenId(userAuthBindingModel.parseResidentialIdToLong());
        residentialEntityToken.setValid(true);

        return new ModelAndView("redirect:/register/auth/user");
    }
}
