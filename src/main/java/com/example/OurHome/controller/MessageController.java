package com.example.OurHome.controller;

import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.Message.SendMessageBindingModel;
import com.example.OurHome.model.dto.BindingModels.ResidentialEntity.ResidentManageBindingModel;
import com.example.OurHome.model.dto.ViewModels.UserViewModel;
import com.example.OurHome.service.MessageService;
import com.example.OurHome.service.ResidentialEntityService;
import com.example.OurHome.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MessageController {
    private final UserService userService;
    private final MessageService messageService;

    private final ResidentialEntityService residentialEntityService;

    public MessageController(UserService userService, MessageService messageService, ResidentialEntityService residentialEntityService) {
        this.userService = userService;
        this.messageService = messageService;
        this.residentialEntityService = residentialEntityService;
    }

    @GetMapping("/messages")
    public ModelAndView messages(@ModelAttribute("sendMessageBindingModel")
                                 SendMessageBindingModel sendMessageBindingModel) {

        return new ModelAndView("messages", "userViewModel", getUserViewModel())
                .addObject("sendMessageBindingModel", sendMessageBindingModel);
    }

    @PostMapping("/messages/send/{id}")
    @PreAuthorize("@securityService.checkMessageSender(#messageId, #sendMessageBindingModel.getSenderId() ,authentication)")
    public ModelAndView sendMessage(@ModelAttribute("sendMessageBindingModel")
                                    SendMessageBindingModel sendMessageBindingModel,
                                    @PathVariable("id") Long messageId) {

        ModelAndView modelAndView = new ModelAndView("messages")
                .addObject("userViewModel", getUserViewModel())
                .addObject("sendMessageBindingModel", sendMessageBindingModel);

        if (sendMessageBindingModel.getMessage().length() > 2000) {
            return modelAndView.addObject("messageError", true);
        }

        messageService.sendMessage(
                userService.findUserById(sendMessageBindingModel.getReceiverId()),
                userService.findUserById(sendMessageBindingModel.getSenderId()),
                sendMessageBindingModel.getMessage());
        messageService.readMessage(messageId);
        sendMessageBindingModel.setMessage("");

        return modelAndView.addObject("messageSent", true);
    }


    @GetMapping("/messages/archives")
    public ModelAndView messageArchives() {

        return new ModelAndView("messages-archives", "userViewModel", getUserViewModel());
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
                                    @PathVariable("id") Long id) {

        ModelAndView modelAndView = new ModelAndView("administration/administration-owners")
                .addObject("userViewModel", getUserViewModel())
                .addObject("residentialEntity", getResidentialEntity(residentManageBindingModel.getEntityId()));

        if (residentManageBindingModel.getMessage().length() > 2000) {
            return modelAndView.addObject("messageError", true);
        }

        messageService.sendMessage(userService.findUserById(id), userService.findUserById(getUserViewModel().getId()), residentManageBindingModel.getMessage());
        residentManageBindingModel.setMessage("");

        return modelAndView.addObject("messageSent", true);
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
