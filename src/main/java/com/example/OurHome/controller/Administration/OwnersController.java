package com.example.OurHome.controller.Administration;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyManageBindingModel;
import com.example.OurHome.model.dto.BindingModels.ResidentialEntity.ResidentManageBindingModel;
import com.example.OurHome.service.PropertyService;
import com.example.OurHome.service.ReportService;
import com.example.OurHome.service.ResidentialEntityService;
import com.example.OurHome.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class OwnersController {

    private final UserService userService;
    private final ResidentialEntityService residentialEntityService;
    private final PropertyService propertyService;
    private final ReportService reportService;

    public OwnersController(UserService userService, ResidentialEntityService residentialEntityService, PropertyService propertyService, ReportService reportService) {
        this.userService = userService;
        this.residentialEntityService = residentialEntityService;
        this.propertyService = propertyService;
        this.reportService = reportService;
    }

    /**
     * View list of users with access to condominium
     *
     * @param id Condominium (RE) id
     * @return view administration-owners
     */
    @GetMapping("/administration/owners/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityOwners(@ModelAttribute("residentManageBindingModel") ResidentManageBindingModel residentManageBindingModel,
                                                @PathVariable("id") Long id,
                                                @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-owners") : new ModelAndView("en/administration/administration-owners");

        return view.addObject("residentialEntity", getResidentialEntity(id));
    }


    /**
     * View user detailed information
     *
     * @param id user id
     * @return view administration-owners-details
     */
    @GetMapping("/administration/owners/details/{id}")
    @PreAuthorize("@securityService.checkResidentDetailsModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityOwnersDetails(@ModelAttribute("residentManageBindingModel") ResidentManageBindingModel residentManageBindingModel,
                                                       @PathVariable("id") Long id,
                                                       @CookieValue(value = "lang", defaultValue = "bg") String lang,
                                                       @RequestParam(value = "resId", required = false) Long resId,
                                                       @RequestParam(value = "redirect", required = false) boolean redirect) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-owners-details") : new ModelAndView("en/administration/administration-owners-details");

        return view.addObject("resident", userService.findUserById(id))
                .addObject("residentialEntity", getResidentialEntity(resId))
                .addObject("redirect", redirect);
    }

    /**
     * Owners details in Administration
     *
     * @param id property id
     * @return view administration- pending owner registrations
     */
    @GetMapping("/administration/property/owners/pending/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityPendingOwnerRegistrations(@ModelAttribute("propertyManageBindingModel") PropertyManageBindingModel propertyManageBindingModel,
                                                                   @PathVariable("id") Long id,
                                                                   @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-property-owners-pending") : new ModelAndView("en/administration/administration-property-owners-pending");

        return view.addObject("residentialEntity", getResidentialEntity(id));
    }

    /**
     * Owners details in Administration
     *
     * @param id property id
     * @return view administration- pending owner registrations
     */
    @GetMapping("/administration/property/owners/pending/request/{id}")
    @PreAuthorize("@securityService.checkPropertyModeratorAccess(#id, authentication)")
    public ModelAndView propertyOwnerRegistrationRequest(@ModelAttribute("propertyManageBindingModel") PropertyManageBindingModel propertyManageBindingModel,
                                                         @PathVariable("id") Long id,
                                                         @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-property-owners-pending-request") : new ModelAndView("en/administration/administration-property-owners-pending-request");

        return view.addObject("property", getProperty(id));
    }

    /**
     * Property details in Administration
     *
     * @param id property id
     * @return view administration- rejected properties
     */
    @GetMapping("/administration/property/owners/rejected/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityRejectedOwnerRegistrations(@ModelAttribute("propertyManageBindingModel") PropertyManageBindingModel propertyManageBindingModel,
                                                                    @PathVariable("id") Long id,
                                                                    @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-property-owners-rejected") : new ModelAndView("en/administration/administration-property-owners-rejected");

        return view.addObject("residentialEntity", getResidentialEntity(id));
    }


    /**
     * Processing property owner registration request.
     *
     * @param propertyManageBindingModel carries information about the entityId
     * @param id                         property id
     * @return "redirect:/administration/property/owners/pending/{entityId}"
     */
    @PostMapping("/administration/owners/process-request/{id}")
    @PreAuthorize("@securityService.checkPropertyModeratorAccess(#id, authentication)")
    public ModelAndView propertyRegistrationRequestProcessing(@ModelAttribute("propertyManageBindingModel") PropertyManageBindingModel propertyManageBindingModel,
                                                              @PathVariable("id") Long id) {

        Property property = propertyService.findPropertyById(id);

        if (propertyManageBindingModel.getAction().equals("approveWithChange")) {
            propertyService.approvePropertyRegistrationWithDataChange(id, true);
        }
        if (propertyManageBindingModel.getAction().equals("approveWithNoChange") && property.isValidated()) {
            propertyService.approvePropertyRegistrationWithoutDataChange(id);
        }
        if (propertyManageBindingModel.getAction().equals("reject")) {
            propertyService.rejectProperty(id);
        }

        return new ModelAndView("redirect:/administration/property/owners/pending/" + property.getResidentialEntity().getId() + "#pending-registrations");
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
                                       @PathVariable("id") Long id,
                                       @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        boolean userIsModerator = residentialEntityService.checkIfUserIsResidentialEntityModerator(residentManageBindingModel.getEntityId(), residentManageBindingModel.getUserId());

        if (!userIsModerator) {
            userService.createModerator(residentManageBindingModel.getUserId(), residentManageBindingModel.getEntityId());
        } else {
            userService.removeModerator(id, residentManageBindingModel.getEntityId());
        }

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-owners-details") : new ModelAndView("en/administration/administration-owners-details");

        ResidentialEntity residentialEntityById = residentialEntityService.findResidentialEntityById(residentManageBindingModel.getEntityId()).orElse(null);


        view.addObject("residentialEntity", residentialEntityById)
                .addObject("resident", userService.findUserById(id))
                .addObject("userPrivilegesChanged", true);

        return view;
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
        //unlink reports from user
        reportService.unlinkAllReportsFromOwner(id, residentialEntity);

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-owners") : new ModelAndView("en/administration/administration-owners");

        return view
                .addObject("residentialEntity", getResidentialEntity(residentManageBindingModel.getEntityId()))
                .addObject("residentRemoved", true);
    }

    /**
     * Unlink property owner from property when registration request is deleted. The owner remains part of the condominium, but the property ownership is reset to null.
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

        return new ModelAndView("redirect:/administration/property/owners/rejected/" + propertyManageBindingModel.getEntityId() + "#rejected-registration");
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
     * Language resolver
     *
     * @param lang This value shows the language
     * @return boolean
     */
    private boolean resolveView(String lang) {
        return "bg".equals(lang);
    }
}