package com.example.OurHome.model.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.sql.Time;
import java.time.LocalDate;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDate date;

    @NotNull
    private Time time;

    @Column(columnDefinition = "TEXT")
    @NotNull
    private String text;

    @Column(columnDefinition = "TEXT")
    private String textEn;

    @ManyToOne
    private UserEntity receiver;

    @ManyToOne
    private UserEntity sender;
    private boolean isRead;
    private boolean isArchived;

    public Message() {
    }

    public Message(LocalDate date, Time time, String text, String textEn, UserEntity receiver, boolean isRead, boolean isArchived) {
        this.date = date;
        this.time = time;
        this.text = text;
        this.textEn = textEn;
        this.receiver = receiver;
        this.isRead = isRead;
        this.isArchived = isArchived;
    }

    public Message(LocalDate date, Time time, String text, UserEntity receiver, UserEntity sender, boolean isRead, boolean isArchived) {
        this.date = date;
        this.time = time;
        this.text = text;
        this.receiver = receiver;
        this.sender = sender;
        this.isRead = isRead;
        this.isArchived = isArchived;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public UserEntity getReceiver() {
        return receiver;
    }

    public void setReceiver(UserEntity user) {
        this.receiver = user;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public UserEntity getSender() {
        return sender;
    }

    public void setSender(UserEntity sender) {
        this.sender = sender;
    }

    public String getTextEn() {
        return textEn;
    }

    public void setTextEn(String textEn) {
        this.textEn = textEn;
    }
}
