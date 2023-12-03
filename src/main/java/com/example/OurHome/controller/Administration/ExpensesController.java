package com.example.OurHome.controller.Administration;

import com.example.OurHome.model.Entity.Expense;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.Entity.dto.BindingModels.Expense.ExpenseAddBindingModel;
import com.example.OurHome.model.Entity.dto.BindingModels.Expense.ExpenseEditBindingModel;
import com.example.OurHome.model.Entity.dto.BindingModels.Expense.ExpenseFilterBindingModel;
import com.example.OurHome.model.Entity.dto.ViewModels.UserViewModel;
import com.example.OurHome.service.ExpensesService;
import com.example.OurHome.service.ResidentialEntityService;
import com.example.OurHome.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;

@Controller
public class ExpensesController {

    private final UserService userService;
    private final ResidentialEntityService residentialEntityService;
    private final ExpensesService expensesService;

    public ExpensesController(UserService userService, ResidentialEntityService residentialEntityService, ExpensesService expensesService) {
        this.userService = userService;
        this.residentialEntityService = residentialEntityService;
        this.expensesService = expensesService;
    }

    @GetMapping("/administration/expenses/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityExpenses(@PathVariable("id") Long id) {

        ExpenseFilterBindingModel expenseFilter = expensesService.createDefaultExpenseFilter(getResidentialEntity(id));

        return new ModelAndView("administration-expenses")
                .addObject("userViewModel", getUserViewModel())
                .addObject("residentialEntity", getResidentialEntity(id))
                .addObject("expenseFilterBindingModel", expenseFilter);
    }

    @PostMapping("/administration/expenses/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityExpenses(@PathVariable("id") Long id,
                                                  @Valid ExpenseFilterBindingModel expenseFilter,
                                                  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("administration-expenses")
                    .addObject("userViewModel", getUserViewModel())
                    .addObject("residentialEntity", getResidentialEntity(id))
                    .addObject("expenseFilterBindingModel", expenseFilter);
        }

        expenseFilter = expensesService.createCustomExpenseFilter(expenseFilter.getPeriodStart(),
                expenseFilter.getPeriodEnd(), getResidentialEntity(id));

        return new ModelAndView("administration-expenses")
                .addObject("userViewModel", getUserViewModel())
                .addObject("residentialEntity", getResidentialEntity(id))
                .addObject("expenseFilterBindingModel", expenseFilter);
    }


    @GetMapping("/administration/expenses/add/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView addExpense(@PathVariable("id") Long id) {


        return new ModelAndView("administration-expenses-add")
                .addObject("userViewModel", getUserViewModel())
                .addObject("residentialEntity", getResidentialEntity(id))
                .addObject("expenseAddBindingModel", new ExpenseAddBindingModel());
    }


    @PostMapping("/administration/expenses/add/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView addExpense(@PathVariable("id") Long id,
                                   @Valid ExpenseAddBindingModel expenseAddBindingModel,
                                   BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("administration-expenses-add")
                    .addObject("userViewModel", getUserViewModel())
                    .addObject("expenseAddBindingModel", expenseAddBindingModel);
        }

        expensesService.createExpense(getResidentialEntity(id), expenseAddBindingModel);

        return new ModelAndView("redirect:/administration/expenses/" + id);
    }

    @GetMapping("/administration/expenses/edit/{id}")
    @PreAuthorize("@securityService.checkExpenseModeratorAccess(#id, authentication)")
    public ModelAndView editExpense(@PathVariable("id") Long id) {

        Expense expense = expensesService.findById(id);
        ExpenseEditBindingModel expenseEditBindingModel = expensesService.mapExpenseToBindingModel(expense);

        return new ModelAndView("administration-expenses-edit")
                .addObject("userViewModel", getUserViewModel())
                .addObject("expenseEditBindingModel", expenseEditBindingModel)
                .addObject("entityId", expense.getResidentialEntity().getId());
    }

    @PostMapping("/administration/expenses/edit/{id}")
    @PreAuthorize("@securityService.checkExpenseModeratorAccess(#id, authentication)")
    public ModelAndView editExpense(@PathVariable("id") Long id,
                                    @Valid ExpenseEditBindingModel expenseEditBindingModel,
                                    BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("administration-expenses-edit")
                    .addObject("userViewModel", getUserViewModel())
                    .addObject("expenseEditBindingModel", expenseEditBindingModel);
        }

        Expense expense = expensesService.findById(id);
        expensesService.editExpense(expenseEditBindingModel, expense);

        return new ModelAndView("redirect:/administration/expenses/" + expense.getResidentialEntity().getId());
    }

    @PostMapping("/administration/expenses/delete/{id}")
    @PreAuthorize("@securityService.checkExpenseModeratorAccess(#id, authentication)")
    public ModelAndView deleteExpense(@PathVariable("id") Long id) {

        Expense expense = expensesService.findById(id);
        Long entityId = expense.getResidentialEntity().getId();
        expensesService.deleteExpense(expense);

        return new ModelAndView("redirect:/administration/expenses/" + entityId);
    }

    @GetMapping("/administration/expenses/details/{id}")
    @PreAuthorize("@securityService.checkExpenseModeratorAccess(#id, authentication)")
    public ModelAndView expenseDetails(@PathVariable("id") Long id) {

        Expense expense = expensesService.findById(id);

        return new ModelAndView("administration-expenses-details")
                .addObject("userViewModel", getUserViewModel())
                .addObject("expense", expense);
    }


    @PostMapping("/uploadDocument/{id}")
    @PreAuthorize("@securityService.checkExpenseModeratorAccess(#id, authentication)")
    public ModelAndView uploadDocument(@RequestParam("document") MultipartFile file,
                                       @PathVariable("id") Long id) {

        Expense expense = expensesService.findById(id);

        ModelAndView modelAndView = new ModelAndView("administration-expenses-details")
                .addObject("userViewModel", getUserViewModel())
                .addObject("expense", expense);

        try {
            String relativePath = expensesService.saveDocument(file, id);
            expensesService.updateExpenseDocument(expense, relativePath);
        } catch (IllegalArgumentException | IOException e) {
            modelAndView.addObject("errorMessage", e.getMessage());
        }
        return modelAndView;
    }

    @PostMapping("/deleteDocument/{id}")
    @PreAuthorize("@securityService.checkExpenseModeratorAccess(#id, authentication)")
    public ModelAndView deleteDocument(@PathVariable("id") Long id) {

        Expense expense = expensesService.findById(id);

        ModelAndView modelAndView = new ModelAndView("administration-expenses-details")
                .addObject("userViewModel", getUserViewModel())
                .addObject("expense", expense);

        if (expense.getPicturePath() != null) {
            String documentPath = expense.getPicturePath();

            // Delete the document file from the file system
            String absolutePath = "src/main/resources/static" + documentPath;
            File file = new File(absolutePath);

            if (file.exists()) {
                if (file.delete()) {
                    expensesService.deleteDocumentFromExpense(expense);
                } else {
                    modelAndView.addObject("deleteError", "Failed to delete the document!");
                }
            } else {
                modelAndView.addObject("deleteError", "Document not found!");
            }
        } else {
            modelAndView.addObject("deleteError", "No document associated with this expense!");
        }

        return new ModelAndView("redirect:/administration/expenses/details/ " + expense.getId());
    }

    /**
     * View residential entity expense doc by property owner
     * @param id expense id
     * @return expense-document
     */
    @GetMapping("/expenses/details/{id}")
    @PreAuthorize("@securityService.checkExpenseUserAccess(#id, authentication)")
    public ModelAndView residentialEntityExpenseDetails(@PathVariable("id") Long id) {

        Expense expense = expensesService.findById(id);
        return new ModelAndView("expense-document")
                .addObject(expense);
    }

    /**
     * Method returns currently logged user
     *
     * @return UserEntity
     */
    private UserViewModel getUserViewModel() {
        UserEntity loggedUser = userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        return userService.getUserViewData(loggedUser);
    }

    /**
     * Method returns a ResidentialEntity
     *
     * @param id residential entity id
     * @return ResidentialEntity
     */
    private ResidentialEntity getResidentialEntity(Long id) {
        return residentialEntityService.findResidentialEntityById(id).orElse(null);
    }

}