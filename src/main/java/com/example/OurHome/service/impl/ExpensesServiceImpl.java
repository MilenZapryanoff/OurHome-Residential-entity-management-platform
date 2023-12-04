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

    /**
     * Expense creating
     * @param residentialEntity The ResidentialEntity which the Expense belongs to.
     * @param expenseAddBindingModel data from Frontend
     */
    @Override
    public void createExpense(ResidentialEntity residentialEntity, ExpenseAddBindingModel expenseAddBindingModel) {
        Expense expense = modelMapper.map(expenseAddBindingModel, Expense.class);
        expense.setResidentialEntity(residentialEntity);

        expensesRepository.save(expense);
    }

    /**
     * Mapping between Expense and ExpenseEditBindingModel
     * @param expense Expense
     * @return mapped ExpenseEditBindingModel
     */
    @Override
    public ExpenseEditBindingModel mapExpenseToBindingModel(Expense expense) {
        return modelMapper.map(expense, ExpenseEditBindingModel.class);
    }

    /**
     * Expense modification method
     * @param expenseEditBindingModel data from Frontend
     * @param expense Expense that has to be modified
     */
    @Override
    public void editExpense(ExpenseEditBindingModel expenseEditBindingModel, Expense expense) {
        modelMapper.map(expenseEditBindingModel, expense);
        expensesRepository.save(expense);
    }

    /**
     * Expense deletion
     * @param expense The expense that has to be deleted
     */
    @Override
    public void deleteExpense(Expense expense) {
        expensesRepository.delete(expense);
    }


    /**
     * Default filter for expenses
     * @param residentialEntity Residential entity
     * @return ExpenseFilterBindingModel with start date the first day of the current month and end date, the last day
     * of the current month
     */
    @Override
    public ExpenseFilterBindingModel createDefaultExpenseFilter(ResidentialEntity residentialEntity) {

        ExpenseFilterBindingModel expenseFilterBindingModel = new ExpenseFilterBindingModel();
        if (residentialEntity != null) {

            LocalDate startPeriod = LocalDate.now().withDayOfMonth(1);
            LocalDate endPeriod = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
            return getExpenseFilterBindingModel(startPeriod, endPeriod, residentialEntity, expenseFilterBindingModel);
        }
        return expenseFilterBindingModel;
    }

    /**
     * User selected filter for expenses
     * @param startPeriod LocalDate start period
     * @param endPeriod LocalDate end period
     * @param residentialEntity Residential entity
     * @return ExpenseFilterBindingModel with user selected start & end dates
     */
    @Override
    public ExpenseFilterBindingModel createCustomExpenseFilter(LocalDate startPeriod, LocalDate endPeriod, ResidentialEntity residentialEntity) {

        ExpenseFilterBindingModel expenseFilterBindingModel = new ExpenseFilterBindingModel();
        if (residentialEntity != null) {
            return getExpenseFilterBindingModel(startPeriod, endPeriod, residentialEntity, expenseFilterBindingModel);
        }
        return expenseFilterBindingModel;
    }

    /**
     * Storing the uploaded expense document (picture format) in the file system.
     * @param file uploaded from manager document (picture)
     * @param id Expense id
     * @return String with documentPath
     * @throws IOException If error occurs
     */
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
                    // Update the user's documentPath in the database
                    String documentPath = "/documents/" + fileName;
                    // Update user entity with the avatar path
                    updateExpenseDocument(expensesRepository.findById(id).orElseThrow(), documentPath);
                    return documentPath;
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

    /**
     *Delete expense document method
     */
    @Override
    public void deleteDocumentFromExpense(Expense expense) {
        if (expense != null) {
            expense.setPicturePath(null);
            expensesRepository.save(expense);
        }
    }

    /**
     * Update of expense picturePath field.
     * @param expense Expense entity
     * @param relativePath the path where the picture is stored
     */
    @Override
    public void updateExpenseDocument(Expense expense, String relativePath) {
        if (expense != null) {
            expense.setPicturePath(relativePath);
            expensesRepository.save(expense);
        } else {
            throw new IllegalArgumentException("Expense is null!");
        }
    }

    /**
     * Expense filter method.
     * @param startPeriod LocalDate startPeriod
     * @param endPeriod LocalDate startPeriod
     * @param residentialEntity Residential entity
     * @param expenseFilterBindingModel FrontEnd view
     * @return ExpenseFilterBindingModel
     */

    private ExpenseFilterBindingModel getExpenseFilterBindingModel(LocalDate startPeriod, LocalDate endPeriod, ResidentialEntity residentialEntity, ExpenseFilterBindingModel expenseFilterBindingModel) {
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
}
