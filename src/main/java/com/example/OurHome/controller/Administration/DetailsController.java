package com.example.OurHome.controller.Administration;

import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.dto.BindingModels.ResidentialEntity.ResidentialEntityEditBindingModel;
import com.example.OurHome.service.ResidentialEntityService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DetailsController {

    private final ResidentialEntityService residentialEntityService;

    public DetailsController(ResidentialEntityService residentialEntityService) {
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
    public ModelAndView residentialEntityDetails(@PathVariable("id") Long id,
                                                 @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-details") : new ModelAndView("en/administration/administration-details");

        return view.addObject("residentialEntity", getResidentialEntity(id));
    }

    /**
     * Condominium edit form in Administration
     *
     * @param id property id
     * @return view administration-details-edit
     */
    @GetMapping("/administration/details/edit/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityEditDetails(@PathVariable("id") Long id,
                                                     @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ResidentialEntityEditBindingModel residentialEntityEditBindingModel = residentialEntityService.mapEntityToEditBindingModel(getResidentialEntity(id));

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-details-edit") : new ModelAndView("en/administration/administration-details-edit");

        return view
                .addObject("residentialEntity", getResidentialEntity(id))
                .addObject("residentialEntityEditBindingModel", residentialEntityEditBindingModel);
    }

    /**
     * Condominium edit form in Administration
     *
     * @param residentialEntityEditBindingModel carries info about new values
     * @param entityId                          RE id
     * @param bindingResult                     result
     * @return redirect:/administration/details/{entityId}
     */
    @PostMapping("/administration/details/edit/{entityId}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#entityId, authentication)")
    public ModelAndView residentialEntityEditDetailsPost(@ModelAttribute("residentialEntityEditBindingModel")
                                                         @Valid ResidentialEntityEditBindingModel residentialEntityEditBindingModel,
                                                         BindingResult bindingResult,
                                                         @PathVariable("entityId") Long entityId,
                                                         @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-details-edit") : new ModelAndView("en/administration/administration-details-edit");

        view.addObject("residentialEntity", getResidentialEntity(entityId))
                .addObject("residentialEntityEditBindingModel", residentialEntityEditBindingModel);

        String accessCode = residentialEntityEditBindingModel.getAccessCode();
        String confirmAccessCode = residentialEntityEditBindingModel.getConfirmAccessCode();

        if (bindingResult.hasErrors()) {
            return view;
        } else if (!accessCode.isEmpty()) {
            if (accessCode.length() <= 3 || accessCode.length() >= 20) {
                return view.addObject("bad_accessCode", true);
            }
            if (!residentialEntityService.accessCodesMatchCheck(accessCode, confirmAccessCode)) {
                return view.addObject("noAccessCodeMatch", true);
            }
        }
        residentialEntityService.editResidentialEntity(entityId, residentialEntityEditBindingModel);

        return new ModelAndView("redirect:/administration/details/" + entityId);
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
     *
     * @param lang This value shows the language
     * @return boolean
     */
    private boolean resolveView(String lang) {
        return "bg".equals(lang);
    }
}