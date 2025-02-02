package com.example.OurHome.service.impl;

import com.example.OurHome.repo.EventRepository;
import com.example.OurHome.service.EventService;
import org.springframework.stereotype.Service;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

}
