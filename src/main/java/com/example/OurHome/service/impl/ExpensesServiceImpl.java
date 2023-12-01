package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.Expense;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.dto.BindingModels.Expense.ExpenseAddBindingModel;
import com.example.OurHome.model.Entity.dto.BindingModels.Expense.ExpenseEditBindingModel;
import com.example.OurHome.model.Entity.dto.BindingModels.Expense.ExpenseFilterBindingModel;
import com.example.OurHome.repo.ExpensesRepository;
import com.example.OurHome.service.ExpensesService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ExpensesServiceImpl implements ExpensesService {

    private final ExpensesRepository expensesRepository;
    private final ModelMapper modelMapper;

    public ExpensesServiceImpl(ExpensesRepository expensesRepository, ModelMapper modelMapper) {
        this.expensesRepository = expensesRepository;
        this.modelMapper = modelMapper;
    }

    public Expense findById(Long id) {
        return expensesRepository.findById(id).orElse(null);
    }

    @Override
    public void createExpense(ResidentialEntity residentialEntity, ExpenseAddBindingModel expenseAddBindingModel) {
        Expense expense = modelMapper.map(expenseAddBindingModel, Expense.class);
        expense.setResidentialEntity(residentialEntity);

        expensesRepository.save(expense);
    }

    @Override
    public ExpenseEditBindingModel mapExpenseToBindingModel(Expense expense) {
        return modelMapper.map(expense, ExpenseEditBindingModel.class);
    }

    @Override
    public void editExpense(ExpenseEditBindingModel expenseEditBindingModel, Expense expense) {
        modelMapper.map(expenseEditBindingModel, expense);
        expensesRepository.save(expense);
    }

    @Override
    public void deleteExpense(Expense expense) {
        expensesRepository.delete(expense);
    }

    @Override
    public ExpenseFilterBindingModel createDefaultExpenseFilter(ResidentialEntity residentialEntity) {

        ExpenseFilterBindingModel expenseFilterBindingModel = new ExpenseFilterBindingModel();

        if (residentialEntity != null) {

            LocalDate startPeriod = LocalDate.now().withDayOfMonth(1);
            LocalDate endPeriod = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
            BigDecimal expensesTotalSum = BigDecimal.valueOf(0.0);

            List<Expense> filteredExpenses = expensesRepository.findExpensesByDatesAndResidentialEntityId(startPeriod, endPeriod, residentialEntity.getId());
            for (Expense expense : filteredExpenses) {
                expensesTotalSum = expensesTotalSum.add(expense.getAmount());
            }

            expenseFilterBindingModel.setPeriodStart(startPeriod);
            expenseFilterBindingModel.setPeriodEnd(endPeriod);
            expenseFilterBindingModel.setTotalExpensesAmount(expensesTotalSum);

            return expenseFilterBindingModel;
        }
        return expenseFilterBindingModel;
    }

    @Override
    public ExpenseFilterBindingModel createCustomExpenseFilter(LocalDate startPeriod, LocalDate endPeriod, ResidentialEntity residentialEntity) {

        ExpenseFilterBindingModel expenseFilterBindingModel = new ExpenseFilterBindingModel();

        if (residentialEntity != null) {
            BigDecimal expensesTotalSum = BigDecimal.valueOf(0.0);

            List<Expense> filteredExpenses = expensesRepository.findExpensesByDatesAndResidentialEntityId(startPeriod, endPeriod, residentialEntity.getId());
            for (Expense expense : filteredExpenses) {
                expensesTotalSum = expensesTotalSum.add(expense.getAmount());
            }

            expenseFilterBindingModel.setPeriodStart(startPeriod);
            expenseFilterBindingModel.setPeriodEnd(endPeriod);
            expenseFilterBindingModel.setTotalExpensesAmount(expensesTotalSum);

            return expenseFilterBindingModel;
        }
        return expenseFilterBindingModel;
    }
}
