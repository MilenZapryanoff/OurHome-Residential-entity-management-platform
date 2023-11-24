package com.example.OurHome.controller;

import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.Entity.dto.BindingModels.ProfileEditBindingModel;
import com.example.OurHome.model.Entity.dto.ViewModels.UserViewModel;
import com.example.OurHome.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
     * @return modelAndView
     */
    @GetMapping("/profile")
    public ModelAndView profile(ProfileEditBindingModel profileEditBindingModel) {

        return new ModelAndView("profile", "userViewModel", getUserViewModel()).addObject(profileEditBindingModel);
    }

    /**
     * Upload avatar method
     *
     * @param file uploaded file
     * @return modelAndView
     */
    @PostMapping("/uploadAvatar")
    public ModelAndView uploadAvatar(ProfileEditBindingModel profileEditBindingModel,
                                     @RequestParam("avatar") MultipartFile file) {
        UserEntity loggedUser = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        ModelAndView modelAndView = new ModelAndView("profile", "userViewModel", getUserViewModel());

        try {
            String relativePath = userService.saveAvatar(file, loggedUser.getId());
            userService.updateUserAvatar(loggedUser, relativePath);
        } catch (IllegalArgumentException | IOException e) {
            modelAndView.addObject("errorMessage", e.getMessage());
        }
        return modelAndView;
    }

    @GetMapping("/profile/edit/{id}")
    public ModelAndView profileEdit(@PathVariable("id") Long id) {

        return new ModelAndView("profile-edit", "userViewModel", getUserViewModel())
                .addObject("profileEditBindingModel", userService.getProfileEditBindingModel(id));
    }

    /**
     * User edit request
     * @param id logged user id
     * @param profileEditBindingModel binding model bearing new data
     * @param bindingResult validation result
     * @return modelAndView
     */
    @PostMapping("/profile/edit/{id}")
    public ModelAndView profileEdit(@PathVariable("id") Long id,
                                    @Valid ProfileEditBindingModel profileEditBindingModel,
                                    BindingResult bindingResult) {

        ModelAndView modelAndView = new ModelAndView("profile-edit", "userViewModel", getUserViewModel());

        if (bindingResult.hasErrors()) {
            return modelAndView;
        }
        if (!getUserViewModel().getUsername().equals(profileEditBindingModel.getUsername()) &&
                userService.duplicatedUsernameCheck(profileEditBindingModel.getUsername())) {
            return modelAndView.addObject("duplicatedEmail", true);
        }
        if (!profileEditBindingModel.getNewPassword().isEmpty() &&
                !profileEditBindingModel.getConfirmPassword().isEmpty())
            if (!userService.passwordsMatch(profileEditBindingModel.getNewPassword(), profileEditBindingModel.getConfirmPassword())) {
                return modelAndView.addObject("noPasswordsMatch", true);
            }
        if (userService.editProfile(id, profileEditBindingModel)) {
            return new ModelAndView("redirect:/profile");
        }
        return modelAndView.addObject("unsuccessfulEdit", true);
    }

    /**
     * Method returns information about logged user
     * @return userViewModel
     */
    private UserViewModel getUserViewModel() {
        UserEntity loggedUser = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return userService.getUserViewData(loggedUser);
    }
}
