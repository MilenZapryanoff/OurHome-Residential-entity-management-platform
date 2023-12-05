package com.example.OurHome.controller.Administration;

import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.ResidentialEntity.ResidentialEntityEditBindingModel;
import com.example.OurHome.model.dto.ViewModels.UserViewModel;
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
public class DetailsController {

    private final UserService userService;
    private final ResidentialEntityService residentialEntityService;

    public DetailsController(UserService userService, ResidentialEntityService residentialEntityService) {
        this.userService = userService;
        this.residentialEntityService = residentialEntityService;
    }

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
     *
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
     *
     * @param residentialEntityEditBindingModel carries info about new values
     * @param entityId                          RE id
     * @param bindingResult                     result
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