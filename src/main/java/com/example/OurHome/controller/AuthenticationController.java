package com.example.OurHome.controller;

import com.example.OurHome.model.dto.BindingModels.User.UserAuthBindingModel;
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

    public AuthenticationController(UserService userService, ResidentialEntityToken residentialEntityToken) {
        this.userService = userService;
        this.residentialEntityToken = residentialEntityToken;
    }

    /**
     * USER pre-registration validation
     */

    @GetMapping("/register/auth")
    public ModelAndView authenticateUser(@ModelAttribute("userAuthBindingModel")
                                         UserAuthBindingModel userAuthBindingModel,
                                         @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/auth-user") : new ModelAndView("en/auth-user");

        return view;
    }

    @PostMapping("/register/auth")
    public ModelAndView authenticateUser(@ModelAttribute("userAuthBindingModel")
                                         @Valid UserAuthBindingModel userAuthBindingModel,
                                         BindingResult bindingResult,
                                         @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/auth-user") : new ModelAndView("en/auth-user");

        if (bindingResult.hasErrors()) {
            return view;
        } else if (!userService.residentialValidation(userAuthBindingModel.parseResidentialIdToLong(),
                userAuthBindingModel.getResidentialAccessCode())) {
            return view.addObject("badResidentialEntity", true);
        }

        residentialEntityToken.setTokenId(userAuthBindingModel.parseResidentialIdToLong());
        residentialEntityToken.setValid(true);

        return new ModelAndView("redirect:/register/auth/user");
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
