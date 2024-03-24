package com.example.OurHome.service;

import com.example.OurHome.model.Entity.Expense;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.dto.BindingModels.Financial.ExpenseAddBindingModel;
import com.example.OurHome.model.dto.BindingModels.Financial.ExpenseEditBindingModel;
import com.example.OurHome.model.dto.BindingModels.Financial.ExpenseFilterBindingModel;
import com.example.OurHome.model.dto.BindingModels.Financial.IncomesBindingModel;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

public interface FinancialService {

    Expense findById(Long id);

    void createExpense(ResidentialEntity residentialEntity, ExpenseAddBindingModel expenseAddBindingModel);

    void editExpense(ExpenseEditBindingModel expenseEditBindingModel, Expense expense);

    void deleteExpense(Expense expense);

    void updateExpenseDocument(Expense expense, String relativePath);

    void deleteDocumentFromExpense(Expense expense);

    ExpenseEditBindingModel mapExpenseToBindingModel(Expense expense);

    IncomesBindingModel mapIncomesBindingModel(Long id);

    ExpenseFilterBindingModel createDefaultExpenseFilter(ResidentialEntity residentialEntity);

    ExpenseFilterBindingModel createCustomExpenseFilter(LocalDate startPeriod, LocalDate endPeriod, ResidentialEntity residentialEntity);

    String saveDocument(MultipartFile file, Long id) throws IOException;


}
