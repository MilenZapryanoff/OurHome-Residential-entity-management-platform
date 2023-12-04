package com.example.OurHome.controller.Administration;

import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.Entity.dto.BindingModels.ResidentialEntity.ResidentialEntityRegisterBindingModel;
import com.example.OurHome.model.Entity.dto.ViewModels.UserViewModel;
import com.example.OurHome.service.ResidentialEntityService;
import com.example.OurHome.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

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

        return new ModelAndView("administration", "userViewModel", getUserViewModel());
    }

    /**
     * Create new Residential entity
     */
    @GetMapping("/administration/add")
    public ModelAndView addNewResidence(@ModelAttribute("residentialEntityRegisterBindingModel")
                                        ResidentialEntityRegisterBindingModel residentialEntityRegisterBindingModel) {

        return new ModelAndView("administration-add-residence", "userViewModel", getUserViewModel());
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
            return new ModelAndView("administration-add-residence", "userViewModel", getUserViewModel());
        } else if (!residentialEntityService.accessCodesMatchCheck(residentialEntityRegisterBindingModel.getAccessCode(), residentialEntityRegisterBindingModel.getConfirmAccessCode())) {
            return new ModelAndView("administration-add-residence", "userViewModel", getUserViewModel())
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
        ModelAndView modelAndView = new ModelAndView("administration", "userViewModel", getUserViewModel());

        if (residentialEntityService.checkIfResidentialEntityDeletable(id)) {
            residentialEntityService.removeResidentialEntity(id);
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
}