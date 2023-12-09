package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.Expense;
import com.example.OurHome.model.dto.BindingModels.Expense.ExpenseEditBindingModel;
import com.example.OurHome.repo.ExpensesRepository;
import com.example.OurHome.service.ExpensesService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ExpensesServiceImplTestIT {

    @Autowired
    private ExpensesService expensesServiceToTest;

    @Autowired
    private ExpensesRepository expensesRepository;

    @BeforeEach
    void setUp() {
        expensesRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        expensesRepository.deleteAll();
    }

    @Test
    void testEditExpense() {
        Expense expense = new Expense();
        expense.setAmount(BigDecimal.valueOf(100));

        ExpenseEditBindingModel expenseEditBindingModel = new ExpenseEditBindingModel();
        expenseEditBindingModel.setExpenseAmount(BigDecimal.valueOf(200));
        expenseEditBindingModel.setExpenseDate(LocalDate.parse("2023-12-07"));
        expenseEditBindingModel.setDescription("test description");

        expensesServiceToTest.editExpense(expenseEditBindingModel, expense);

        assertEquals("test description", expense.getDescription());
        assertEquals(BigDecimal.valueOf(200), expense.getAmount());
    }

    @Test
    void testMapExpenseToBindingModel() {
        Expense expense = new Expense();
        expense.setAmount(BigDecimal.valueOf(100));
        expense.setExpenseDate(LocalDate.parse("2023-12-07"));
        expense.setDescription("test expense");

        ExpenseEditBindingModel expenseEditBindingModel = expensesServiceToTest.mapExpenseToBindingModel(expense);

        assertNotNull(expenseEditBindingModel);
        assertEquals(expense.getAmount(), expenseEditBindingModel.getExpenseAmount());
        assertEquals(expense.getExpenseDate(), expenseEditBindingModel.getExpenseDate());
        assertEquals(expense.getDescription(), expenseEditBindingModel.getDescription());
    }

    @Test
    void testDeleteExpense() {
        Expense expense = new Expense();
        expense.setAmount(BigDecimal.valueOf(100));
        expense.setExpenseDate(LocalDate.parse("2023-12-07"));
        expensesRepository.save(expense);
        Long id = expense.getId();

        expensesServiceToTest.deleteExpense(expense);
        Expense result = expensesRepository.findById(id).orElse(null);

        assertNull(result);
    }

    @Test
    public void testDeleteDocumentFromExpense(){
        Expense expense = new Expense();
        expense.setAmount(BigDecimal.valueOf(100));
        expense.setExpenseDate(LocalDate.parse("2023-12-07"));
        expense.setPicturePath("/test/of/path/removal");
        expensesRepository.save(expense);
        Long id = expense.getId();

        expensesServiceToTest.deleteDocumentFromExpense(expense);
        Expense result = expensesRepository.findById(id).orElse(null);

        assert result != null;
        assertNull(result.getPicturePath());
    }

    @Test
    public void testUpdateExpenseDocument(){
        Expense expense = new Expense();
        expense.setAmount(BigDecimal.valueOf(100));
        expense.setExpenseDate(LocalDate.parse("2023-12-07"));
        expense.setPicturePath("/test/of/path/removal");
        expensesRepository.save(expense);
        Long id = expense.getId();

        String newPath = "/new/path/set";

        expensesServiceToTest.updateExpenseDocument(expense, newPath);
        Optional<Expense> result = expensesRepository.findById(id);

        assertNotNull(result);
        assertEquals(newPath, result.get().getPicturePath());
    }

    @Test
    public void testUpdateExpenseDocumentWithNullExpense(){
        String newPath = "/new/path/set";
        assertThrows(IllegalArgumentException.class,
                () -> expensesServiceToTest.updateExpenseDocument(null, newPath));
    }
}
