package com.example.OurHome.controller;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.PropertyType;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.Financial.ExpenseFilterBindingModel;
import com.example.OurHome.model.dto.BindingModels.Message.SendMessageBindingModel;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyEditBindingModel;
import com.example.OurHome.model.dto.BindingModels.Property.PropertyRegisterBindingModel;
import com.example.OurHome.model.dto.BindingModels.ResidentialEntity.ResidentManageBindingModel;
import com.example.OurHome.model.dto.BindingModels.User.UserAuthBindingModel;
import com.example.OurHome.model.dto.ViewModels.UserViewModel;
import com.example.OurHome.repo.PropertyRepository;
import com.example.OurHome.service.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PropertyController {

    private final UserService userService;
    private final PropertyService propertyService;
    private final MessageService messageService;
    private final FinancialService financialService;
    private final ResidentialEntityService residentialEntityService;
    private final PropertyTypeService propertyTypeService;
    private final PropertyChangeRequestService propertyChangeRequestService;
    private final PropertyRepository propertyRepository;

    public PropertyController(UserService userService, PropertyService propertyService, MessageService messageService, FinancialService financialService, ResidentialEntityService residentialEntityService, PropertyTypeService propertyTypeService, PropertyChangeRequestService propertyChangeRequestService, PropertyRepository propertyRepository) {
        this.userService = userService;
        this.propertyService = propertyService;
        this.messageService = messageService;
        this.financialService = financialService;
        this.residentialEntityService = residentialEntityService;
        this.propertyTypeService = propertyTypeService;
        this.propertyChangeRequestService = propertyChangeRequestService;
        this.propertyRepository = propertyRepository;
    }

    /**
     * PROPERTY section
     */
    @GetMapping("/property")
    public ModelAndView property(@CookieValue(value = "lang", defaultValue = "bg") String lang) {

        return resolveView(lang) ?
                new ModelAndView("bg/property/property") : new ModelAndView("en/property/property");
    }

    /**
     * PROPERTY -> Add property
     */
    @GetMapping("/property/add")
    public ModelAndView addProperty(@ModelAttribute("propertyRegisterBindingModel") PropertyRegisterBindingModel propertyRegisterBindingModel, @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        UserViewModel loggedUser = getUserViewModel();
        UserAuthBindingModel userAuthBindingModel = new UserAuthBindingModel();

        if (loggedUser.getResidentialEntities().isEmpty()) {

            ModelAndView view = resolveView(lang) ?
                    new ModelAndView("bg/property/property-add-new-entity") : new ModelAndView("en/property/property-add-new-entity");

            return view.addObject("userAuthRegisterBindingModel", userAuthBindingModel)
                    .addObject("notJoinedToResidentialEntity", true);
        }

        return resolveView(lang) ?
                new ModelAndView("bg/property/property-add") : new ModelAndView("en/property/property-add");
    }

    /**
     * PROPERTY -> Select property type after property creation
     * POST
     */
    @PostMapping("/property/add")
    public ModelAndView addProperty(@ModelAttribute("propertyRegisterBindingModel") @Valid PropertyRegisterBindingModel propertyRegisterBindingModel, BindingResult bindingResult, @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/property/property-add") : new ModelAndView("en/property/property-add");

        if (bindingResult.hasErrors()) {
            return view;
        }

        if (propertyService.requestToObtainProperty(propertyRegisterBindingModel, getLoggedUser())) {
            return new ModelAndView("redirect:/property");
        } else {
            return view.addObject("registrationFailed", true);
        }
    }

    /**
     * PROPERTY -> Add property -> Add new RE
     */
    @GetMapping("/property/add/new")
    public ModelAndView addPropertyInNewEntity(@ModelAttribute("userAuthRegisterBindingModel") UserAuthBindingModel userAuthBindingModel, @CookieValue(value = "lang", defaultValue = "bg") String lang) {


        return resolveView(lang) ?
                new ModelAndView("bg/property/property-add-new-entity") : new ModelAndView("en/property/property-add-new-entity");
    }

    /**
     * PROPERTY -> Add property
     * POST
     */
    @PostMapping("/property/add/new")
    public ModelAndView addPropertyInNewEntity(@ModelAttribute("userAuthRegisterBindingModel")
                                               @Valid UserAuthBindingModel userAuthBindingModel,
                                               BindingResult bindingResult,
                                               @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        Long residentialEntityId = userAuthBindingModel.parseResidentialIdToLong();
        String validationCode = userAuthBindingModel.getResidentialAccessCode();

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/property/property-add-new-entity") : new ModelAndView("en/property/property-add-new-entity");

        if (bindingResult.hasErrors()) {
            return view;

        } else if (!userService.residentialValidation(residentialEntityId, validationCode)) {
            return view.addObject("badResidentialEntity", true);

        }
        userService.joinUserToNewResidentialEntity(userAuthBindingModel, getLoggedUser());

        return new ModelAndView("redirect:/property/add");
    }

    /**
     * PROPERTY -> SUMMARY Section
     */
    @GetMapping("/property/summary/{id}")
    @PreAuthorize("@securityService.checkPropertyOwnerAccess(#id, authentication)")
    public ModelAndView propertySummary(
            @ModelAttribute("residentManageBindingModel") ResidentManageBindingModel residentManageBindingModel,
            @ModelAttribute("sendMessageBindingModel") SendMessageBindingModel sendMessageBindingModel,
            @PathVariable("id") Long id,
            @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/property/property-summary") : new ModelAndView("en/property/property-summary");

        return view.addObject("property", getProperty(id));
    }

    /**
     * PROPERTY -> SUMMARY Section
     * POST
     */
    @PostMapping("/property/summary/messageToManager/{id}")
    @PreAuthorize("@securityService.checkMessageSenderAndReceiver(#propertyId, #sendMessageBindingModel.getSenderId() ,#sendMessageBindingModel.getReceiverId())")
    public ModelAndView sendMessageToPropertyManager(@ModelAttribute("sendMessageBindingModel") SendMessageBindingModel sendMessageBindingModel, @PathVariable("id") Long propertyId, @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/property/property-summary") : new ModelAndView("en/property/property-summary");


        view.addObject("property", getProperty(propertyId))
                .addObject("sendMessageBindingModel", sendMessageBindingModel);

        if (sendMessageBindingModel.getMessage().length() > 2000) {
            return view.addObject("messageError", true);
        }

        messageService.sendMessage(userService.findUserById(sendMessageBindingModel.getReceiverId()), userService.findUserById(sendMessageBindingModel.getSenderId()), sendMessageBindingModel.getMessage());

        return view.addObject("messageSent", true);
    }

    /**
     * PROPERTY -> DETAILS Section
     */
    @GetMapping("/property/details/{id}")
    @PreAuthorize("@securityService.checkPropertyOwnerAccess(#id, authentication)")
    public ModelAndView propertyDetails(@ModelAttribute("propertyManageBindingModel") ResidentManageBindingModel residentManageBindingModel, @PathVariable("id") Long id, @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/property/property-details") : new ModelAndView("en/property/property-details");

        return view.addObject("property", getProperty(id));
    }

    /**
     * PROPERTY -> DETAILS -> Edit Section
     */
    @GetMapping("/property/details/edit/{id}")
    @PreAuthorize("@securityService.checkPropertyOwnerAccess(#id, authentication)")
    public ModelAndView propertyEdit(@PathVariable("id") Long id, @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        Property property = getProperty(id);
        PropertyEditBindingModel propertyEditBindingModel;

        if (property.isRejected() && property.getPropertyRegisterRequest() != null) {
            //getting input fields from the active registration request. This way we hide the real property data
            propertyEditBindingModel = propertyService.mapRegistrationRequestToEditBindingModel(property);
        } else if (property.getPropertyChangeRequest() != null) {
            //getting input fields from the active change request. This way we hide the real property data
            propertyEditBindingModel = propertyService.mapChangeRequestToEditBindingModel(property);
        } else {
            //getting input fields data directly from the property
            propertyEditBindingModel = propertyService.mapPropertyToEditBindingModel(property);
        }

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/property/property-details-edit") : new ModelAndView("en/property/property-details-edit");

        return view
                .addObject("property", getProperty(id))
                .addObject("propertyEditBindingModel", propertyEditBindingModel);
    }

    /**
     * PROPERTY -> DETAILS -> Edit Section
     * POST
     */
    @PostMapping("/property/details/edit/{id}")
    @PreAuthorize("@securityService.checkPropertyOwnerAccess(#id, authentication)")
    public ModelAndView propertyEdit(@PathVariable("id") Long id, @Valid PropertyEditBindingModel propertyEditBindingModel, BindingResult bindingResult, @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/property/property-details-edit") : new ModelAndView("en/property/property-details-edit");

        if (bindingResult.hasErrors()) {
            return view.addObject("property", getProperty(id))
                    .addObject("propertyEditBindingModel", propertyEditBindingModel);
        }

        PropertyType propertyType = null;
        if (propertyEditBindingModel.getPropertyType() != null) {
            propertyType = propertyTypeService.findById(propertyEditBindingModel.getPropertyType());
        }

        Property property = getProperty(id);
        //setting property number to original value. This will not allow change request to contain different property number.
        //Front-end (edit page) input field is also readonly!
        propertyEditBindingModel.setNumber(property.getNumber());

        boolean validationRequired = propertyService.validationIsRequired(id, propertyEditBindingModel);

        //updating of non-fee component data. This happens anyway.
        propertyService.updateNonFeeComponentData(property, propertyEditBindingModel, propertyType);

        //if there are no fee components data change and property ownership is FINISHED!
        if (!validationRequired && property.isObtained()) {
            //cancel changeRequest (by forwarding to cancel method in controller) if such exists as there is no change of fee component data. In this case changeRequest is redundant!
            if (property.getPropertyChangeRequest() != null) {
                return new ModelAndView("forward:/property/details/cancel-change-request/" + id);
            }
            return new ModelAndView("redirect:/property/details/" + id);
        }

        //processing of change request if validation is REQUIRED!
        if (propertyService.processChangeRequest(id, propertyEditBindingModel, propertyType, getLoggedUser())) {
            return new ModelAndView("redirect:/property/details/" + id);
        } else {
            return view
                    .addObject("property", getProperty(id))
                    .addObject("propertyEditBindingModel", propertyEditBindingModel)
                    .addObject("editFailed", true);
        }
    }


    /**
     * PROPERTY -> DETAILS -> Cancel request
     * POST
     */
    @PostMapping("/property/details/cancel-change-request/{id}")
    @PreAuthorize("@securityService.checkPropertyOwnerAccess(#id, authentication)")
    public ModelAndView propertyCancelChangeRequest(@PathVariable("id") Long id) {

        Property property = getProperty(id);
        propertyChangeRequestService.invalidateRequest(property.getPropertyChangeRequest());

        property.setPropertyChangeRequest(null);
        propertyRepository.save(property);

        return new ModelAndView("redirect:/property/details/" + id);
    }


    /**
     * PROPERTY -> Delete property owner
     * POST
     */
    @PostMapping("/property/delete/{id}")
    @PreAuthorize("@securityService.checkPropertyOwnerAccess(#id, authentication)")
    public ModelAndView propertyDelete(@PathVariable("id") Long id) {

        propertyService.unlinkOwner(id, false);

        return new ModelAndView("redirect:/property");
    }

    /**
     * PROPERTY -> MONTHLY FEES Section
     */
    @GetMapping("/property/monthlyfees/{id}")
    @PreAuthorize("@securityService.checkPropertyOwnerFullAccess(#id, authentication)")
    public ModelAndView propertyFeesDetails(@PathVariable("id") Long id, @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/property/property-monthlyfees") : new ModelAndView("en/property/property-monthlyfees");

        return view.addObject("property", getProperty(id));
    }


    /**
     * PROPERTY -> RE -> Data Section
     */
    @GetMapping("/property/re/data/{id}")
    @PreAuthorize("@securityService.checkPropertyOwnerFullAccess(#id, authentication)")
    public ModelAndView residentialEntityData(@ModelAttribute("residentManageBindingModel") ResidentManageBindingModel residentManageBindingModel, @ModelAttribute("sendMessageBindingModel") SendMessageBindingModel sendMessageBindingModel, @PathVariable("id") Long id, @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/property/property-re-data") : new ModelAndView("en/property/property-re-data");


        Property property = getProperty(id);
        ResidentialEntity residentialEntity = getResidentialEntity(property.getResidentialEntity().getId());

        ExpenseFilterBindingModel expenseFilter = financialService.createDefaultExpenseFilter(residentialEntity);

        return view
                .addObject("property", property)
                .addObject("expenseFilterBindingModel", expenseFilter);
    }

    /**
     * PROPERTY -> RE -> EXPENSES Section
     */
    @GetMapping("/property/re/expenses/{id}")
    @PreAuthorize("@securityService.checkPropertyOwnerFullAccess(#id, authentication)")
    public ModelAndView residentialEntityExpenses(@ModelAttribute("residentManageBindingModel") ResidentManageBindingModel residentManageBindingModel, @ModelAttribute("sendMessageBindingModel") SendMessageBindingModel sendMessageBindingModel, @PathVariable("id") Long id, @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/property/property-re-expenses") : new ModelAndView("en/property/property-re-expenses");

        Property property = getProperty(id);
        ResidentialEntity residentialEntity = getResidentialEntity(property.getResidentialEntity().getId());

        ExpenseFilterBindingModel expenseFilter = financialService.createDefaultExpenseFilter(residentialEntity);

        return view
                .addObject("property", property)
                .addObject("expenseFilterBindingModel", expenseFilter);
    }

    @PostMapping("/property/re/expenses/{id}")
    @PreAuthorize("@securityService.checkPropertyOwnerFullAccess(#id, authentication)")
    public ModelAndView residentialEntityFilterExpenses(@PathVariable("id") Long id, @Valid ExpenseFilterBindingModel expenseFilter, BindingResult bindingResult, @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        Property property = getProperty(id);

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/property/property-re-expenses") : new ModelAndView("en/property/property-re-expenses");

        view.addObject("property", property);

        if (bindingResult.hasErrors()) {
            return view.addObject("expenseFilterBindingModel", expenseFilter);
        }

        ResidentialEntity residentialEntity = getResidentialEntity(property.getResidentialEntity().getId());

        expenseFilter = financialService.createCustomExpenseFilter(expenseFilter.getPeriodStart(), expenseFilter.getPeriodEnd(), residentialEntity);

        return view.addObject("expenseFilterBindingModel", expenseFilter);
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
