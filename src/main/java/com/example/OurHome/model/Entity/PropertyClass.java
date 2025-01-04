package com.example.OurHome.model.Entity;

import com.example.OurHome.model.enums.PropertyClassName;
import jakarta.persistence.*;

@Entity(name = "property_classes")
public class PropertyClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PropertyClassName name;

    private String description;

    public PropertyClass() {
    }

    public PropertyClass(PropertyClassName name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PropertyClassName getName() {
        return name;
    }

    public void setName(PropertyClassName propertyClassName) {
        this.name = propertyClassName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
