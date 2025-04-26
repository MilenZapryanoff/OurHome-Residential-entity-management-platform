package com.example.OurHome.controller;

import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.User.ForgotPasswordBindingModel;
import com.example.OurHome.model.dto.BindingModels.User.ResetPasswordBindingModel;
import com.example.OurHome.service.UserService;
import com.example.OurHome.service.tokens.UserToken;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ForgotPasswordController {

    private final UserService userService;
    private final UserToken userToken;

    public ForgotPasswordController(UserService userService, UserToken userToken) {
        this.userService = userService;
        this.userToken = userToken;
    }

    /**
     * Handles email input from user.
     */
    @GetMapping("/forgot-password")
    public ModelAndView forgotPassword(@ModelAttribute("forgotPasswordBindingModel") ForgotPasswordBindingModel forgotPasswordBindingModel,
                                       @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/password-forgot") : new ModelAndView("en/password-forgot");

        return view;
    }

    /**
     * Handles email input from user.
     */
    @PostMapping("/forgot-password")
    public ModelAndView forgotPassword(@ModelAttribute("forgotPasswordBindingModel")
                                       @Valid ForgotPasswordBindingModel forgotPasswordBindingModel,
                                       BindingResult bindingResult,
                                       @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/password-forgot") : new ModelAndView("en/password-forgot");

        if (bindingResult.hasErrors()) {
            return view;
        }

        UserEntity user = userService.findUserByEmail(forgotPasswordBindingModel.getEmail());

        if (user != null) {
            if (user.isValidated()) {
                userService.sendResetCode(user);
            }
            userToken.setUserId(user.getId());
            userToken.setValid(true);
            return new ModelAndView("redirect:/reset-password");
        }

        return view.addObject("resetFailed", true);
    }

    @GetMapping("/reset-success")
    public ModelAndView resetPassword(@CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/reset-success") : new ModelAndView("en/reset-success");

        return view;
    }

    @GetMapping("/reset-password")
    public ModelAndView resetPassword(@ModelAttribute("resetPasswordBindingModel")
                                      ResetPasswordBindingModel resetPasswordBindingModel,
                                      @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/password-reset") : new ModelAndView("en/password-reset");

        return view.addObject("resetPasswordBindingModel", resetPasswordBindingModel);
    }

    @PostMapping("/reset-password")
    public ModelAndView resetPassword(@ModelAttribute("resetPasswordBindingModel")
                                      @Valid ResetPasswordBindingModel resetPasswordBindingModel,
                                      BindingResult bindingResult,
                                      @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        if (!userToken.isValid()) {
            return new ModelAndView("redirect:/forgot-password");
        }

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/password-reset") : new ModelAndView("en/password-reset");

        if (bindingResult.hasErrors()) {
            return view;
        }

        String newPassword = resetPasswordBindingModel.getNewPassword();
        UserEntity user = userService.findUserById(userToken.getUserId());

        if (user != null) {
            if (!userService.resetCodeMatch(user, resetPasswordBindingModel.getResetCode())) {
                return view.addObject("invalidResetCode", true);
            }
            if (!userService.passwordsMatch(newPassword, resetPasswordBindingModel.getConfirmPassword())) {
                return view.addObject("noPasswordMatch", true);
            }
            userService.resetPassword(user, newPassword);

            return new ModelAndView("redirect:/reset-success");
        }
        return view.addObject("resetFailed", true);
    }

    @PostMapping("/reset-password/resend")
    public ModelAndView resendResetCode(@ModelAttribute("resetPasswordBindingModel")
                                        ResetPasswordBindingModel resetPasswordBindingModel,
                                        @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        if (!userToken.isValid()) {
            return new ModelAndView("redirect:/forgot-password");
        }

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/password-reset") : new ModelAndView("en/password-reset");

        UserEntity user = userService.findUserById(userToken.getUserId());
        if (user != null) {
            userService.sendResetCode(user);
            userToken.setUserId(user.getId());
            userToken.setValid(true);
        }
        return view.addObject("codeResent", true);
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
