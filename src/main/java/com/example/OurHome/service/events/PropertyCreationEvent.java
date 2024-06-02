package com.example.OurHome.service.events;

import com.example.OurHome.model.Entity.Property;
import org.springframework.context.ApplicationEvent;

public class PropertyCreationEvent extends ApplicationEvent {

    private Property property;

    public PropertyCreationEvent(Object source, Property property) {
        super(source);
        this.property = property;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }
}
