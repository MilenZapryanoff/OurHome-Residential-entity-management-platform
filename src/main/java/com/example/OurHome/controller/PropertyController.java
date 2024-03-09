package com.example.OurHome.controller;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.Expense.ExpenseFilterBindingModel;
import com.example.OurHome.model.dto.BindingModels.Message.SendMessageBindingModel;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyEditBindingModel;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyRegisterBindingModel;
import com.example.OurHome.model.dto.BindingModels.ResidentialEntity.ResidentManageBindingModel;
import com.example.OurHome.model.dto.BindingModels.User.UserAuthBindingModel;
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
public class PropertyController {

    private final UserService userService;
    private final PropertyService propertyService;
    private final MessageService messageService;
    private final ExpensesService expensesService;
    private final ResidentialEntityService residentialEntityService;


    public PropertyController(UserService userService, PropertyService propertyService, MessageService messageService, ExpensesService expensesService, ResidentialEntityService residentialEntityService) {
        this.userService = userService;
        this.propertyService = propertyService;
        this.messageService = messageService;
        this.expensesService = expensesService;
        this.residentialEntityService = residentialEntityService;
    }

    /**
     * PROPERTY section
     */
    @GetMapping("/property")
    public ModelAndView property() {

        return new ModelAndView("property", "userViewModel", getUserViewModel());
    }

    /**
     * PROPERTY -> Add property
     */
    @GetMapping("/property/add")
    public ModelAndView addProperty(@ModelAttribute("propertyRegisterBindingModel") PropertyRegisterBindingModel propertyRegisterBindingModel) {

        return new ModelAndView("property-add", "userViewModel", getUserViewModel());
    }

    /**
     * PROPERTY -> Add property
     * POST
     */
    @PostMapping("/property/add")
    public ModelAndView addProperty(@ModelAttribute("propertyRegisterBindingModel")
                                    @Valid PropertyRegisterBindingModel propertyRegisterBindingModel, BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            return new ModelAndView("property-add", "userViewModel", getUserViewModel());
        }

        if (propertyService.newProperty(propertyRegisterBindingModel, getLoggedUser())) {
            return new ModelAndView("redirect:/property");
        } else {
            return new ModelAndView("property-add", "userViewModel", getUserViewModel())
                    .addObject("registrationFailed", true);
        }
    }

    /**
     * PROPERTY -> Add property -> Add new RE
     */
    @GetMapping("/property/add/new")
    public ModelAndView addPropertyInNewEntity(@ModelAttribute("userAuthRegisterBindingModel") UserAuthBindingModel userAuthBindingModel) {

        return new ModelAndView("property-add-new-entity", "userViewModel", getUserViewModel());
    }

    /**
     * PROPERTY -> Add property
     * POST
     */
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
     * PROPERTY -> SUMMARY Section
     */
    @GetMapping("/property/summary/{id}")
    @PreAuthorize("@securityService.checkPropertyOwnerAccess(#id, authentication)")
    public ModelAndView residentialEntityDetails(
            @ModelAttribute("residentManageBindingModel") ResidentManageBindingModel residentManageBindingModel,
            @ModelAttribute("sendMessageBindingModel") SendMessageBindingModel sendMessageBindingModel,
            @PathVariable("id") Long id) {

        return new ModelAndView("property-summary", "userViewModel", getUserViewModel())
                .addObject("property", getProperty(id));
    }

    /**
     * PROPERTY -> SUMMARY Section
     * POST
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
     * PROPERTY -> DETAILS Section
     */
    @GetMapping("/property/details/{id}")
    @PreAuthorize("@securityService.checkPropertyOwnerAccess(#id, authentication)")
    public ModelAndView propertyDetails(@ModelAttribute("propertyManageBindingModel") ResidentManageBindingModel residentManageBindingModel, @PathVariable("id") Long id) {

        return new ModelAndView("property-details", "userViewModel", getUserViewModel())
                .addObject("property", getProperty(id));
    }

    /**
     * PROPERTY -> DETAILS -> Edit Section
     */
    @GetMapping("/property/details/edit/{id}")
    @PreAuthorize("@securityService.checkPropertyOwnerAccess(#id, authentication)")
    public ModelAndView residentialEntityEditDetails(@PathVariable("id") Long id) {

        PropertyEditBindingModel propertyEditBindingModel = propertyService.mapPropertyToEditBindingModel(getProperty(id));

        return new ModelAndView("property-details-edit")
                .addObject("userViewModel", getUserViewModel())
                .addObject("property", getProperty(id))
                .addObject("propertyEditBindingModel", propertyEditBindingModel);
    }


    /**
     * PROPERTY -> DETAILS -> Edit Section
     * POST
     */
    @PostMapping("/property/details/edit/{id}")
    @PreAuthorize("@securityService.checkPropertyOwnerAccess(#id, authentication)")
    public ModelAndView residentialEntityEditDetailsPost(@ModelAttribute("propertyEditBindingModel")
                                                         @Valid PropertyEditBindingModel propertyEditBindingModel,
                                                         @PathVariable("id") Long id, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("property-details-edit")
                    .addObject("userViewModel", getUserViewModel())
                    .addObject("property", getProperty(id))
                    .addObject("propertyEditBindingModel", propertyEditBindingModel);
        }

        if (propertyService.editProperty(id, propertyEditBindingModel, !propertyService.needOfVerification(id, propertyEditBindingModel))) {
            return new ModelAndView("redirect:/property/details/" + id);
        } else {
            return new ModelAndView("property-details-edit")
                    .addObject("userViewModel", getUserViewModel())
                    .addObject("property", getProperty(id))
                    .addObject("propertyEditBindingModel", propertyEditBindingModel)
                    .addObject("editFailed", true);
        }
    }

    /**
     * PROPERTY -> Delete property
     * POST
     */
    @PostMapping("/property/delete/{id}")
    @PreAuthorize("@securityService.checkPropertyOwnerAccess(#id, authentication)")
    public ModelAndView propertyDelete(@PathVariable("id") Long id) {

        propertyService.deleteProperty(id, false);

        return new ModelAndView("redirect:/property");
    }

    /**
     * PROPERTY -> MONTHLY FEES Section
     */
    @GetMapping("/property/monthlyfees/{id}")
    @PreAuthorize("@securityService.checkPropertyOwnerAccess(#id, authentication)")
    public ModelAndView propertyFeesDetails(@PathVariable("id") Long id) {

        return new ModelAndView("property-monthlyfees", "userViewModel", getUserViewModel())
                .addObject("property", getProperty(id));
    }


    /**
     * PROPERTY -> EXPENSES RE Section
     */
    @GetMapping("/property/expenses/{id}")
    @PreAuthorize("@securityService.checkPropertyOwnerAccess(#id, authentication)")
    public ModelAndView residentialEntityExpenses(
            @ModelAttribute("residentManageBindingModel") ResidentManageBindingModel residentManageBindingModel,
            @ModelAttribute("sendMessageBindingModel") SendMessageBindingModel sendMessageBindingModel,
            @PathVariable("id") Long id) {


        Property property = getProperty(id);
        ResidentialEntity residentialEntity = getResidentialEntity(property.getResidentialEntity().getId());

        ExpenseFilterBindingModel expenseFilter = expensesService
                .createDefaultExpenseFilter(residentialEntity);


        return new ModelAndView("property-expenses", "userViewModel", getUserViewModel())
                .addObject("userViewModel", getUserViewModel())
                .addObject("property", property)
                .addObject("expenseFilterBindingModel", expenseFilter);
    }

    @PostMapping("/property/expenses/{id}")
    @PreAuthorize("@securityService.checkPropertyOwnerAccess(#id, authentication)")
    public ModelAndView residentialEntityFilterExpenses(@PathVariable("id") Long id,
                                                        @Valid ExpenseFilterBindingModel expenseFilter,
                                                        BindingResult bindingResult) {

        Property property = getProperty(id);
        ModelAndView modelAndView = new ModelAndView("property-expenses")
                .addObject("userViewModel", getUserViewModel())
                .addObject("property", property);

        if (bindingResult.hasErrors()) {
            return modelAndView.addObject("expenseFilterBindingModel", expenseFilter);
        }

        ResidentialEntity residentialEntity = getResidentialEntity(property.getResidentialEntity().getId());

        expenseFilter = expensesService.createCustomExpenseFilter(expenseFilter.getPeriodStart(),
                expenseFilter.getPeriodEnd(), residentialEntity);

        return modelAndView.addObject("expenseFilterBindingModel", expenseFilter);
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
