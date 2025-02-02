package com.example.OurHome.model.Entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private LocalDate date;
    private LocalTime timeFrom;
    private LocalTime timeTo;

    @ManyToOne
    private ResidentialEntity residentialEntity;

    public Event() {
    }

    public Event(String title, LocalDate date, LocalTime timeFrom, LocalTime timeTo, ResidentialEntity residentialEntity) {
        this.title = title;
        this.date = date;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.residentialEntity = residentialEntity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(LocalTime timeFrom) {
        this.timeFrom = timeFrom;
    }

    public LocalTime getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(LocalTime timeTo) {
        this.timeTo = timeTo;
    }

    public ResidentialEntity getResidentialEntity() {
        return residentialEntity;
    }

    public void setResidentialEntity(ResidentialEntity residentialEntity) {
        this.residentialEntity = residentialEntity;
    }
}