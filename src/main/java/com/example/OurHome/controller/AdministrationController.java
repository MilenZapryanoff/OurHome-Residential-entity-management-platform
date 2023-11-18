package com.example.OurHome.controller;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.Entity.dto.BindingModels.*;
import com.example.OurHome.model.Entity.dto.ViewModels.UserViewModel;
import com.example.OurHome.service.PropertyService;
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
public class AdministrationController {

    private final UserService userService;
    private final ResidentialEntityService residentialEntityService;
    private final PropertyService propertyService;

    public AdministrationController(UserService userService, ResidentialEntityService residentialEntityService, PropertyService propertyService) {
        this.userService = userService;
        this.residentialEntityService = residentialEntityService;
        this.propertyService = propertyService;
    }

    /**
     * Administration section
     * @return view administration
     */
    @GetMapping("/administration")
    public ModelAndView administration() {

        return new ModelAndView("administration", "userViewModel", getUserViewModel());
    }

    /**
     * Create new Residential entity
     * @return view administration-add-residence
     */
    @GetMapping("/administration/add")
    public ModelAndView addResidence() {

        return new ModelAndView("administration-add-residence", "userViewModel", getUserViewModel());
    }

    /**
     * Create new Residential entity
     * @param residentialEntityRegisterBindingModel carries register information
     * @param bindingResult result
     * @return redirect:/administration
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
     * @param id RE id
     * @return view administration
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

    //RESIDENTS MANAGE (ADMINISTRATION SECTION)

    /**
     * @param id residential entity (RE) id
     * @return view administration-residents
     */
    @GetMapping("/administration/residents/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityResidentsDetails(@PathVariable("id") Long id) {

        return new ModelAndView("administration-residents")
                .addObject("userViewModel", getUserViewModel())
                .addObject("residentialEntity", getResidentialEntity(id));
    }

    /**
     * Handles role change requests in RE
     *
     * @param residentManageBindingModel carries information about the RE id
     * @param id                         resident(owner) id
     * @return redirect:/administration/residents/{entityId}
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
     *
     * @param residentManageBindingModel carries information about the RE id
     * @param id                         resident(owner) id
     * @return redirect:/administration/residents/{entityId}
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

    //RESIDENTIAL ENTITY DETAILS(DATA) MANAGE (ADMINISTRATION SECTION)

    /**
     * RE details in Administration
     *
     * @param id property id
     * @return view administration-property
     */
    @GetMapping("/administration/details/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityDetails(@PathVariable("id") Long id) {

        return new ModelAndView("administration-details")
                .addObject("userViewModel", getUserViewModel())
                .addObject("residentialEntity", getResidentialEntity(id));
    }

    /**
     * Residential entity edit form in Administration
     * @param id property id
     * @return view administration-details-edit
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
     * Residential entity edit form in Administration
     * @param residentialEntityEditBindingModel carries info about new values
     * @param entityId RE id
     * @param bindingResult result
     * @return redirect:/administration/details/{entityId}
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

    //PROPERTY MANAGEMENT (ADMINISTRATION SECTION)

    /**
     * Property details in Administration
     *
     * @param id property id
     * @return view administration-property
     */
    @GetMapping("/administration/property/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityPropertyDetails(@PathVariable("id") Long id) {

        return new ModelAndView("administration-property")
                .addObject("userViewModel", getUserViewModel())
                .addObject("residentialEntity", getResidentialEntity(id));
    }


    /**
     * Property approve
     *
     * @param propertyManageBindingModel carries information about the entityId
     * @param id                         property id
     * @return "redirect:/administration/property/{entityId}"
     */
    @PostMapping("/administration/property/approve/{id}")
    @PreAuthorize("@securityService.checkPropertyModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityPropertyApprove(@ModelAttribute("propertyManageBindingModel") PropertyManageBindingModel propertyManageBindingModel, @PathVariable("id") Long id) {

        propertyService.approveProperty(id);

        return new ModelAndView("redirect:/administration/property/" + propertyManageBindingModel.getEntityId());
    }

    /**
     * Property reject
     *
     * @param propertyManageBindingModel carries information about the entityId
     * @param id                         property id
     * @return "redirect:/administration/property/{entityId}"
     */
    @PostMapping("/administration/property/reject/{id}")
    @PreAuthorize("@securityService.checkPropertyModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityPropertyReject(@ModelAttribute("propertyManageBindingModel") PropertyManageBindingModel propertyManageBindingModel, @PathVariable("id") Long id) {

        propertyService.rejectProperty(id);

        return new ModelAndView("redirect:/administration/property/" + propertyManageBindingModel.getEntityId());
    }

    /**
     * Property delete
     *
     * @param propertyManageBindingModel carries information about the entityId
     * @param id                         property id
     * @return "redirect:/administration/property/{entityId}"
     */
    @PostMapping("/administration/property/delete/{id}")
    @PreAuthorize("@securityService.checkPropertyModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityPropertyDelete(@ModelAttribute("propertyManageBindingModel") PropertyManageBindingModel propertyManageBindingModel, @PathVariable("id") Long id) {

        propertyService.deleteProperty(id);

        return new ModelAndView("redirect:/administration/property/" + propertyManageBindingModel.getEntityId());
    }

    /**
     * Property edit in Residential entity
     *
     * @param id property id
     * @return view administration-property-edit.html
     */
    @GetMapping("/administration/property/edit/{id}")
    @PreAuthorize("@securityService.checkPropertyModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityPropertyEdit(@PathVariable("id") Long id) {

        PropertyEditBindingModel propertyEditBindingModel = propertyService.mapPropertyToEditBindingModel(getProperty(id));

        return new ModelAndView("administration-property-edit")
                .addObject("userViewModel", getUserViewModel())
                .addObject("property", getProperty(id))
                .addObject("propertyEditBindingModel", propertyEditBindingModel);
    }

    /**
     * Property edit in Residential entity
     *
     * @param propertyEditBindingModel property data
     * @param id                       property id
     * @return "redirect:/administration/property/{id}"
     */
    @PostMapping("/administration/property/edit/{id}")
    @PreAuthorize("@securityService.checkPropertyModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityPropertyEdit(@ModelAttribute("propertyEditBindingModel")
                                                      @Valid PropertyEditBindingModel propertyEditBindingModel,
                                                      @PathVariable("id") Long id) {

        ResidentialEntity residentialEntity = residentialEntityService.findResidentialEntityByPropertyId(id);
        propertyService.editProperty(id, propertyEditBindingModel, true);

        return new ModelAndView("redirect:/administration/property/" + residentialEntity.getId());
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
     * Method returns a ResidentialEntity
     *
     * @param id residential entity id
     * @return ResidentialEntity
     */
    private ResidentialEntity getResidentialEntity(Long id) {
        return residentialEntityService.findResidentialEntityById(id).orElse(null);
    }

    /**
     * Method returns a Property
     *
     * @param id property id
     * @return Property
     */
    private Property getProperty(Long id) {
        return propertyService.findPropertyById(id);
    }
}