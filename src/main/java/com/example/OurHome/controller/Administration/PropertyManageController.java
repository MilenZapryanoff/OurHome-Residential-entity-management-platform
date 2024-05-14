package com.example.OurHome.controller.Administration;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.PropertyType;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyEditBindingModel;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyManageBindingModel;
import com.example.OurHome.model.dto.ViewModels.UserViewModel;
import com.example.OurHome.service.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PropertyManageController {

    private final UserService userService;
    private final ResidentialEntityService residentialEntityService;
    private final PropertyService propertyService;
    private final MessageService messageService;
    private final PropertyTypeService propertyTypeService;

    public PropertyManageController(UserService userService, ResidentialEntityService residentialEntityService, PropertyService propertyService, MessageService messageService, PropertyTypeService propertyTypeService) {
        this.userService = userService;
        this.residentialEntityService = residentialEntityService;
        this.propertyService = propertyService;
        this.messageService = messageService;
        this.propertyTypeService = propertyTypeService;
    }

    /**
     * Property details in Administration
     *
     * @param id property id
     * @return view administration- active properties
     */
    @GetMapping("/administration/property/active/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityActiveProperties(@ModelAttribute("propertyManageBindingModel") PropertyManageBindingModel propertyManageBindingModel,
                                                          @PathVariable("id") Long id) {

        return new ModelAndView("administration-property-active")
                .addObject("userViewModel", getUserViewModel())
                .addObject("residentialEntity", getResidentialEntity(id));
    }

    /**
     * Unlink property owner
     *
     * @param propertyManageBindingModel carries information about the entityId
     * @param id                         property id
     * @return "redirect:/administration/property/{entityId}"
     */
    @PostMapping("/administration/property/unlink/{id}")
    @PreAuthorize("@securityService.checkPropertyModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityPropertyUnlink(@ModelAttribute("propertyManageBindingModel") PropertyManageBindingModel propertyManageBindingModel, @PathVariable("id") Long id) {

        propertyService.unlinkOwner(id, true);

        return new ModelAndView("redirect:/administration/property/active/" + propertyManageBindingModel.getEntityId() + "#active-registrations");
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

        PropertyType propertyType = null;
        if (propertyEditBindingModel.getPropertyType() != null) {
            propertyType = propertyTypeService.findById(propertyEditBindingModel.getPropertyType());
        }

        if (propertyService.editProperty(id, propertyEditBindingModel, propertyType)) {
            return new ModelAndView("redirect:/administration/property/active/" + residentialEntity.getId());
        } else {
            //sending message (notification) to owner/resident
            messageService.propertyModificationMessageToResident(propertyService.findPropertyById(id));

            return new ModelAndView("administration-property-edit")
                    .addObject("userViewModel", getUserViewModel())
                    .addObject("property", getProperty(id))
                    .addObject("propertyEditBindingModel", propertyEditBindingModel)
                    .addObject("editFailed", true);
        }
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

        boolean rejected = propertyService.findPropertyById(id).isRejected();
        propertyService.deleteProperty(id, true);

        if (rejected) {
            return new ModelAndView("redirect:/administration/property/rejected/" + propertyManageBindingModel.getEntityId() + "#rejected-registrations");
        } else {
            return new ModelAndView("redirect:/administration/property/active/" + propertyManageBindingModel.getEntityId() + "#active-registrations");
        }
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