package com.example.OurHome.controller;

import com.example.OurHome.model.Entity.Event;
import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.Event.EventAddBindingModel;
import com.example.OurHome.model.dto.BindingModels.Event.EventEditBindingModel;
import com.example.OurHome.repo.EventRepository;
import com.example.OurHome.repo.PropertyRepository;
import com.example.OurHome.service.EventService;
import com.example.OurHome.service.PropertyService;
import com.example.OurHome.service.ResidentialEntityService;
import com.example.OurHome.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class EventController {

    private final ResidentialEntityService residentialEntityService;
    private final UserService userService;
    private final EventService eventService;
    private final EventRepository eventRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyService propertyService;

    public EventController(ResidentialEntityService residentialEntityService, UserService userService, EventService eventService, EventRepository eventRepository, PropertyRepository propertyRepository, PropertyService propertyService) {
        this.residentialEntityService = residentialEntityService;
        this.userService = userService;
        this.eventService = eventService;
        this.eventRepository = eventRepository;
        this.propertyRepository = propertyRepository;
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
     * Add new event
     *
     * @param id property id
     * @return view property - list of all events
     */
    @GetMapping("/administration/events/add/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView addEvent(@PathVariable("id") Long id,
                                 @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        EventAddBindingModel eventAddBindingModel = new EventAddBindingModel();

        return new ModelAndView(lang + "/administration/administration-events-add")
                .addObject("residentialEntity", getResidentialEntity(id))
                .addObject("eventAddBindingModel", eventAddBindingModel);
    }

    /**
     * Add new event
     *
     * @param id event id
     * @return view property - list of all events
     */
    @PostMapping("/administration/events/add/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView addEvent(@PathVariable("id") Long id,
                                 @ModelAttribute("eventAddBindingModel") EventAddBindingModel eventAddBindingModel,
                                 @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        try {
            // Извикваме сервиза за създаване на Event
            eventService.createEvent(eventAddBindingModel, id);
        } catch (IllegalArgumentException e) {
            // Обработка на грешка
            return new ModelAndView(lang + "/administration/administration-events-add")
                    .addObject("eventAddBindingModel", eventAddBindingModel)
                    .addObject("addFailed", true)
                    .addObject("errorMsg", e.getMessage());
        }
        return new ModelAndView("redirect:/administration/events/" + id);
    }


    /**
     * Delete event
     *
     * @param id event id
     * @return view property - list of all events
     */
    @PostMapping("/administration/events/delete/{id}")
    @PreAuthorize("@securityService.checkEventModeratorAccess(#id, authentication)")
    public ModelAndView deleteEvent(@PathVariable("id") Long id,
                                    @ModelAttribute("eventAddBindingModel") EventAddBindingModel eventAddBindingModel,
                                    @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        Event event = eventRepository.findById(id).orElse(null);
        Long residentialEntityId = null;
        if (event != null) {
            residentialEntityId = event.getResidentialEntity().getId();
        }

        try {
            // Извикваме сервиза за обработка и валидиране на данните
            eventService.deleteEvent(event);
        } catch (IllegalArgumentException e) {

            return new ModelAndView(lang + "/administration/administration-events-add")
                    .addObject("eventAddBindingModel", eventAddBindingModel)
                    .addObject("deletionFailed", true)
                    .addObject("errorMsg", e.getMessage());
        }
        return new ModelAndView("redirect:/administration/events/" + residentialEntityId);
    }


    /**
     * Edit event
     *
     * @param id event id
     * @return view property - list of all events
     */
    @GetMapping("/administration/events/edit/{id}")
    @PreAuthorize("@securityService.checkEventModeratorAccess(#id, authentication)")
    public ModelAndView editEvent(@PathVariable("id") Long id,
                                  @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        Event event = eventRepository.findById(id).orElse(null);
        Long residentialEntityId = null;
        if (event != null) {
            residentialEntityId = event.getResidentialEntity().getId();
        }

        EventEditBindingModel eventEditBindingModel = eventService.mapEventToBindingModel(id);

        return new ModelAndView(lang + "/administration/administration-events-edit")
                .addObject("residentialEntityId", residentialEntityId)
                .addObject("eventEditBindingModel", eventEditBindingModel);
    }

    /**
     * Edit event
     *
     * @param id event id
     * @return view property - list of all events
     */
    @PostMapping("/administration/events/edit/{id}")
    @PreAuthorize("@securityService.checkEventModeratorAccess(#id, authentication)")
    public ModelAndView editEvent(@PathVariable("id") Long id,
                                  @ModelAttribute("eventEditBindingModel") EventEditBindingModel eventEditBindingModel,
                                  @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        Event event = eventRepository.findById(id).orElse(null);
        Long residentialEntityId = null;
        if (event != null) {
            residentialEntityId = event.getResidentialEntity().getId();
        }

        try {
            // Извикваме сервиза за обработка и валидиране на данните
            eventService.editEvent(event, eventEditBindingModel);
        } catch (IllegalArgumentException e) {
            // Обработка на грешка

            return new ModelAndView(lang + "/administration/administration-events-edit")
                    .addObject("eventEditBindingModel", eventEditBindingModel)
                    .addObject("editFailed", true)
                    .addObject("errorMsg", e.getMessage());
        }
        return new ModelAndView("redirect:/administration/events/" + residentialEntityId);
    }

    /**
     * Events in Property
     *
     * @param id Condominium id
     * @return view property -> events
     */
    @GetMapping("/property/events/{id}")
    @PreAuthorize("@securityService.checkPropertyOwnerAccess(#id, authentication)")
    public ModelAndView reports(@PathVariable("id") Long id,
                                @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        Property property = propertyRepository.findById(id).orElse(null);
        assert property != null;

        return new ModelAndView(lang + "/property/events")
                .addObject("property", getProperty(id))
                .addObject("residentialEntity", getResidentialEntity(property.getResidentialEntity().getId()));
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
     * This private method returns a Property by id
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
