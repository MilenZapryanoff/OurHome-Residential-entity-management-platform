package com.example.OurHome.controller.Administration;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.PropertyType;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyCreateBindingModel;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyEditBindingModel;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyManageBindingModel;
import com.example.OurHome.service.MessageService;
import com.example.OurHome.service.PropertyService;
import com.example.OurHome.service.PropertyTypeService;
import com.example.OurHome.service.ResidentialEntityService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PropertyManageController {

    private final ResidentialEntityService residentialEntityService;
    private final PropertyService propertyService;
    private final MessageService messageService;
    private final PropertyTypeService propertyTypeService;

    public PropertyManageController(ResidentialEntityService residentialEntityService, PropertyService propertyService, MessageService messageService, PropertyTypeService propertyTypeService) {
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
                                                          @PathVariable("id") Long id,
                                                          @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-property-active") : new ModelAndView("en/administration/administration-property-active");

        return view.addObject("residentialEntity", getResidentialEntity(id));
    }

    /**
     * Property change requests in Administration
     *
     * @param id property id
     * @return view administration-change-requests
     */
    @GetMapping("/administration/property/change-requests/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityChangeRequests(@PathVariable("id") Long id,
                                                        @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-property-change-requests") : new ModelAndView("en/administration/administration-property-change-requests");

        return view.addObject("residentialEntity", getResidentialEntity(id));
    }


    /**
     * Change request details in Administration
     *
     * @param id property id
     * @return view administration- pending property change request
     */
    @GetMapping("/administration/property/pending/request/{id}")
    @PreAuthorize("@securityService.checkPropertyModeratorAccess(#id, authentication)")
    public ModelAndView propertyChangeRequest(@ModelAttribute("propertyManageBindingModel") PropertyManageBindingModel propertyManageBindingModel,
                                              @PathVariable("id") Long id,
                                              @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-property-pending-request") : new ModelAndView("en/administration/administration-property-pending-request");

        return view.addObject("property", getProperty(id));
    }


    /**
     * Processing property change request.
     *
     * @param propertyManageBindingModel carries information about the entityId
     * @param id                         property id
     * @return "redirect:/administration/property/change-requests/{entityId}"
     */
    @PostMapping("/administration/property/pending/request/process/{id}")
    @PreAuthorize("@securityService.checkPropertyModeratorAccess(#id, authentication)")
    public ModelAndView propertyChangeRequestProcess(@ModelAttribute("propertyManageBindingModel") PropertyManageBindingModel propertyManageBindingModel,
                                                     @PathVariable("id") Long id) {

        Property property = propertyService.findPropertyById(id);

        if (propertyManageBindingModel.getAction().equals("approveWithChange")) {
            propertyService.approvePropertyChangeRequest(id);
        }
        if (propertyManageBindingModel.getAction().equals("reject")) {
            propertyService.rejectPropertyChangeRequest(id);
        }

        return new ModelAndView("redirect:/administration/property/change-requests/" + property.getResidentialEntity().getId() + "#pending-registrations");
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
     * Create single property in Condominium
     *
     * @param id residential entitiy id
     * @return view administration-property-create-bg.html
     */
    @GetMapping("/administration/property/create/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityPropertyCreate(@PathVariable("id") Long id,
                                                        @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-property-create") : new ModelAndView("en/administration/administration-property-create");

        return view.addObject("propertyCreateBindingModel", new PropertyCreateBindingModel())
                .addObject("residentialEntity", getResidentialEntity(id));
    }

    /**
     * Create single property in Condominium
     *
     * @param id residential entitiy id
     * @return view administration-property-create.html
     */
    @PostMapping("/administration/property/create/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityPropertyCreate(@ModelAttribute("propertyCreateBindingModel")
                                                        @PathVariable("id") Long id,
                                                        @Valid PropertyCreateBindingModel propertyCreateBindingModel,
                                                        BindingResult bindingResult,
                                                        @CookieValue(value = "lang", defaultValue = "bg") String lang) {


        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-property-create") : new ModelAndView("en/administration/administration-property-create");

        view.addObject("propertyCreateBindingModel", propertyCreateBindingModel)
                .addObject("residentialEntity", getResidentialEntity(id));


        if (bindingResult.hasErrors()) {
            return view;
        }

        int propertyNumber = propertyCreateBindingModel.getNumber();

        //check if this property number is already registered.
        if (propertyService.findPropertyByNumberAndResidentialEntity(propertyNumber, id) != null) {
            return view.addObject("duplicatedProperty", true);
        }

        PropertyType propertyType;
        Long propertyTypeInput = propertyCreateBindingModel.getPropertyType();

        //check if propertyType input exists and propertyType is found
        if (propertyTypeInput != null && getPropertyType(propertyTypeInput) != null) {
            propertyType = getPropertyType(propertyTypeInput);
        } else propertyType = null;

        ResidentialEntity residentialEntity = getResidentialEntity(id);
        if (residentialEntity != null) {
            propertyService.createSingleProperty(propertyCreateBindingModel, residentialEntity, propertyType);
        }

        return new ModelAndView("redirect:/administration/property/active/" + id);
    }


    /**
     * Property edit in Condominium
     *
     * @param id property id
     * @return view administration-property-edit.html
     */
    @GetMapping("/administration/property/edit/{id}")
    @PreAuthorize("@securityService.checkPropertyModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityPropertyEdit(@PathVariable("id") Long id,
                                                      @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-property-edit") : new ModelAndView("en/administration/administration-property-edit");

        return view
                .addObject("property", getProperty(id))
                .addObject("propertyEditBindingModel", propertyService.mapPropertyToEditBindingModel(getProperty(id)));
    }

    /**
     * Property edit in Condominium
     *
     * @param propertyEditBindingModel property data
     * @param id                       property id
     * @return "redirect:/administration/property/{id}"
     */
    @PostMapping("/administration/property/edit/{id}")
    @PreAuthorize("@securityService.checkPropertyModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityPropertyEdit(@PathVariable("id") Long id,
                                                      @Valid PropertyEditBindingModel propertyEditBindingModel,
                                                      BindingResult bindingResult,
                                                      @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        Property property = getProperty(id);

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-property-edit") : new ModelAndView("en/administration/administration-property-edit");

        view.addObject("property", property);

        if (bindingResult.hasErrors()) {
            return view;
        }

        PropertyType propertyType = null;
        if (propertyEditBindingModel.getPropertyType() != null) {
            propertyType = propertyTypeService.findById(propertyEditBindingModel.getPropertyType());
        }

        if (!propertyService.editProperty(id, propertyEditBindingModel, propertyType)) {
            return view.addObject("editFailed", true);
        }

        return new ModelAndView("redirect:/administration/property/active/" + property.getResidentialEntity().getId());
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
    public ModelAndView residentialEntityPropertyDelete
    (@ModelAttribute("propertyManageBindingModel") PropertyManageBindingModel
             propertyManageBindingModel, @PathVariable("id") Long id) {

        Property property = propertyService.findPropertyById(id);
        propertyService.deleteProperty(id, true);

        if (property.isRejected()) {
            return new ModelAndView("redirect:/administration/property/rejected/" + propertyManageBindingModel.getEntityId() + "#rejected-registrations");
        } else {
            return new ModelAndView("redirect:/administration/property/active/" + property.getResidentialEntity().getId() + "#active-registrations");
        }
    }

    /**
     * Method returns a ResidentialEntity
     *
     * @param id Condominium id
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

    /**
     * Method returns a Property
     *
     * @param id property id
     * @return Property
     */
    private PropertyType getPropertyType(Long id) {
        return propertyTypeService.findById(id);
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