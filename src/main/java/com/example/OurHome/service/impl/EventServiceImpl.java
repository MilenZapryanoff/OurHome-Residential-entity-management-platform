package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.Event;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.dto.BindingModels.Event.EventAddBindingModel;
import com.example.OurHome.model.dto.BindingModels.Event.EventEditBindingModel;
import com.example.OurHome.repo.EventRepository;
import com.example.OurHome.service.EventService;
import com.example.OurHome.service.MessageService;
import com.example.OurHome.service.ResidentialEntityService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final ResidentialEntityService residentialEntityService;
    private final ModelMapper modelMapper;
    private final MessageService messageService;

    public EventServiceImpl(EventRepository eventRepository, ResidentialEntityService residentialEntityService, ModelMapper modelMapper, MessageService messageService) {
        this.eventRepository = eventRepository;
        this.residentialEntityService = residentialEntityService;
        this.modelMapper = modelMapper;
        this.messageService = messageService;
    }

    @Override
    public void createEvent(EventAddBindingModel eventAddBindingModel, Long residentialEntityId) {

        ResidentialEntity residentialEntity = residentialEntityService.findResidentialEntityById(residentialEntityId).orElse(null);

        Event event = modelMapper.map(eventAddBindingModel, Event.class);
        event.setResidentialEntity(residentialEntity);

        eventRepository.save(event);

        if (eventAddBindingModel.getDate().isAfter(LocalDate.now())) {
            messageService.newEventMessageToAllVerifiedPropertyOwners(event, residentialEntity);
        }
    }

    @Override
    public void deleteEvent(Event event) {
        eventRepository.delete(event);
    }

    @Override
    public void editEvent(Event event, EventEditBindingModel eventEditBindingModel) {

        event.setTitle(eventEditBindingModel.getTitle());
        event.setDate(eventEditBindingModel.getDate());
        event.setTimeFrom(eventEditBindingModel.getTimeFrom());
        event.setTimeTo(eventEditBindingModel.getTimeTo());

        eventRepository.save(event);
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
