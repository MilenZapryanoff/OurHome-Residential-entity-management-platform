package com.example.OurHome.controller.Administration;

import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.ResidentialEntity.ResidentialEntityRegisterBindingModel;
import com.example.OurHome.model.dto.ViewModels.UserViewModel;
import com.example.OurHome.service.ResidentialEntityService;
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
public class ResidentialEntityController {

    private final UserService userService;
    private final ResidentialEntityService residentialEntityService;

    public ResidentialEntityController(UserService userService, ResidentialEntityService residentialEntityService) {
        this.userService = userService;
        this.residentialEntityService = residentialEntityService;
    }

    /**
     * Administration section
     */
    @GetMapping("/administration")
    public ModelAndView administration(@CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration") : new ModelAndView("en/administration/administration");

        return view.addObject("userViewModel", getUserViewModel());
    }

    /**
     * Administration section
     */
    @GetMapping("/administration/summary/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntitySummary(
            @PathVariable("id") Long id,
            @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-summary") : new ModelAndView("en/administration/administration-summary");

        return view.addObject("userViewModel", getUserViewModel())
                .addObject("residentialEntity", getResidentialEntity(id));
    }


    /**
     * Create new Residential entity
     */
    @GetMapping("/administration/add")
    public ModelAndView addNewResidence(@ModelAttribute("residentialEntityRegisterBindingModel")
                                        ResidentialEntityRegisterBindingModel residentialEntityRegisterBindingModel,
                                        @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-add-residence") : new ModelAndView("en/administration/administration-add-residence");

        return view.addObject("userViewModel", getUserViewModel());
    }

    /**
     * Create new Residential entity
     *
     * @param residentialEntityRegisterBindingModel carries register information
     * @param bindingResult                         result
     */
    @PostMapping("/administration/add")
    public ModelAndView addResidence(@ModelAttribute("residentialEntityRegisterBindingModel")
                                     @Valid ResidentialEntityRegisterBindingModel residentialEntityRegisterBindingModel,
                                     BindingResult bindingResult,
                                     @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        UserEntity loggedUser = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-add-residence") : new ModelAndView("en/administration/administration-add-residence");

        view.addObject("userViewModel", getUserViewModel());

        if (bindingResult.hasErrors()) {
            return view;
        } else if (!residentialEntityService.accessCodesMatchCheck(residentialEntityRegisterBindingModel.getAccessCode(), residentialEntityRegisterBindingModel.getConfirmAccessCode())) {
            return view.addObject("noAccessCodeMatch", true);
        }
        residentialEntityService.newResidentialEntity(residentialEntityRegisterBindingModel, loggedUser);

        return new ModelAndView("redirect:/administration");
    }

    /**
     * Residential entity (RE) deletion
     *
     * @param id Residential entity id
     */
    @PostMapping("/administration/remove/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityRemove(@PathVariable("id") Long id,
                                                @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-summary") : new ModelAndView("en/administration/administration-summary");

        view
                .addObject("userViewModel", getUserViewModel())
                .addObject("residentialEntity", getResidentialEntity(id));

        if (residentialEntityService.checkIfResidentialEntityDeletable(id)) {
            residentialEntityService.deleteResidentialEntity(id);

            ModelAndView deletionView = resolveView(lang) ?
                    new ModelAndView("bg/administration/administration") : new ModelAndView("en/administration/administration");

            return deletionView
                    .addObject("userViewModel", getUserViewModel())
                    .addObject("deleted", true);
        }
        return view.addObject("notDeleted", true);
    }


    /**
     * Upload Residential entity picture method
     *
     * @param file uploaded file
     * @return resultView
     */
    @PostMapping("/administration/uploadResidentialEntityPicture/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView uploadResidentialEntityPicture(@PathVariable("id") Long id,
                                                       @RequestParam("picture") MultipartFile file,
                                                       @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ResidentialEntity residentialEntity = getResidentialEntity(id);

        try {
            String relativePath = residentialEntityService.savePicture(file, residentialEntity);
            residentialEntityService.updatePicturePath(residentialEntity, relativePath);
        } catch (IllegalArgumentException | IOException e) {

            ModelAndView view = resolveView(lang) ?
                    new ModelAndView("bg/administration/administration-summary") : new ModelAndView("en/administration/administration-summary");

            return view
                    .addObject("userViewModel", getUserViewModel())
                    .addObject("errorMessage", e.getMessage());
        }
        return new ModelAndView("redirect:/administration/summary/" + id);
    }

    /**
     * Remove residential entity picture method
     *
     * @return resultView
     */
    @PostMapping("/administration/removeResidentialEntityPicture/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView removeResidentialEntityPicture(@PathVariable("id") Long id,
                                                       @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ResidentialEntity residentialEntity = getResidentialEntity(id);

        try {
            residentialEntityService.removeResidentialEntityPicture(residentialEntity);
        } catch (IllegalArgumentException | IOException e) {

            ModelAndView view = resolveView(lang) ?
                    new ModelAndView("bg/administration/administration") : new ModelAndView("en/administration/administration");

            return view
                    .addObject("userViewModel", getUserViewModel())
                    .addObject("errorMessage", e.getMessage());
        }
        return new ModelAndView("redirect:/administration/summary/" + id);
    }

    private ResidentialEntity getResidentialEntity(Long id) {
        return residentialEntityService.findResidentialEntityById(id).orElse(null);
    }

    /**
     * Method returns currently logged user
     *
     * @return UserEntity
     */
    private UserViewModel getUserViewModel() {
        UserEntity loggedUser = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return userService.getUserViewData(loggedUser);
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