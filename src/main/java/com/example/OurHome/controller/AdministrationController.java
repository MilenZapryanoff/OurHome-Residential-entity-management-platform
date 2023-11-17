package com.example.OurHome.controller;

import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.Entity.dto.BindingModels.PropertyManageBindingModel;
import com.example.OurHome.model.Entity.dto.BindingModels.ResidentManageBindingModel;
import com.example.OurHome.model.Entity.dto.BindingModels.ResidentialEntityEditBindingModel;
import com.example.OurHome.model.Entity.dto.BindingModels.ResidentialEntityRegisterBindingModel;
import com.example.OurHome.model.Entity.dto.ViewModels.UserViewModel;
import com.example.OurHome.security.SecurityService;
import com.example.OurHome.service.PropertyService;
import com.example.OurHome.service.ResidentialEntityService;
import com.example.OurHome.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdministrationController {

    private final UserService userService;
    private final ResidentialEntityService residentialEntityService;
    private final PropertyService propertyService;
    private final SecurityService securityService;

    public AdministrationController(UserService userService, ResidentialEntityService residentialEntityService, PropertyService propertyService, SecurityService securityService) {
        this.userService = userService;
        this.residentialEntityService = residentialEntityService;
        this.propertyService = propertyService;
        this.securityService = securityService;
    }

    @GetMapping("/administration")
    public ModelAndView administration() {

        return new ModelAndView("administration", "userViewModel", getUserViewModel());
    }

    @GetMapping("/administration/add")
    public ModelAndView addResidence(@ModelAttribute("residentialEntityRegisterBindingModel") ResidentialEntityRegisterBindingModel residentialEntityRegisterBindingModel) {

        return new ModelAndView("administration-add-residence", "userViewModel", getUserViewModel());
    }

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

    @PostMapping("/administration/remove/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityRemove(@PathVariable("id") Long id) {


        if (residentialEntityService.checkIfResidentialEntityDeletable(id)) {
            residentialEntityService.removeResidentialEntity(id);
            return new ModelAndView("administration", "userViewModel", getUserViewModel()).addObject("deleted", true);
        }
        return new ModelAndView("administration", "userViewModel", getUserViewModel())
                .addObject("notDeleted", true);
    }

    /**
     * GetMapping of Property residents details
     */
    @GetMapping("/administration/residents/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityResidentsDetails(@ModelAttribute("residentManageBindingModel") ResidentManageBindingModel residentManageBindingModel, @PathVariable("id") Long id) {


        return new ModelAndView("administration-residents", "userViewModel", getUserViewModel())
                .addObject("residentialEntity", getResidentialEntity(id));
    }

    /**
     * Property residents details.
     * Handles role changes, send messages and delete user requests in administration-residents.html
     * PostMapping
     */
    @PostMapping("/administration/residents/edit_role/{id}")
    @PreAuthorize("@securityService.checkResidentModeratorAccess(#id, authentication)")
    public ModelAndView changeUserRole(@ModelAttribute("residentManageBindingModel") ResidentManageBindingModel residentManageBindingModel, @PathVariable("id") Long id) {

        if (residentManageBindingModel.getUserId() != null && residentManageBindingModel.getEntityId() != null) {
            userService.createModerator(residentManageBindingModel.getUserId(), residentManageBindingModel.getEntityId());
        } else {
            userService.removeModerator(id, residentManageBindingModel.getEntityId());
        }

        return new ModelAndView("redirect:/administration/residents/" + residentManageBindingModel.getEntityId());
    }

    /**
     * Remove resident from RE.
     * PostMapping
     */
    @PostMapping("/administration/residents/delete/{id}")
    @PreAuthorize("@securityService.checkResidentModeratorAccess(#id, authentication)")
    public ModelAndView deleteResident(@ModelAttribute("residentManageBindingModel") ResidentManageBindingModel residentManageBindingModel, @PathVariable("id") Long id) {

        //remove user form RE
        userService.removeResidentFromResidentialEntity(id, getResidentialEntity(residentManageBindingModel.getEntityId()));
        //delete user's properties in this RE
        propertyService.deletePropertiesWhenResidentRemoved(id, residentManageBindingModel.getEntityId());

        return new ModelAndView("redirect:/administration/residents/" + residentManageBindingModel.getEntityId());
    }

    /**
     * Residential entity details
     * GetMapping
     */
    @GetMapping("/administration/details/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityDetails(@ModelAttribute("residentManageBindingModel") ResidentManageBindingModel residentManageBindingModel, @PathVariable("id") Long id) {

        return new ModelAndView("administration-details", "userViewModel", getUserViewModel())
                .addObject("residentialEntity", getResidentialEntity(id));
    }

    /**
     * Property details
     * GetMapping
     */
    @GetMapping("/administration/property/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityPropertyDetails(@ModelAttribute("propertyManageBindingModel") PropertyManageBindingModel propertyManageBindingModel, @PathVariable("id") Long id) {

        return new ModelAndView("administration-property", "userViewModel", getUserViewModel())
                .addObject("residentialEntity", getResidentialEntity(id));
    }


    /**
     * Property approval
     * PostMapping
     */
    @PostMapping("/administration/property/approve/{id}")
    @PreAuthorize("@securityService.checkPropertyModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityPropertyApprove(@ModelAttribute("propertyManageBindingModel") PropertyManageBindingModel propertyManageBindingModel, @PathVariable("id") Long id) {

        propertyService.approveProperty(id);

        return new ModelAndView("redirect:/administration/property/" + propertyManageBindingModel.getEntityId());
    }

    /**
     * Property reject
     * PostMapping
     */
    @PostMapping("/administration/property/reject/{id}")
    @PreAuthorize("@securityService.checkPropertyModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityPropertyDecline(@ModelAttribute("propertyManageBindingModel") PropertyManageBindingModel propertyManageBindingModel, @PathVariable("id") Long id) {

        propertyService.rejectProperty(id);

        return new ModelAndView("redirect:/administration/property/" + propertyManageBindingModel.getEntityId());
    }

    /**
     * Property delete
     * PostMapping
     */
    @PostMapping("/administration/property/delete/{id}")
    @PreAuthorize("@securityService.checkPropertyModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityPropertyDelete(@ModelAttribute("propertyManageBindingModel") PropertyManageBindingModel propertyManageBindingModel, @PathVariable("id") Long id) {

        propertyService.deleteProperty(id);

        return new ModelAndView("redirect:/administration/property/" + propertyManageBindingModel.getEntityId());
    }

    /**
     * Residential entity edit data method
     * GetMapping
     */
    @GetMapping("/administration/details/edit/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityEditDetails(@PathVariable("id") Long id) {

        ResidentialEntityEditBindingModel residentialEntityEditBindingModel = residentialEntityService.mapEntityToEditBindingModel(getResidentialEntity(id));

        return new ModelAndView("administration-details-edit")
                .addObject("userViewModel", getUserViewModel())
                .addObject("residentialEntity", getResidentialEntity(id))
                .addObject("residentialEntityEditBindingModel", residentialEntityEditBindingModel);
    }


    /**
     * Residential entity data edit method
     * PostMapping
     */
    @PostMapping("/administration/details/edit/{entityId}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#entityId, authentication)")
    public ModelAndView residentialEntityEditDetailsPost(@ModelAttribute("residentialEntityEditBindingModel")
                                                         @Valid ResidentialEntityEditBindingModel residentialEntityEditBindingModel, @PathVariable("entityId") Long entityId, BindingResult bindingResult) {


        ModelAndView modelAndView = new ModelAndView("administration-details-edit")
                .addObject("userViewModel", getUserViewModel())
                .addObject("residentialEntity", getResidentialEntity(entityId))
                .addObject("residentialEntityEditBindingModel", residentialEntityEditBindingModel);


        String accessCode = residentialEntityEditBindingModel.getAccessCode();
        String confirmAccessCode = residentialEntityEditBindingModel.getConfirmAccessCode();

        if (bindingResult.hasErrors()) {
            return modelAndView;
        } else if (!accessCode.isEmpty()) {
            if (accessCode.length() <= 3 || accessCode.length() >= 20) {
                return modelAndView.addObject("bad_accessCode", true);
            }
            if (!residentialEntityService.accessCodesMatchCheck(accessCode, confirmAccessCode)) {
                return modelAndView.addObject("noAccessCodeMatch", true);
            }
        }
        residentialEntityService.editResidentialEntity(entityId, residentialEntityEditBindingModel);

        return new ModelAndView("redirect:/administration/details/" + entityId);
    }

    /**
     * This private method returns currently logged user
     *
     * @return UserEntity
     */
    private UserViewModel getUserViewModel() {
        UserEntity loggedUser = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return userService.getUserViewData(loggedUser);
    }

    /**
     * This private method finds a ResidentialEntity by residential entity id
     *
     * @param id
     * @return ResidentialEntity
     */
    private ResidentialEntity getResidentialEntity(Long id) {
        return residentialEntityService.findResidentialEntityById(id).orElse(null);
    }
}