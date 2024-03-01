package com.example.OurHome.controller.Administration;

import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.ResidentialEntity.ResidentManageBindingModel;
import com.example.OurHome.model.dto.ViewModels.UserViewModel;
import com.example.OurHome.service.PropertyService;
import com.example.OurHome.service.ResidentialEntityService;
import com.example.OurHome.service.UserService;
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
                                                       @PathVariable("id") Long id) {

        return new ModelAndView("administration-owners")
                .addObject("userViewModel", getUserViewModel())
                .addObject("residentialEntity", getResidentialEntity(id));
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
    public ModelAndView deleteResident(@ModelAttribute("residentManageBindingModel") ResidentManageBindingModel residentManageBindingModel, @PathVariable("id") Long id) {

        //remove user form RE
        userService.removeResidentFromResidentialEntity(id, getResidentialEntity(residentManageBindingModel.getEntityId()));
        //delete user's properties in this RE
        propertyService.deletePropertiesWhenResidentRemoved(id, residentManageBindingModel.getEntityId());

        return new ModelAndView("administration-owners")
                .addObject("userViewModel", getUserViewModel())
                .addObject("residentialEntity", getResidentialEntity(residentManageBindingModel.getEntityId()))
                .addObject("residentRemoved", true);
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