package com.example.OurHome.controller.Administration;

import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.PropertyType.PropertyTypeAddBindingModel;
import com.example.OurHome.model.dto.BindingModels.PropertyType.PropertyTypeEditBindingModel;
import com.example.OurHome.model.dto.ViewModels.UserViewModel;
import com.example.OurHome.service.*;
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
public class PropertyTypeController {

    private final UserService userService;
    private final PropertyTypeService propertyTypeService;
    private final ResidentialEntityService residentialEntityService;
    private final PropertyRegisterRequestService propertyRegisterRequestService;
    private final PropertyChangeRequestService propertyChangeRequestService;

    public PropertyTypeController(UserService userService, PropertyTypeService propertyTypeService, ResidentialEntityService residentialEntityService, PropertyRegisterRequestService propertyRegisterRequestService, PropertyChangeRequestService propertyChangeRequestService) {
        this.userService = userService;
        this.propertyTypeService = propertyTypeService;
        this.residentialEntityService = residentialEntityService;
        this.propertyRegisterRequestService = propertyRegisterRequestService;
        this.propertyChangeRequestService = propertyChangeRequestService;
    }


    /**
     * PropertyType in Administration
     *
     * @param id residential entity id
     * @return view administration - PROPERTY TYPES
     */
    @GetMapping("/administration/property/types/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityPropertyTypes(@PathVariable("id") Long id) {

        return new ModelAndView("administration/administration-property-types")
                .addObject("userViewModel", getUserViewModel())
                .addObject("residentialEntity", getResidentialEntity(id));
    }

    /**
     * Add (new) new propertyType in Administration -> Properties -> PROPERTY TYPES
     *
     * @param id residential entity id
     * @return view administration - PROPERTY TYPES add form
     */
    @GetMapping("/administration/property/types/add/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView propertyTypeAdd(@PathVariable("id") Long id) {

        PropertyTypeAddBindingModel propertyTypeAddBindingModel = new PropertyTypeAddBindingModel();

        return new ModelAndView("administration/administration-property-types-add")
                .addObject("userViewModel", getUserViewModel())
                .addObject("residentialEntity", getResidentialEntity(id))
                .addObject("propertyTypeAddBindingModel", propertyTypeAddBindingModel);
    }

    /**
     * Add (new) new propertyType in Administration -> Properties -> PROPERTY TYPES
     *
     * @param id residential entity id
     * @return view administration - PROPERTY TYPES add form
     */
    @PostMapping("/administration/property/types/add/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView propertyTypeAdd(@ModelAttribute("propertyTypeAddBindingModel")
                                        @Valid PropertyTypeAddBindingModel propertyTypeAddBindingModel,
                                        BindingResult bindingResult,
                                        @PathVariable("id") Long id) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("administration/administration-property-types-add")
                    .addObject("userViewModel", getUserViewModel())
                    .addObject("residentialEntity", getResidentialEntity(id))
                    .addObject("propertyTypeAddBindingModel", propertyTypeAddBindingModel);
        }
        if (propertyTypeService.addPropertyType(id, propertyTypeAddBindingModel)) {
            return new ModelAndView("redirect:/administration/property/types/" + id);
        } else {
            return new ModelAndView("administration/administration-property-types-add")
                    .addObject("userViewModel", getUserViewModel())
                    .addObject("propertyTypeAddBindingModel", propertyTypeAddBindingModel)
                    .addObject("residentialEntity", getResidentialEntity(id))
                    .addObject("editFailed", true);
        }
    }

    /**
     * PropertyType edit in Administration -> Properties -> PROPERTY TYPES -> edit fee
     *
     * @param id property type id
     * @return view administration - PROPERTY TYPES edit form (not redirect)
     */
    @GetMapping("/administration/property/types/edit/{id}")
    @PreAuthorize("@securityService.checkPropertyTypeModeratorAccess(#id, authentication)")
    public ModelAndView propertyTypeEdit(@PathVariable("id") Long id) {

        PropertyTypeEditBindingModel propertyTypeEditBindingModel = propertyTypeService.mapPropertyTypeToEditBindingModel(id);
        ResidentialEntity residentialEntity = propertyTypeService.findResidentialEntityByPropertyType(id);

        return new ModelAndView("administration/administration-property-types-edit")
                .addObject("userViewModel", getUserViewModel())
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
                                         @PathVariable("id") Long id) {

        ResidentialEntity residentialEntity = propertyTypeService.findResidentialEntityByPropertyType(id);

        if (bindingResult.hasErrors()) {
            return new ModelAndView("administration/administration-property-types-edit")
                    .addObject("userViewModel", getUserViewModel())
                    .addObject("residentialEntity", residentialEntity)
                    .addObject("propertyTypeEditBindingModel", propertyTypeEditBindingModel);
        }

        if (propertyTypeService.editPropertyType(id, propertyTypeEditBindingModel)) {
            return new ModelAndView("redirect:/administration/property/types/" + residentialEntity.getId() + "#property-types");
        } else {
            return new ModelAndView("administration/administration-property-types-edit")
                    .addObject("userViewModel", getUserViewModel())
                    .addObject("propertyTypeEditBindingModel", propertyTypeEditBindingModel)
                    .addObject("residentialEntity", residentialEntity)
                    .addObject("editFailed", true);
        }
    }

    /**
     * PropertyType edit in Administration -> Monthly fees -> RE fees -> Set/Modify fees
     *
     * @param id property type id
     * @return view administration - PROPERTY TYPES edit (REDIRECT) form
     */
    @GetMapping("/administration/property/types/redirect/edit/{id}")
    @PreAuthorize("@securityService.checkPropertyTypeModeratorAccess(#id, authentication)")
    public ModelAndView propertyTypeRedirectEdit(@PathVariable("id") Long id) {

        PropertyTypeEditBindingModel propertyTypeEditBindingModel = propertyTypeService.mapPropertyTypeToEditBindingModel(id);
        ResidentialEntity residentialEntity = propertyTypeService.findResidentialEntityByPropertyType(id);

        return new ModelAndView("administration/administration-property-types-redirect-edit")
                .addObject("userViewModel", getUserViewModel())
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
                                                 @PathVariable("id") Long id) {

        ResidentialEntity residentialEntity = propertyTypeService.findResidentialEntityByPropertyType(id);

        if (bindingResult.hasErrors()) {
            return new ModelAndView("administration/administration-property-types-redirect-edit")
                    .addObject("userViewModel", getUserViewModel())
                    .addObject("residentialEntity", residentialEntity)
                    .addObject("propertyTypeEditBindingModel", propertyTypeEditBindingModel);
        }

        if (propertyTypeService.editPropertyType(id, propertyTypeEditBindingModel)) {
            return new ModelAndView("redirect:/administration/fees/edit/" + residentialEntity.getId() + "#post-nav");
        } else {
            return new ModelAndView("administration/administration-property-types-edit")
                    .addObject("userViewModel", getUserViewModel())
                    .addObject("propertyTypeEditBindingModel", propertyTypeEditBindingModel)
                    .addObject("residentialEntity", residentialEntity)
                    .addObject("editFailed", true);
        }
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

}
