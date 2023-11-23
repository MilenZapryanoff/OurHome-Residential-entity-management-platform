package com.example.OurHome.controller;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.Entity.dto.BindingModels.*;
import com.example.OurHome.model.Entity.dto.ViewModels.UserViewModel;
import com.example.OurHome.service.MessageService;
import com.example.OurHome.service.PropertyService;
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
public class PropertyController {

    private final UserService userService;
    private final PropertyService propertyService;
    private final MessageService messageService;


    public PropertyController(UserService userService, PropertyService propertyService, MessageService messageService) {
        this.userService = userService;
        this.propertyService = propertyService;
        this.messageService = messageService;
    }

    @GetMapping("/property")
    public ModelAndView property() {

        return new ModelAndView("property", "userViewModel", getUserViewModel());
    }

    @GetMapping("/property/add")
    public ModelAndView addProperty(@ModelAttribute("propertyRegisterBindingModel") PropertyRegisterBindingModel propertyRegisterBindingModel) {

        return new ModelAndView("property-add", "userViewModel", getUserViewModel());
    }

    @PostMapping("/property/add")
    public ModelAndView addProperty(@ModelAttribute("propertyRegisterBindingModel")
                                    @Valid PropertyRegisterBindingModel propertyRegisterBindingModel, BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new ModelAndView("property-add", "userViewModel", getUserViewModel());
        }
        propertyService.newProperty(propertyRegisterBindingModel, getLoggedUser());

        return new ModelAndView("redirect:/property");
    }

    @GetMapping("/property/add/new")
    public ModelAndView addPropertyInNewEntity(@ModelAttribute("userAuthRegisterBindingModel") UserAuthBindingModel userAuthBindingModel) {

        return new ModelAndView("property-add-new-entity", "userViewModel", getUserViewModel());
    }

    @PostMapping("/property/add/new")
    public ModelAndView addPropertyInNewEntity(@ModelAttribute("userAuthRegisterBindingModel")
                                               @Valid UserAuthBindingModel userAuthBindingModel,
                                               BindingResult bindingResult) {

        Long residentialEntityId = userAuthBindingModel.parseResidentialIdToLong();
        String validationCode = userAuthBindingModel.getResidentialAccessCode();

        if (bindingResult.hasErrors()) {
            return new ModelAndView("property-add-new-entity", "userViewModel", getUserViewModel());
        } else if (!userService.residentialValidation(residentialEntityId, validationCode)) {
            return new ModelAndView("property-add-new-entity", "userViewModel", getUserViewModel())
                    .addObject("badResidentialEntity", true);

        }
        userService.joinUserToNewResidentialEntity(userAuthBindingModel, getLoggedUser());

        return new ModelAndView("redirect:/property/add");
    }

    /**
     * Property summary
     * GetMapping
     */
    @GetMapping("/property/summary/{id}")
    public ModelAndView residentialEntityDetails(
            @ModelAttribute("residentManageBindingModel") ResidentManageBindingModel residentManageBindingModel,
            @ModelAttribute("sendMessageBindingModel") SendMessageBindingModel sendMessageBindingModel,
            @PathVariable("id") Long id) {

        return new ModelAndView("property-summary", "userViewModel", getUserViewModel())
                .addObject("property", getProperty(id));
    }


    /**
     * @param sendMessageBindingModel
     * @param propertyId
     * @return
     */

    @PostMapping("/property/summary/messageToManager/{id}")
    @PreAuthorize("@securityService.checkMessageSenderAndReceiver(#propertyId, #sendMessageBindingModel.getSenderId() ,#sendMessageBindingModel.getReceiverId())")
    public ModelAndView sendMessageToPropertyManager(@ModelAttribute("sendMessageBindingModel")
                                                     SendMessageBindingModel sendMessageBindingModel,
                                                     @PathVariable("id") Long propertyId) {

        ModelAndView modelAndView = new ModelAndView("property-summary")
                .addObject("userViewModel", getUserViewModel())
                .addObject("property", getProperty(propertyId))
                .addObject("sendMessageBindingModel", sendMessageBindingModel);

        if (sendMessageBindingModel.getMessage().length() > 2000) {
            return modelAndView.addObject("messageError", true);
        }

        messageService.sendMessage(
                userService.findUserById(sendMessageBindingModel.getReceiverId()),
                userService.findUserById(sendMessageBindingModel.getSenderId()),
                sendMessageBindingModel.getMessage());

        return modelAndView.addObject("messageSent", true);
    }

    /**
     * Property details
     * GetMapping
     */
    @GetMapping("/property/details/{id}")
    public ModelAndView propertyDetails(@ModelAttribute("propertyManageBindingModel") ResidentManageBindingModel residentManageBindingModel, @PathVariable("id") Long id) {

        return new ModelAndView("property-details", "userViewModel", getUserViewModel())
                .addObject("property", getProperty(id));
    }

    /**
     * Property edit view
     * GetMapping
     */
    @GetMapping("/property/details/edit/{id}")
    public ModelAndView residentialEntityEditDetails(@PathVariable("id") Long id) {

        PropertyEditBindingModel propertyEditBindingModel = propertyService.mapPropertyToEditBindingModel(getProperty(id));

        return new ModelAndView("property-details-edit")
                .addObject("userViewModel", getUserViewModel())
                .addObject("property", getProperty(id))
                .addObject("propertyEditBindingModel", propertyEditBindingModel);
    }


    /**
     * Property data edit method
     * PostMapping
     */
    @PostMapping("/property/details/edit/{id}")
    public ModelAndView residentialEntityEditDetailsPost(@ModelAttribute("propertyEditBindingModel")
                                                         @Valid PropertyEditBindingModel propertyEditBindingModel,
                                                         @PathVariable("id") Long id, BindingResult bindingResult) {

        propertyService.editProperty(id, propertyEditBindingModel, !propertyService.needOfVerification(id, propertyEditBindingModel));

        return new ModelAndView("redirect:/property/details/" + id);
    }

    @PostMapping("/property/delete/{id}")
    public ModelAndView propertyDelete(@PathVariable("id") Long id) {

        propertyService.deleteProperty(id, false);

        return new ModelAndView("redirect:/property");
    }

    /**
     * This private method returns a Property by id
     *
     * @param id
     * @return Property
     */
    private Property getProperty(Long id) {
        return propertyService.findPropertyById(id);
    }

    private UserEntity getLoggedUser() {
        return userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
    }

    /**
     * This private method returns currently logged user
     *
     * @return UserEntity
     */
    private UserViewModel getUserViewModel() {
        return userService.getUserViewData(getLoggedUser());
    }
}
