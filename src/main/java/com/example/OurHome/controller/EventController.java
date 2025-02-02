package com.example.OurHome.controller;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.Report;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.Reports.ReportAddBindingModel;
import com.example.OurHome.model.dto.BindingModels.Reports.ReportEditBindingModel;
import com.example.OurHome.repo.ReportRepository;
import com.example.OurHome.service.PropertyService;
import com.example.OurHome.service.ReportService;
import com.example.OurHome.service.ResidentialEntityService;
import com.example.OurHome.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

@Controller
public class EventController {

    private final ResidentialEntityService residentialEntityService;
    private final UserService userService;
    private final PropertyService propertyService;

    public EventController(ResidentialEntityService residentialEntityService, UserService userService, PropertyService propertyService) {
        this.residentialEntityService = residentialEntityService;
        this.userService = userService;
        this.propertyService = propertyService;
    }

    /**
     * Events in Administration
     *
     * @param id Condominium id
     * @return view administration - PROPERTY TYPES
     */
    @GetMapping("/administration/events/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityReports(@PathVariable("id") Long id,
                                                 @CookieValue(value = "lang", defaultValue = "bg") String lang) {
        return new ModelAndView(lang + "/administration/administration-events")
                .addObject("residentialEntity", getResidentialEntity(id));
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
     * Method returns currently logged user
     *
     * @return UserEntity
     */
    private UserEntity getLoggedUser() {
        return userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
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

    /**
     * This private method returns a Property by id
     *
     * @param id property id
     * @return Property
     */
    private Property getProperty(Long id) {

        return propertyService.findPropertyById(id);
    }
}
