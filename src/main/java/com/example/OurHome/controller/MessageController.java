package com.example.OurHome.controller;

import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.Message.SendMessageBindingModel;
import com.example.OurHome.model.dto.BindingModels.ResidentialEntity.ResidentManageBindingModel;
import com.example.OurHome.model.dto.ViewModels.UserViewModel;
import com.example.OurHome.service.MessageService;
import com.example.OurHome.service.ResidentialEntityService;
import com.example.OurHome.service.UserService;
import com.example.OurHome.service.email.EmailService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MessageController {
    private final UserService userService;
    private final MessageService messageService;

    private final ResidentialEntityService residentialEntityService;
    private final EmailService emailService;

    public MessageController(UserService userService, MessageService messageService, ResidentialEntityService residentialEntityService, EmailService emailService) {
        this.userService = userService;
        this.messageService = messageService;
        this.residentialEntityService = residentialEntityService;
        this.emailService = emailService;
    }

    @GetMapping("/messages")
    public ModelAndView messages(@ModelAttribute("sendMessageBindingModel")
                                 SendMessageBindingModel sendMessageBindingModel,
                                 @CookieValue(value = "lang",defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/messages") : new ModelAndView("en/messages");

        return view.addObject("sendMessageBindingModel", sendMessageBindingModel);
    }

    @PostMapping("/messages/send/{id}")
    @PreAuthorize("@securityService.checkMessageSender(#messageId, #sendMessageBindingModel.getSenderId() ,authentication)")
    public ModelAndView sendMessage(@ModelAttribute("sendMessageBindingModel")
                                    SendMessageBindingModel sendMessageBindingModel,
                                    @PathVariable("id") Long messageId,
                                    @CookieValue(value = "lang",defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/messages") : new ModelAndView("en/messages");

        view.addObject("sendMessageBindingModel", sendMessageBindingModel);

        if (sendMessageBindingModel.getMessage().length() > 2000) {
            return view.addObject("messageError", true);
        }

        messageService.sendMessage(
                userService.findUserById(sendMessageBindingModel.getReceiverId()),
                userService.findUserById(sendMessageBindingModel.getSenderId()),
                sendMessageBindingModel.getMessage());

        messageService.readMessage(messageId);
        sendMessageBindingModel.setMessage("");

        return view.addObject("messageSent", true);
    }


    @GetMapping("/messages/archives")
    public ModelAndView messageArchives(
            @CookieValue(value = "lang",defaultValue = "bg") String lang) {

        return resolveView(lang) ?
                new ModelAndView("bg/messages-archives") : new ModelAndView("en/messages-archives");
    }


    @PostMapping("/messages/delete/{id}")
    @PreAuthorize("@securityService.checkMessageUserAccess(#id, authentication)")
    public ModelAndView messageDelete(@PathVariable("id") Long id) {

        messageService.deleteMessage(id);
        return new ModelAndView("redirect:/messages/archives");
    }

    @PostMapping("/messages/delete/all/{id}")
    @PreAuthorize("@securityService.checkMassMessagesUserAccess(#id, authentication)")
    public ModelAndView messageDeleteAll(@PathVariable("id") Long id) {

        messageService.deleteAllMessages(id);
        return new ModelAndView("redirect:/messages/archives");
    }

    @PostMapping("/messages/read/{id}")
    @PreAuthorize("@securityService.checkMessageUserAccess(#id, authentication)")
    public ModelAndView messageRead(@PathVariable("id") Long id) {

        messageService.readMessage(id);
        return new ModelAndView("redirect:/messages");
    }

    @PostMapping("/messages/read/all/{id}")
    @PreAuthorize("@securityService.checkMassMessagesUserAccess(#id, authentication)")
    public ModelAndView messageReadAll(@PathVariable("id") Long id) {

        messageService.readAllMessages(id);
        return new ModelAndView("redirect:/messages");
    }

    @PostMapping("/messages/archive/{id}")
    @PreAuthorize("@securityService.checkMessageUserAccess(#id, authentication)")
    public ModelAndView messageArchive(@PathVariable("id") Long id) {

        messageService.archiveMessage(id);
        return new ModelAndView("redirect:/messages");
    }

    @PostMapping("/messages/archive/all/{id}")
    @PreAuthorize("@securityService.checkMassMessagesUserAccess(#id, authentication)")
    public ModelAndView messageArchiveAll(@PathVariable("id") Long id) {

        messageService.archiveAllMessages(id);
        return new ModelAndView("redirect:/messages");
    }

    //ADMINISTRATION/OWNERS SEND MESSAGE TO RESIDENT

    /**
     * Send message to resident
     *
     * @param id resident id
     * @return view administration-owners
     */
    @PostMapping("/administration/owners/message/{id}")
    @PreAuthorize("@securityService.checkResidentModeratorAccess(#id, authentication)")
    public ModelAndView sendMessage(@ModelAttribute("residentManageBindingModel")
                                    ResidentManageBindingModel residentManageBindingModel,
                                    @PathVariable("id") Long id,
                                    @CookieValue(value = "lang",defaultValue = "bg") String lang) {


        UserEntity recipient = userService.findUserById(id);
        UserEntity sender = userService.findUserById(getUserViewModel().getId());
        String message = residentManageBindingModel.getMessage();

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-owners-details") : new ModelAndView("en/administration/administration-owners-details");

        view.addObject("residentialEntity", getResidentialEntity(residentManageBindingModel.getEntityId()))
        .addObject("resident", recipient)    ;

        if (message.length() > 2000) {
            return view.addObject("messageError", true);
        }

        messageService.sendMessage(recipient, sender, message);

        residentManageBindingModel.setMessage("");

        return view.addObject("messageSent", true);
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
     * Method returns information about logged user
     *
     * @return userViewModel
     */
    private UserViewModel getUserViewModel() {
        UserEntity loggedUser = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return userService.getUserViewData(loggedUser);
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
