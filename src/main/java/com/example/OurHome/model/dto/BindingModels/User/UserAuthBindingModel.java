package com.example.OurHome.model.dto.BindingModels.User;

import org.hibernate.validator.constraints.Length;

public class UserAuthBindingModel {
    @Length(min = 8, max = 8, message = "Некорктен идентификатор на ЕС/Invalid Condominium ID")
    private String residentialId;

    @Length(min = 3, max = 20, message = "Кодът за достъп трябва да съдържа минимум 3 сомвола/Access code must be at least 3 symbols long")
    private String residentialAccessCode;

    public String getResidentialId() {
        return residentialId;
    }

    public void setResidentialId(String residentialId) {
        this.residentialId = residentialId;
    }

    public String getResidentialAccessCode() {
        return residentialAccessCode;
    }

    public void setResidentialAccessCode(String residentialAccessCode) {
        this.residentialAccessCode = residentialAccessCode;
    }

    public Long parseResidentialIdToLong() {
        String rowInput = this.getResidentialId();
        for (int i = 0; i < rowInput.length(); i++) {
            if (!Character.isDigit(rowInput.charAt(i)))
                return null;
        }
        return Long.valueOf(rowInput);
    }
}
