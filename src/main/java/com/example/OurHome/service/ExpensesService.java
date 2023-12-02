package com.example.OurHome.service;

import com.example.OurHome.model.Entity.Expense;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.dto.BindingModels.Expense.ExpenseAddBindingModel;
import com.example.OurHome.model.Entity.dto.BindingModels.Expense.ExpenseEditBindingModel;
import com.example.OurHome.model.Entity.dto.BindingModels.Expense.ExpenseFilterBindingModel;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

public interface ExpensesService {

    Expense findById(Long id);

    void createExpense(ResidentialEntity residentialEntity, ExpenseAddBindingModel expenseAddBindingModel);

    ExpenseEditBindingModel mapExpenseToBindingModel(Expense expense);

    void editExpense(ExpenseEditBindingModel expenseEditBindingModel, Expense expense);

    void deleteExpense(Expense expense);

    ExpenseFilterBindingModel createDefaultExpenseFilter(ResidentialEntity residentialEntity);

    ExpenseFilterBindingModel createCustomExpenseFilter(LocalDate startPeriod, LocalDate endPeriod, ResidentialEntity residentialEntity);

    String saveDocument(MultipartFile file, Long id) throws IOException;

    void updateExpenseDocument(Expense expense, String relativePath);
    void deleteDocumentFromExpense(Expense expense);
}
