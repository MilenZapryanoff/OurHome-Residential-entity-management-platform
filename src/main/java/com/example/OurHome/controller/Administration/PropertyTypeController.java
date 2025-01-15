package com.example.OurHome.controller.Administration;

import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.dto.BindingModels.PropertyType.PropertyTypeAddBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyType.PropertyTypeEditBindingModel;
import com.example.OurHome.service.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PropertyTypeController {

    private final PropertyTypeService propertyTypeService;
    private final ResidentialEntityService residentialEntityService;
    private final PropertyRegisterRequestService propertyRegisterRequestService;
    private final PropertyChangeRequestService propertyChangeRequestService;

    public PropertyTypeController(PropertyTypeService propertyTypeService, ResidentialEntityService residentialEntityService, PropertyRegisterRequestService propertyRegisterRequestService, PropertyChangeRequestService propertyChangeRequestService) {
        this.propertyTypeService = propertyTypeService;
        this.residentialEntityService = residentialEntityService;
        this.propertyRegisterRequestService = propertyRegisterRequestService;
        this.propertyChangeRequestService = propertyChangeRequestService;
    }


    /**
     * PropertyType in Administration
     *
     * @param id Condominium id
     * @return view administration - PROPERTY TYPES
     */
    @GetMapping("/administration/property/types/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityPropertyTypes(@PathVariable("id") Long id,
                                                       @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        return new ModelAndView(lang + "/administration/administration-property-types")
                .addObject("residentialEntity", getResidentialEntity(id));
    }

    /**
     * Add (new) new propertyType in Administration -> Properties -> PROPERTY TYPES
     *
     * @param id Condominium id
     * @return view administration - PROPERTY TYPES add form
     */
    @GetMapping("/administration/property/types/add/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView propertyTypeAdd(@PathVariable("id") Long id,
                                        @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        PropertyTypeAddBindingModel propertyTypeAddBindingModel = new PropertyTypeAddBindingModel();

        return new ModelAndView(lang + "/administration/administration-property-types-add")
                .addObject("residentialEntity", getResidentialEntity(id))
                .addObject("propertyTypeAddBindingModel", propertyTypeAddBindingModel);
    }

    /**
     * Add (new) new propertyType in Administration -> Properties -> PROPERTY TYPES
     *
     * @param id Condominium id
     * @return view administration - PROPERTY TYPES add form
     */
    @PostMapping("/administration/property/types/add/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView propertyTypeAdd(@ModelAttribute("propertyTypeAddBindingModel")
                                        @Valid PropertyTypeAddBindingModel propertyTypeAddBindingModel,
                                        BindingResult bindingResult,
                                        @PathVariable("id") Long id,
                                        @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView resultView = new ModelAndView(lang + "/administration/administration-property-types-add")
                .addObject("residentialEntity", getResidentialEntity(id))
                .addObject("propertyTypeAddBindingModel", propertyTypeAddBindingModel);

        if (bindingResult.hasErrors()) {
            return resultView;
        }

        if (propertyTypeService.addPropertyType(id, propertyTypeAddBindingModel)) {
            return new ModelAndView("redirect:/administration/property/types/" + id);
        }

        return resultView.addObject("editFailed", true);
    }

    /**
     * PropertyType edit in Administration -> Properties -> PROPERTY TYPES -> edit fee
     *
     * @param id property type id
     * @return view administration - PROPERTY TYPES edit form (not redirect)
     */
    @GetMapping("/administration/property/types/edit/{id}")
    @PreAuthorize("@securityService.checkPropertyTypeModeratorAccess(#id, authentication)")
    public ModelAndView propertyTypeEdit(@PathVariable("id") Long id,
                                         @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        PropertyTypeEditBindingModel propertyTypeEditBindingModel = propertyTypeService.mapPropertyTypeToEditBindingModel(id);
        ResidentialEntity residentialEntity = propertyTypeService.findResidentialEntityByPropertyType(id);

        return new ModelAndView(lang + "/administration/administration-property-types-edit")
                .addObject("residentialEntity", residentialEntity)
                .addObject("propertyTypeEditBindingModel", propertyTypeEditBindingModel);
    }

    /**
     * PropertyType edit in Administration -> Properties -> PROPERTY TYPES -> edit fee
     *
     * @param id property type id
     * @return view administration - PROPERTY TYPES edit form (not redirect)
     */
    @PostMapping("/administration/property/types/edit/{id}")
    @PreAuthorize("@securityService.checkPropertyTypeModeratorAccess(#id, authentication)")
    public ModelAndView propertyTypeEdit(@ModelAttribute("propertyTypeEditBindingModel")
                                         @Valid PropertyTypeEditBindingModel propertyTypeEditBindingModel,
                                         BindingResult bindingResult,
                                         @PathVariable("id") Long id,
                                         @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ResidentialEntity residentialEntity = propertyTypeService.findResidentialEntityByPropertyType(id);

        ModelAndView resultView = new ModelAndView(lang + "/administration/administration-property-types-edit")
                .addObject("residentialEntity", residentialEntity)
                .addObject("propertyTypeEditBindingModel", propertyTypeEditBindingModel);

        if (bindingResult.hasErrors()) {
            return resultView;
        }

        if (propertyTypeService.editPropertyType(id, propertyTypeEditBindingModel)) {
            return new ModelAndView("redirect:/administration/property/types/" + residentialEntity.getId() + "#property-types");
        }
        return resultView.addObject("editFailed", true);

    }

    /**
     * PropertyType edit in Administration -> Monthly fees -> RE fees -> Set/Modify fees
     *
     * @param id property type id
     * @return view administration - PROPERTY TYPES edit (REDIRECT) form
     */
    @GetMapping("/administration/property/types/redirect/edit/{id}")
    @PreAuthorize("@securityService.checkPropertyTypeModeratorAccess(#id, authentication)")
    public ModelAndView propertyTypeRedirectEdit(@PathVariable("id") Long id,
                                                 @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        PropertyTypeEditBindingModel propertyTypeEditBindingModel = propertyTypeService.mapPropertyTypeToEditBindingModel(id);
        ResidentialEntity residentialEntity = propertyTypeService.findResidentialEntityByPropertyType(id);

        return new ModelAndView(lang + "/administration/administration-property-types-redirect-edit")
                .addObject("residentialEntity", residentialEntity)
                .addObject("propertyTypeEditBindingModel", propertyTypeEditBindingModel);
    }

    /**
     * PropertyType edit in Administration -> Monthly fees -> RE fees -> Set/Modify fees
     *
     * @param id property type id
     * @return view administration - PROPERTY TYPES edit (REDIRECT) form
     */
    @PostMapping("/administration/property/types/redirect/edit/{id}")
    @PreAuthorize("@securityService.checkPropertyTypeModeratorAccess(#id, authentication)")
    public ModelAndView propertyTypeRedirectEdit(@ModelAttribute("propertyTypeEditBindingModel")
                                                 @Valid PropertyTypeEditBindingModel propertyTypeEditBindingModel,
                                                 BindingResult bindingResult,
                                                 @PathVariable("id") Long id,
                                                 @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ResidentialEntity residentialEntity = propertyTypeService.findResidentialEntityByPropertyType(id);

        ModelAndView resultView = new ModelAndView(lang + "/administration/administration-property-types-redirect-edit")
                .addObject("residentialEntity", residentialEntity)
                .addObject("propertyTypeEditBindingModel", propertyTypeEditBindingModel);

        if (bindingResult.hasErrors()) {
            return resultView;
        }

        if (propertyTypeService.editPropertyType(id, propertyTypeEditBindingModel)) {
            return new ModelAndView("redirect:/administration/fees/edit/" + residentialEntity.getId() + "#post-nav");
        }

        return resultView.addObject("editFailed", true);
    }

    /**
     * PropertyType edit in Administration -> Properties -> PROPERTY TYPES -> delete fee
     *
     * @param id property type id
     * @return view administration - PROPERTY TYPES delete
     */
    @PostMapping("/administration/property/types/delete/{id}")
    @PreAuthorize("@securityService.checkPropertyTypeModeratorAccess(#id, authentication)")
    public ModelAndView propertyTypeDelete(@PathVariable("id") Long id) {

        ResidentialEntity residentialEntity = propertyTypeService.findResidentialEntityByPropertyType(id);

        //detach register/change requests from propertyTypes.
        propertyRegisterRequestService.detachPropertyType(id);
        propertyChangeRequestService.detachPropertyType(id);

        propertyTypeService.deletePropertyType(id);

        return new ModelAndView("redirect:/administration/property/types/" + residentialEntity.getId() + "#property-types");
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
     * Language resolver
     * @param lang This value shows the language
     * @return boolean
     */
    private boolean resolveView(String lang) {
        return "bg".equals(lang);
    }
}
