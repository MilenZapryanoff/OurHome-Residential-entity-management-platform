package com.example.OurHome.controller;

import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.Entity.dto.BindingModels.User.ForgotPasswordBindingModel;
import com.example.OurHome.model.Entity.dto.BindingModels.User.ResetPasswordBindingModel;
import com.example.OurHome.service.UserService;
import com.example.OurHome.service.tokens.UserToken;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
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
    public ModelAndView forgotPassword(@ModelAttribute("forgotPasswordBindingModel") ForgotPasswordBindingModel forgotPasswordBindingModel) {
        return new ModelAndView("password-forgot");
    }

    /**
     * Handles email input from user.
     */
    @PostMapping("/forgot-password")
    public ModelAndView forgotPassword(@ModelAttribute("forgotPasswordBindingModel")
                                      @Valid ForgotPasswordBindingModel forgotPasswordBindingModel,
                                      BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView();
        }

        UserEntity user = userService.findUserByEmail(forgotPasswordBindingModel.getEmail());

        if (user != null) {
            if (user.isValidated()) {
                userService.sendVerificationCode(user);
            }
            userToken.setUserId(user.getId());
            userToken.setValid(true);
            return new ModelAndView("redirect:/reset-password");
        }
        return new ModelAndView("password-forgot").addObject("resetFailed", true);
    }

    @GetMapping("/reset-password")
    public ModelAndView resetPassword(@ModelAttribute("resetPasswordBindingModel")
                                      ResetPasswordBindingModel resetPasswordBindingModel) {

        return new ModelAndView("password-reset")
                .addObject("resetPasswordBindingModel", resetPasswordBindingModel);
    }

    @PostMapping("/reset-password")
    public ModelAndView resetPassword(@ModelAttribute("resetPasswordBindingModel")
                                          @Valid ResetPasswordBindingModel resetPasswordBindingModel,
                                          BindingResult bindingResult) {

        if (!userToken.isValid()) {
            return new ModelAndView("redirect:/forgot-password");
        }

        ModelAndView modelAndView = new ModelAndView("password-reset");

        if (bindingResult.hasErrors()) {
            return modelAndView;
        }

        String newPassword = resetPasswordBindingModel.getNewPassword();
        UserEntity user = userService.findUserById(userToken.getUserId());

        if (user != null) {
            if (!userService.verificationCodeMatch(user, resetPasswordBindingModel.getVerificationCode())) {
                return modelAndView.addObject("invalidVerificationCode", true);
            }
            if (!userService.passwordsMatch(newPassword, resetPasswordBindingModel.getConfirmPassword())) {
                return modelAndView
                        .addObject("noPasswordMatch", true);
            }
            userService.resetPassword(user, newPassword);

            return modelAndView.addObject("resetSuccess", true);
        }
        return modelAndView.addObject("resetFailed", true);
    }

    @PostMapping("/reset-password/resend")
    public ModelAndView resendVerificationCode(@ModelAttribute("resetPasswordBindingModel")
                                               ResetPasswordBindingModel resetPasswordBindingModel) {

        if (!userToken.isValid()) {
            return new ModelAndView("redirect:/forgot-password");
        }

        UserEntity user = userService.findUserById(userToken.getUserId());
        if (user != null) {
            userService.sendVerificationCode(user);
            userToken.setUserId(user.getId());
            userToken.setValid(true);
            return new ModelAndView("password-reset");
        }
        return new ModelAndView("password-reset").addObject("codeResent", true);
    }
}
