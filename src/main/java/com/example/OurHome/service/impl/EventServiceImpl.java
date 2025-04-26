package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.Event;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.dto.BindingModels.Event.EventAddBindingModel;
import com.example.OurHome.model.dto.BindingModels.Event.EventEditBindingModel;
import com.example.OurHome.repo.EventRepository;
import com.example.OurHome.service.*;
import com.example.OurHome.service.email.EmailService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final ResidentialEntityService residentialEntityService;
    private final ModelMapper modelMapper;
    private final MessageService messageService;
    private final NotificationService notificationService;
    private final LogService logService;
    private final EmailService emailService;

    public EventServiceImpl(EventRepository eventRepository, ResidentialEntityService residentialEntityService, ModelMapper modelMapper, MessageService messageService, NotificationService notificationService, LogService logService, EmailService emailService) {
        this.eventRepository = eventRepository;
        this.residentialEntityService = residentialEntityService;
        this.modelMapper = modelMapper;
        this.messageService = messageService;
        this.notificationService = notificationService;
        this.logService = logService;
        this.emailService = emailService;
    }

    @Override
    public void createEvent(EventAddBindingModel eventAddBindingModel, Long residentialEntityId) {

        ResidentialEntity residentialEntity = residentialEntityService.findResidentialEntityById(residentialEntityId).orElse(null);
        Event event = modelMapper.map(eventAddBindingModel, Event.class);

        try {
            event.setResidentialEntity(residentialEntity);
            eventRepository.save(event);

            logService.info("✅[createEvent ->] New event successfully created for condominium id: {}", residentialEntity.getId());
        } catch (Exception e) {
            logService.error("❌[createEvent ->] Failed to create an event for condominium id: {}! {}", residentialEntity.getId(), e);
            throw new IllegalArgumentException("Грешка при създаване на събитие.");
        }

        if (eventAddBindingModel.getDate().isAfter(LocalDate.now())) {
            //send messages to all verified property owners
            messageService.newEventMessageToAllVerifiedPropertyOwners(event, residentialEntity);
            //send notification for new event to all verified owners
            notificationService.newEventNotificationToAllVerifiedPropertyOwners(event, residentialEntity);
            //send email for new event to all verified owners
            emailService.newEventEmailToAllVerifiedPropertyOwners(event, residentialEntity);
        }
    }

    @Override
    public void deleteEvent(Event event) {
        try {
            eventRepository.delete(event);

            logService.info("✅[deleteEvent] Event with id: {} successfully deleted. Condominium id: {}", event.getId(), event.getResidentialEntity().getId());
        } catch (Exception e) {
            logService.error("❌[deleteEvent] Failed to delete event id: {}. Condominium id: {}! {}", event.getId(), event.getResidentialEntity().getId(), e);
            throw new IllegalArgumentException("Грешка при изтриване на събитие.");
        }
    }

    @Override
    public void editEvent(Event event, EventEditBindingModel eventEditBindingModel) {

        try {
            event.setTitle(eventEditBindingModel.getTitle());
            event.setDate(eventEditBindingModel.getDate());
            event.setTimeFrom(eventEditBindingModel.getTimeFrom());
            event.setTimeTo(eventEditBindingModel.getTimeTo());

            eventRepository.save(event);

            logService.info("✅[editEvent] Event with id: {} successfully edited by manager. Condominium id: {}", event.getId(), event.getResidentialEntity().getId());
        } catch (Exception e) {
            logService.error("❌[editEvent] Failed to edit event id: {}. Condominium id: {}! {}", event.getId(), event.getResidentialEntity().getId(), e);
            throw new IllegalArgumentException("Грешка при редактиране на събитието.");
        }
    }

    @Override
    public EventEditBindingModel mapEventToBindingModel(Long id) {

        Event event = eventRepository.findById(id).orElse(null);
        if (event == null) {
            throw new IllegalArgumentException("Event with ID " + id + " not found");
        }
        return modelMapper.map(event, EventEditBindingModel.class);
    }
}
