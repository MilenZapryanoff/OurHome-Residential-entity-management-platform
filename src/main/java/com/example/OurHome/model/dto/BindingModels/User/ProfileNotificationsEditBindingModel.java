package com.example.OurHome.model.dto.BindingModels.User;

public class ProfileNotificationsEditBindingModel {

    boolean messageEmail;

    boolean newFeeEmail;

    boolean eventEmail;

    boolean reportEmail;

    public boolean isMessageEmail() {
        return messageEmail;
    }

    public void setMessageEmail(boolean messageEmail) {
        this.messageEmail = messageEmail;
    }

    public boolean isNewFeeEmail() {
        return newFeeEmail;
    }

    public void setNewFeeEmail(boolean newFeeEmail) {
        this.newFeeEmail = newFeeEmail;
    }

    public boolean isEventEmail() {
        return eventEmail;
    }

    public void setEventEmail(boolean eventEmail) {
        this.eventEmail = eventEmail;
    }

    public boolean isReportEmail() {
        return reportEmail;
    }

    public void setReportEmail(boolean reportEmail) {
        this.reportEmail = reportEmail;
    }
}
