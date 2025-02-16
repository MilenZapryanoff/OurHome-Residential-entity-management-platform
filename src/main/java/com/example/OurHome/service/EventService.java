package com.example.OurHome.service;

import com.example.OurHome.model.Entity.Event;
import com.example.OurHome.model.dto.BindingModels.Event.EventAddBindingModel;
import com.example.OurHome.model.dto.BindingModels.Event.EventEditBindingModel;

public interface EventService {

    void createEvent(EventAddBindingModel eventAddBindingModel, Long residentialEntityId);

    void deleteEvent(Event event);

    void editEvent(Event event, EventEditBindingModel eventEditBindingModel);

    EventEditBindingModel mapEventToBindingModel(Long id);
}
