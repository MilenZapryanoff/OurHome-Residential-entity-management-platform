package com.example.OurHome.model.dto.BindingModels.Expense;

import com.example.OurHome.model.Entity.Expense;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseFilterBindingModel {

    private BigDecimal totalExpensesAmount;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Select date")
    private LocalDate periodStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Select date")
    private LocalDate periodEnd;

    List<Expense> filteredList;

    public ExpenseFilterBindingModel() {
        this.filteredList = new ArrayList<>();
    }

    public BigDecimal getTotalExpensesAmount() {
        return totalExpensesAmount;
    }

    public void setTotalExpensesAmount(BigDecimal totalExpensesAmount) {
        this.totalExpensesAmount = totalExpensesAmount;
    }

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }

    public List<Expense> getExpenseList() {
        return filteredList;
    }

    public void setExpenseList(List<Expense> expenseList) {
        this.filteredList = expenseList;
    }
}
