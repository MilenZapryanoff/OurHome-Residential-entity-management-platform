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
    public ModelAndView administration() {

        return new ModelAndView("administration/administration", "userViewModel", getUserViewModel());
    }

    /**
     * Create new Residential entity
     */
    @GetMapping("/administration/add")
    public ModelAndView addNewResidence(@ModelAttribute("residentialEntityRegisterBindingModel")
                                        ResidentialEntityRegisterBindingModel residentialEntityRegisterBindingModel) {

        return new ModelAndView("administration/administration-add-residence", "userViewModel", getUserViewModel());
    }

    /**
     * Create new Residential entity
     *
     * @param residentialEntityRegisterBindingModel carries register information
     * @param bindingResult                         result
     */
    @PostMapping("/administration/add")
    public ModelAndView addResidence(@ModelAttribute("residentialEntityRegisterBindingModel") @Valid ResidentialEntityRegisterBindingModel residentialEntityRegisterBindingModel, BindingResult bindingResult) {

        UserEntity loggedUser = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        if (bindingResult.hasErrors()) {
            return new ModelAndView("administration/administration-add-residence", "userViewModel", getUserViewModel());
        } else if (!residentialEntityService.accessCodesMatchCheck(residentialEntityRegisterBindingModel.getAccessCode(), residentialEntityRegisterBindingModel.getConfirmAccessCode())) {
            return new ModelAndView("administration/administration-add-residence", "userViewModel", getUserViewModel())
                    .addObject("noAccessCodeMatch", true);
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
    public ModelAndView residentialEntityRemove(@PathVariable("id") Long id) {

        ModelAndView modelAndView = new ModelAndView("administration/administration", "userViewModel", getUserViewModel());

        if (residentialEntityService.checkIfResidentialEntityDeletable(id)) {
            residentialEntityService.deleteResidentialEntity(id);
            return modelAndView.addObject("deleted", true);
        }
        return modelAndView.addObject("notDeleted", true);
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
     * Upload Residential entity picture method
     *
     * @param file uploaded file
     * @return modelAndView
     */
    @PostMapping("/administration/uploadResidentialEntityPicture/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView uploadResidentialEntityPicture(@PathVariable("id") Long id,
                                                       @RequestParam("picture") MultipartFile file) {

        ResidentialEntity residentialEntity = getResidentialEntity(id);

        try {
            String relativePath = residentialEntityService.savePicture(file, residentialEntity);
            residentialEntityService.updatePicturePath(residentialEntity, relativePath);
        } catch (IllegalArgumentException | IOException e) {
            return new ModelAndView("administration/administration", "userViewModel", getUserViewModel())
                    .addObject("errorMessage", e.getMessage());
        }
        return new ModelAndView("redirect:/administration");
    }


    /**
     * Remove residential entity picture method
     *
     * @return modelAndView
     */
    @PostMapping("/administration/removeResidentialEntityPicture/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView removeResidentialEntityPicture(@PathVariable("id") Long id) {

        ResidentialEntity residentialEntity = getResidentialEntity(id);

        try {
            residentialEntityService.removeResidentialEntityPicture(residentialEntity);
        } catch (IllegalArgumentException | IOException e) {
            return new ModelAndView("administration/administration", "userViewModel", getUserViewModel())
                    .addObject("errorMessage", e.getMessage());
        }
        return new ModelAndView("redirect:/administration");
    }

    private ResidentialEntity getResidentialEntity(Long id) {
        return residentialEntityService.findResidentialEntityById(id).orElse(null);
    }
}