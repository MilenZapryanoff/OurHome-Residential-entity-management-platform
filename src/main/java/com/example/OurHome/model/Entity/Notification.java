package com.example.OurHome.model.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDateTime creationDateTime;

    @Column
    private Long eventId;

    @Column
    private boolean cleared;

    @Column
    private String category;

    @Column
    private String titleBG;

    @Column
    private String descriptionBG;

    @Column
    private String titleEN;

    @Column
    private String descriptionEN;

    @NotNull
    @ManyToOne
    private UserEntity user;

    public Notification() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public boolean isCleared() {
        return cleared;
    }

    public void setCleared(boolean cleared) {
        this.cleared = cleared;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitleBG() {
        return titleBG;
    }

    public void setTitleBG(String titleBG) {
        this.titleBG = titleBG;
    }

    public String getDescriptionBG() {
        return descriptionBG;
    }

    public void setDescriptionBG(String descriptionBG) {
        this.descriptionBG = descriptionBG;
    }

    public String getTitleEN() {
        return titleEN;
    }

    public void setTitleEN(String titleEN) {
        this.titleEN = titleEN;
    }

    public String getDescriptionEN() {
        return descriptionEN;
    }

    public void setDescriptionEN(String descriptionEN) {
        this.descriptionEN = descriptionEN;
    }

    public @NotNull UserEntity getUser() {
        return user;
    }

    public void setUser(@NotNull UserEntity user) {
        this.user = user;
    }
}
