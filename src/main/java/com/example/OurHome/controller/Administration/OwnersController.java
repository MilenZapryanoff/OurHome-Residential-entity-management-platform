package com.example.OurHome.controller.Administration;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyManageBindingModel;
import com.example.OurHome.model.dto.BindingModels.ResidentialEntity.ResidentManageBindingModel;
import com.example.OurHome.service.PropertyService;
import com.example.OurHome.service.ResidentialEntityService;
import com.example.OurHome.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class OwnersController {

    private final UserService userService;
    private final ResidentialEntityService residentialEntityService;
    private final PropertyService propertyService;

    public OwnersController(UserService userService, ResidentialEntityService residentialEntityService, PropertyService propertyService) {
        this.userService = userService;
        this.residentialEntityService = residentialEntityService;
        this.propertyService = propertyService;
    }

    /**
     * @param id residential entity (RE) id
     * @return view administration-owners
     */
    @GetMapping("/administration/owners/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityOwnersDetails(@ModelAttribute("residentManageBindingModel") ResidentManageBindingModel residentManageBindingModel,
                                                       @PathVariable("id") Long id,
                                                       @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-owners") : new ModelAndView("en/administration/administration-owners");

        return view.addObject("residentialEntity", getResidentialEntity(id));
    }

    /**
     * Owners details in Administration
     *
     * @param id property id
     * @return view administration- pending owner registrations
     */
    @GetMapping("/administration/owners/pending/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityPendingOwnerRegistrations(@ModelAttribute("propertyManageBindingModel") PropertyManageBindingModel propertyManageBindingModel,
                                                                   @PathVariable("id") Long id,
                                                                   @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-owners-pending") : new ModelAndView("en/administration/administration-owners-pending");

        return view.addObject("residentialEntity", getResidentialEntity(id));
    }

    /**
     * Owners details in Administration
     *
     * @param id property id
     * @return view administration- pending owner registrations
     */
    @GetMapping("/administration/owners/pending/request/{id}")
    @PreAuthorize("@securityService.checkPropertyModeratorAccess(#id, authentication)")
    public ModelAndView propertyOwnerRegistrationRequest(@ModelAttribute("propertyManageBindingModel") PropertyManageBindingModel propertyManageBindingModel,
                                                         @PathVariable("id") Long id,
                                                         @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-owners-pending-request") : new ModelAndView("en/administration/administration-owners-pending-request");

        return view.addObject("property", getProperty(id));
    }

    /**
     * Property details in Administration
     *
     * @param id property id
     * @return view administration- rejected properties
     */
    @GetMapping("/administration/owners/rejected/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityRejectedOwnerRegistrations(@ModelAttribute("propertyManageBindingModel") PropertyManageBindingModel propertyManageBindingModel,
                                                                    @PathVariable("id") Long id,
                                                                    @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-owners-rejected") : new ModelAndView("en/administration/administration-owners-rejected");

        return view.addObject("residentialEntity", getResidentialEntity(id));
    }

    /**
     * Owner registration approve with applying changes from request
     *
     * @param propertyManageBindingModel carries information about the entityId
     * @param id                         property id
     * @return "redirect:/administration/owners/{entityId}"
     */
    @PostMapping("/administration/owners/approve-with-changes/{id}")
    @PreAuthorize("@securityService.checkPropertyModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityPropertyApproveWithDataChange(@ModelAttribute("propertyManageBindingModel") PropertyManageBindingModel propertyManageBindingModel,
                                                                       @PathVariable("id") Long id) {

        propertyService.approvePropertyRegistrationWithDataChange(id, true);

        return new ModelAndView("redirect:/administration/owners/pending/" + propertyManageBindingModel.getEntityId() + "#pending-registrations");
    }

    /**
     * Owner registration approve without applying changes from request (ignoring request changes)
     *
     * @param propertyManageBindingModel carries information about the entityId
     * @param id                         property id
     * @return "redirect:/administration/owners/{entityId}"
     */
    @PostMapping("/administration/owners/approve-without-changes/{id}")
    @PreAuthorize("@securityService.checkPropertyModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityPropertyApproveWithoutDataChange(@ModelAttribute("propertyManageBindingModel") PropertyManageBindingModel propertyManageBindingModel,
                                                                          @PathVariable("id") Long id) {

        propertyService.approvePropertyRegistrationWithoutDataChange(id);

        return new ModelAndView("redirect:/administration/owners/pending/" + propertyManageBindingModel.getEntityId() + "#pending-registrations");
    }

    /**
     * Owner registration reject
     *
     * @param propertyManageBindingModel carries information about the entityId
     * @param id                         property id
     * @return "redirect:/administration/property/{entityId}"
     */
    @PostMapping("/administration/owners/reject/{id}")
    @PreAuthorize("@securityService.checkPropertyModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityPropertyReject(@ModelAttribute("propertyManageBindingModel") PropertyManageBindingModel propertyManageBindingModel,
                                                        @PathVariable("id") Long id) {

        propertyService.rejectProperty(id);

        return new ModelAndView("redirect:/administration/owners/pending/" + propertyManageBindingModel.getEntityId() + "#pending-registrations");
    }

    /**
     * Handles role change requests in RE
     *
     * @param residentManageBindingModel carries information about the RE id
     * @param id                         resident(owner) id
     * @return redirect:/administration/owners/{entityId}
     */

    @PostMapping("/administration/owners/edit_role/{id}")
    @PreAuthorize("@securityService.checkResidentModeratorAccess(#id, authentication)")
    public ModelAndView changeUserRole(@ModelAttribute("residentManageBindingModel")
                                       @Valid ResidentManageBindingModel residentManageBindingModel,
                                       @PathVariable("id") Long id) {


        if (residentManageBindingModel.getUserId() != null && residentManageBindingModel.getEntityId() != null) {
            userService.createModerator(residentManageBindingModel.getUserId(), residentManageBindingModel.getEntityId());
        } else {
            userService.removeModerator(id, residentManageBindingModel.getEntityId());
        }

        return new ModelAndView("redirect:/administration/owners/" + residentManageBindingModel.getEntityId() + "#post-action-nav");
    }

    /**
     * Remove resident from RE.
     *
     * @param residentManageBindingModel carries information about the RE id
     * @param id                         resident(owner) id
     * @return administration-owners
     */
    @PostMapping("/administration/owners/delete/{id}")
    @PreAuthorize("@securityService.checkResidentModeratorAccess(#id, authentication)")
    public ModelAndView deleteResident(@ModelAttribute("residentManageBindingModel") ResidentManageBindingModel residentManageBindingModel,
                                       @PathVariable("id") Long id,
                                       @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ResidentialEntity residentialEntity = getResidentialEntity(residentManageBindingModel.getEntityId());

        //remove user form RE
        userService.removeResidentFromResidentialEntity(id, residentialEntity);
        //unlink properties from user
        propertyService.unlinkAllPropertiesFromOwner(id, residentialEntity);

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-owners") : new ModelAndView("en/administration/administration-owners");

        return view
                .addObject("residentialEntity", getResidentialEntity(residentManageBindingModel.getEntityId()))
                .addObject("residentRemoved", true);
    }

    /**
     * Unlink property owner
     *
     * @param propertyManageBindingModel carries information about the entityId
     * @param id                         property id
     * @return "redirect:/administration/property/{entityId}"
     */
    @PostMapping("/administration/owners/property/unlink/{id}")
    @PreAuthorize("@securityService.checkPropertyModeratorAccess(#id, authentication)")
    public ModelAndView unlinkPropertyOwner(@ModelAttribute("propertyManageBindingModel") PropertyManageBindingModel propertyManageBindingModel,
                                            @PathVariable("id") Long id) {


        propertyService.unlinkOwner(id, true);

        return new ModelAndView("redirect:/administration/owners/rejected/" + propertyManageBindingModel.getEntityId() + "#rejected-registration");
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

    /**
     * Language resolver
     * @param lang This value shows the language
     * @return boolean
     */
    private boolean resolveView(String lang) {
        return "bg".equals(lang);
    }
}