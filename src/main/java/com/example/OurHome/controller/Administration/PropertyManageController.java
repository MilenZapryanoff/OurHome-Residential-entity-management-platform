package com.example.OurHome.controller.Administration;

import com.example.OurHome.model.Entity.Property;
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

import java.math.BigDecimal;

@Controller
public class PropertyManageController {

    private final UserService userService;
    private final ResidentialEntityService residentialEntityService;
    private final PropertyService propertyService;
    private final PropertyFeeService propertyFeeService;
    private final FeeService feeService;
    private final MessageService messageService;

    public PropertyManageController(UserService userService, ResidentialEntityService residentialEntityService, PropertyService propertyService, PropertyFeeService propertyFeeService, FeeService feeService, MessageService messageService) {
        this.userService = userService;
        this.residentialEntityService = residentialEntityService;
        this.propertyService = propertyService;
        this.propertyFeeService = propertyFeeService;
        this.feeService = feeService;
        this.messageService = messageService;
    }

    /**
     * Property details in Administration
     *
     * @param id property id
     * @return view administration-property
     */
    @GetMapping("/administration/property/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityPropertyDetails(@ModelAttribute("propertyManageBindingModel") PropertyManageBindingModel propertyManageBindingModel,
                                                         @PathVariable("id") Long id) {

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

        Property property = propertyService.findPropertyById(id);
        BigDecimal monthlyFee = feeService.calculateMonthlyFee(property.getResidentialEntity(), property);

        propertyService.setMonthlyFee(monthlyFee, property);

        return new ModelAndView("redirect:/administration/property/" + propertyManageBindingModel.getEntityId() + "#post-action-nav");
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

        return new ModelAndView("redirect:/administration/property/" + propertyManageBindingModel.getEntityId() + "#post-action-nav");
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

        propertyService.deleteProperty(id, true);

        return new ModelAndView("redirect:/administration/property/" + propertyManageBindingModel.getEntityId() + "#post-action-nav");
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
        //sending message (notification) to owner/resident
        messageService.propertyModificationMessageToResident(propertyService.findPropertyById(id));

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