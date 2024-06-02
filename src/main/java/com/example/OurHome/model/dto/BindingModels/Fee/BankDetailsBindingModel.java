package com.example.OurHome.model.dto.BindingModels.Fee;

import jakarta.validation.constraints.Size;

public class BankDetailsBindingModel {

    @Size(min = 22, max = 22, message = "Invalid IBAN format. Must be exactly 22 characters.")
    private String bankIban;
    private String bankAccountHolder;
    private String bankName;
    private String bankAdditionalData;

    public String getBankIban() {
        return bankIban;
    }

    public void setBankIban(String bankIban) {
        this.bankIban = bankIban;
    }

    public String getBankAccountHolder() {
        return bankAccountHolder;
    }

    public void setBankAccountHolder(String bankAccountHolder) {
        this.bankAccountHolder = bankAccountHolder;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAdditionalData() {
        return bankAdditionalData;
    }

    public void setBankAdditionalData(String bankAdditionalData) {
        this.bankAdditionalData = bankAdditionalData;
    }
}
