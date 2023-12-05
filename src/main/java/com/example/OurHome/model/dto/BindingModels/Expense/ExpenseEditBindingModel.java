package com.example.OurHome.model.dto.BindingModels.Expense;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseEditBindingModel {

    @NotNull
    @DecimalMin(value = "0.0", message = "Expense amount must be greater than or equal to zero")
    private BigDecimal expenseAmount;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "End period date cannot be null")
    private LocalDate expenseDate;
    @Size(max = 100, message = "Description must not be longer than 100 symbols")
    private String description;
    private String picturePath;
    public BigDecimal getExpenseAmount() {
        return expenseAmount;
    }
    public void setExpenseAmount(BigDecimal expenseAmount) {
        this.expenseAmount = expenseAmount;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }
}
