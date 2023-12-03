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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
            expenseFilterBindingModel.setExpenseList(filteredExpenses);

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
            expenseFilterBindingModel.setExpenseList(filteredExpenses);

            return expenseFilterBindingModel;
        }
        return expenseFilterBindingModel;
    }

    @Override
    public String saveDocument(MultipartFile file, Long id) throws IOException {

        Expense expense = expensesRepository.findById(id).orElse(null);

        if (file != null && !file.isEmpty()) {

            if (file.getSize() > 3 * 1024 * 1024) {
                throw new IllegalArgumentException("File size exceeds the allowed limit (3MB)");
            }

            assert expense != null;
            String uploadDirectory = "src/main/resources/static/documents";
            File directory = new File(uploadDirectory);

            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    throw new IOException("Failed to create directory!");
                }
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null) {
                String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));

                // Validate file type (allow only image files)
                if (!fileExtension.matches("\\.(jpg|jpeg|png|gif)$")) {
                    throw new IllegalArgumentException("Invalid file type!");
                }

                String fileName = "document-" + expense.getResidentialEntity().getId() + "-" + expense.getId() + "-" + expense.getExpenseDate() + fileExtension;
                Path filePath = Paths.get(uploadDirectory, fileName);

                try {
                    Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    // Update the user's avatarPath in the database
                    String avatarPath = "/documents/" + fileName;
                    // Update user entity with the avatar path
                    updateExpenseDocument(expensesRepository.findById(id).orElseThrow(), avatarPath);
                    return avatarPath;
                } catch (IOException e) {
                    throw new IOException("Failed to save the file!");
                }
            } else {
                throw new IOException("Invalid file name!");
            }
        } else {
            throw new IllegalArgumentException("File is null or empty!");
        }
    }

    @Override
    public void deleteDocumentFromExpense(Expense expense) {
        if (expense != null) {
            expense.setPicturePath(null);
            expensesRepository.save(expense);
        }
    }

    @Override
    public void updateExpenseDocument(Expense expense, String relativePath) {
        if (expense != null) {
            expense.setPicturePath(relativePath);
            expensesRepository.save(expense);
        } else {
            throw new IllegalArgumentException("Expense is null!");
        }
    }
}
