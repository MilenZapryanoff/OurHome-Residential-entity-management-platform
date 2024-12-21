package com.example.OurHome.controller;

import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.User.ProfileEditBindingModel;
import com.example.OurHome.model.dto.ViewModels.UserViewModel;
import com.example.OurHome.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Profile section view
     *
     * @return resultView
     */
    @GetMapping("/profile")
    public ModelAndView profile(ProfileEditBindingModel profileEditBindingModel,
                                @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/profile") : new ModelAndView("en/profile");

        return view.addObject(profileEditBindingModel);
    }

    /**
     * Upload avatar method
     *
     * @param file uploaded file
     * @return resultView
     */
    @PostMapping("/uploadAvatar")
    public ModelAndView uploadAvatar(@RequestParam("avatar") MultipartFile file,
                                     @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        UserEntity loggedUser = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());



        try {
            String relativePath = userService.saveAvatar(file, loggedUser.getId());
            userService.updateUserAvatar(loggedUser, relativePath);
        } catch (IllegalArgumentException | IOException e) {

            ModelAndView view = resolveView(lang) ?
                    new ModelAndView("bg/profile") : new ModelAndView("en/profile");

            return view.addObject("errorMessage", e.getMessage());
        }
        return new ModelAndView("redirect:/profile");
    }


    /**
     * Remove avatar method
     *
     * @return resultView
     */
    @PostMapping("/removeAvatar")
    public ModelAndView removeAvatar(@CookieValue(value = "lang", defaultValue = "bg") String lang) {

        try {
            userService.removeAvatar(getUserViewModel().getId());
        } catch (IllegalArgumentException | IOException e) {

            ModelAndView view = resolveView(lang) ?
                    new ModelAndView("bg/profile") : new ModelAndView("en/profile");

            return view.addObject("errorMessage", e.getMessage());
        }

        return new ModelAndView("redirect:/profile");
    }

    @GetMapping("/profile/edit/{id}")
    @PreAuthorize("@securityService.checkProfileEditAccess(#id, authentication)")
    public ModelAndView profileEdit(@PathVariable("id") Long id,
                                    @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/profile-edit") : new ModelAndView("en/profile-edit");

        return view.addObject("profileEditBindingModel", userService.getProfileEditBindingModel(id));
    }

    /**
     * User edit request
     *
     * @param id                      logged user id
     * @param profileEditBindingModel binding model bearing new data
     * @param bindingResult           validation result
     * @return resultView
     */
    @PostMapping("/profile/edit/{id}")
    @PreAuthorize("@securityService.checkProfileEditAccess(#id, authentication)")
    public ModelAndView profileEdit(@PathVariable("id") Long id,
                                    @Valid ProfileEditBindingModel profileEditBindingModel,
                                    BindingResult bindingResult,
                                    @CookieValue(value = "lang", defaultValue = "bg") String lang) {


        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/profile-edit") : new ModelAndView("en/profile-edit");

        if (bindingResult.hasErrors()) {
            return view;
        }

        if (!getUserViewModel().getUsername().equals(profileEditBindingModel.getUsername()) &&
                userService.duplicatedUsernameCheck(profileEditBindingModel.getUsername())) {
            return view.addObject("duplicatedEmail", true);
        }

        if (profileEditBindingModel.getNewPassword().isEmpty() &&
                profileEditBindingModel.getConfirmPassword().isEmpty()) {
            userService.editProfile(id, profileEditBindingModel, false);
            return new ModelAndView("redirect:/profile");
        }

        if (!userService.passwordsMatch(profileEditBindingModel.getNewPassword(), profileEditBindingModel.getConfirmPassword())) {
            return view.addObject("noPasswordsMatch", true);
        }

        userService.editProfile(id, profileEditBindingModel, true);
        return new ModelAndView("redirect:/profile");
    }

    /**
     * Method returns information about logged user
     *
     * @return userViewModel
     */
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
