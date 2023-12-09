package com.example.OurHome.service.impl;

import com.example.OurHome.model.Entity.Expense;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.Role;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.Expense.ExpenseAddBindingModel;
import com.example.OurHome.model.dto.BindingModels.Expense.ExpenseEditBindingModel;
import com.example.OurHome.model.dto.BindingModels.Expense.ExpenseFilterBindingModel;
import com.example.OurHome.model.enums.CityName;
import com.example.OurHome.repo.*;
import com.example.OurHome.service.ExpensesService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ExpensesServiceImplTestIT {

    @Autowired
    private ExpensesService expensesServiceToTest;

    @Autowired
    private ExpensesRepository expensesRepository;

    @Autowired
    private ResidentialEntityRepository residentialEntityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CityRepository cityRepository;

    @BeforeEach
    void setUp() {
        residentialEntityRepository.deleteAll();
        userRepository.deleteAll();
        expensesRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        residentialEntityRepository.deleteAll();
        userRepository.deleteAll();
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
    public void testDeleteDocumentFromExpense() {
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
    public void testUpdateExpenseDocument() {
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
    public void testUpdateExpenseDocumentWithNullExpense() {
        String newPath = "/new/path/set";
        assertThrows(IllegalArgumentException.class,
                () -> expensesServiceToTest.updateExpenseDocument(null, newPath));
    }

    @Test
    public void testGetExpenseFilterBindingModel() {
        ResidentialEntity residentialEntity = createTestData();

        ExpenseFilterBindingModel defaultExpenseFilter = expensesServiceToTest.createDefaultExpenseFilter(residentialEntity);

        LocalDate startPeriod = LocalDate.now().withDayOfMonth(1);
        LocalDate endPeriod = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        assertEquals(startPeriod,(defaultExpenseFilter.getPeriodStart()));
        assertEquals(endPeriod,(defaultExpenseFilter.getPeriodEnd()));
    }

    @Test
    public void testCreateCustomExpenseFilter() {
        ResidentialEntity residentialEntity = createTestData();

        LocalDate startPeriod = LocalDate.parse("2023-08-01");
        LocalDate endPeriod = LocalDate.parse("2023-08-15");

        ExpenseFilterBindingModel customExpenseFilter = expensesServiceToTest.createCustomExpenseFilter(startPeriod, endPeriod, residentialEntity);

        assertEquals(startPeriod,(customExpenseFilter.getPeriodStart()));
        assertEquals(endPeriod,(customExpenseFilter.getPeriodEnd()));
    }

    @Test
    public void testCreateCustomExpenseFilterWithNullResidentialEntity() {

        LocalDate startPeriod = LocalDate.parse("2023-08-01");
        LocalDate endPeriod = LocalDate.parse("2023-08-15");

        ExpenseFilterBindingModel returnedExpenseFilter = expensesServiceToTest.createCustomExpenseFilter(startPeriod, endPeriod, null);

        assertNull(returnedExpenseFilter.getPeriodStart());
        assertNull(returnedExpenseFilter.getPeriodEnd());
    }

    @Test
    public void testCreateExpense() {
        UserEntity manager = createTestManager();

        ResidentialEntity residentialEntity = createTestData();
        residentialEntity.setManager(manager);

        residentialEntityRepository.save(residentialEntity);

        ExpenseAddBindingModel expenseAddBindingModel = new ExpenseAddBindingModel();
        expenseAddBindingModel.setExpenseDate(LocalDate.now());
        expenseAddBindingModel.setExpenseAmount(BigDecimal.valueOf(100));
        expenseAddBindingModel.setDescription("test");

        expensesServiceToTest.createExpense(residentialEntity, expenseAddBindingModel);

        List<Expense> expenses = expensesRepository.findAll();
        Expense expense = expenses.get(0);

        assertEquals(1, expenses.size());
        assertEquals(expenseAddBindingModel.getExpenseDate(), expense.getExpenseDate());
        assertEquals(0, expenseAddBindingModel.getExpenseAmount().compareTo(expense.getAmount()));
        assertEquals(expenseAddBindingModel.getDescription(), expense.getDescription());
    }

    private ResidentialEntity createTestData() {

        ResidentialEntity residentialEntity = new ResidentialEntity();
        residentialEntity.setId(100L);
        residentialEntity.setAccessCode("test");
        residentialEntity.setCity(cityRepository.findByName(CityName.valueOf("София")));
        residentialEntity.setNumberOfApartments(5L);
        residentialEntity.setStreetName("Test");
        residentialEntity.setStreetNumber(String.valueOf(1));
        residentialEntity.setCity(cityRepository.findByName(CityName.valueOf("София")));
        return residentialEntity;
    }

    private Expense createTestExpense() {
        Expense expense = new Expense();
        expense.setAmount(BigDecimal.valueOf(10));
        expense.setExpenseDate(LocalDate.now());
        expensesRepository.save(expense);
        return expense;
    }

    private UserEntity createTestManager() {
        UserEntity manager = new UserEntity();
        Role role = roleRepository.findRoleByName("MANAGER");
        roleRepository.save(role);
        manager.setEmail("test@test.test");
        manager.setFirstName("Test");
        manager.setLastName("Test");
        manager.setUsername("testerManager");
        manager.setPassword("testPassword");
        manager.setPhoneNumber("0777777777");
        manager.setRole(role);
        userRepository.save(manager);
        return manager;
    }
}
