package com.example.OurHome.controller.Administration;

import com.example.OurHome.model.Entity.Expense;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.Financial.ExpenseAddBindingModel;
import com.example.OurHome.model.dto.BindingModels.Financial.ExpenseEditBindingModel;
import com.example.OurHome.model.dto.BindingModels.Financial.ExpenseFilterBindingModel;
import com.example.OurHome.model.dto.BindingModels.Financial.IncomesBindingModel;
import com.example.OurHome.model.dto.ViewModels.UserViewModel;
import com.example.OurHome.service.FinancialService;
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
public class FinancialController {

    private final UserService userService;
    private final ResidentialEntityService residentialEntityService;
    private final FinancialService financialService;

    public FinancialController(UserService userService, ResidentialEntityService residentialEntityService, FinancialService financialService) {
        this.userService = userService;
        this.residentialEntityService = residentialEntityService;
        this.financialService = financialService;
    }

    /**
     * Administration -> Financial
     *
     * @param id Residential entity Id
     */
    @GetMapping("/administration/financial/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityFinancials(@PathVariable("id") Long id) {

        ExpenseFilterBindingModel expenseFilter = financialService.createDefaultExpenseFilter(getResidentialEntity(id));

        return new ModelAndView("administration-financial")
                .addObject("userViewModel", getUserViewModel())
                .addObject("residentialEntity", getResidentialEntity(id))
                .addObject("expenseFilterBindingModel", expenseFilter);
    }

    /**
     * Administration -> Financial -> Custom filter
     *
     * @param id Residential entity Id
     */
    @PostMapping("/administration/financial/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityFinancials(@PathVariable("id") Long id,
                                                    @Valid ExpenseFilterBindingModel expenseFilter,
                                                    BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("administration-financial")
                    .addObject("userViewModel", getUserViewModel())
                    .addObject("residentialEntity", getResidentialEntity(id))
                    .addObject("expenseFilterBindingModel", expenseFilter);
        }

        expenseFilter = financialService.createCustomExpenseFilter(expenseFilter.getPeriodStart(),
                expenseFilter.getPeriodEnd(), getResidentialEntity(id));

        return new ModelAndView("administration-financial")
                .addObject("userViewModel", getUserViewModel())
                .addObject("residentialEntity", getResidentialEntity(id))
                .addObject("expenseFilterBindingModel", expenseFilter);
    }

    /**
     * Administration -> Financial -> Add expense
     *
     * @param id Residential entity Id
     */
    @GetMapping("/administration/financial/add/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView addExpense(@PathVariable("id") Long id) {


        return new ModelAndView("administration-financial-add")
                .addObject("userViewModel", getUserViewModel())
                .addObject("residentialEntity", getResidentialEntity(id))
                .addObject("expenseAddBindingModel", new ExpenseAddBindingModel());
    }

    /**
     * Administration -> Financial -> Add expense
     *
     * @param id Residential entity id
     *           POST
     */
    @PostMapping("/administration/financial/add/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView addExpense(@PathVariable("id") Long id,
                                   @Valid ExpenseAddBindingModel expenseAddBindingModel,
                                   BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("administration-financial-add")
                    .addObject("userViewModel", getUserViewModel())
                    .addObject("expenseAddBindingModel", expenseAddBindingModel);
        }

        financialService.createExpense(getResidentialEntity(id), expenseAddBindingModel);

        return new ModelAndView("redirect:/administration/financial/" + id);
    }

    /**
     * Administration -> Financial -> Edit expense
     *
     * @param id Residential entity Id
     */
    @GetMapping("/administration/financial/edit/{id}")
    @PreAuthorize("@securityService.checkExpenseModeratorAccess(#id, authentication)")
    public ModelAndView editExpense(@PathVariable("id") Long id) {

        Expense expense = financialService.findById(id);
        ExpenseEditBindingModel expenseEditBindingModel = financialService.mapExpenseToBindingModel(expense);

        return new ModelAndView("administration-financial-edit")
                .addObject("userViewModel", getUserViewModel())
                .addObject("expenseEditBindingModel", expenseEditBindingModel)
                .addObject("entityId", expense.getResidentialEntity().getId());
    }

    /**
     * Administration -> Financial -> Edit expense
     *
     * @param id Residential entity id
     *           POST
     */
    @PostMapping("/administration/financial/edit/{id}")
    @PreAuthorize("@securityService.checkExpenseModeratorAccess(#id, authentication)")
    public ModelAndView editExpense(@PathVariable("id") Long id,
                                    @Valid ExpenseEditBindingModel expenseEditBindingModel,
                                    BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("administration-financial-edit")
                    .addObject("userViewModel", getUserViewModel())
                    .addObject("expenseEditBindingModel", expenseEditBindingModel);
        }

        Expense expense = financialService.findById(id);
        financialService.editExpense(expenseEditBindingModel, expense);

        return new ModelAndView("redirect:/administration/financial/" + expense.getResidentialEntity().getId());
    }

    /**
     * Administration -> Financial -> Delete expense
     *
     * @param id Residential entity id
     *           POST
     */
    @PostMapping("/administration/financial/delete/{id}")
    @PreAuthorize("@securityService.checkExpenseModeratorAccess(#id, authentication)")
    public ModelAndView deleteExpense(@PathVariable("id") Long id) {

        Expense expense = financialService.findById(id);
        Long entityId = expense.getResidentialEntity().getId();
        financialService.deleteExpense(expense);

        return new ModelAndView("redirect:/administration/financial/" + entityId);
    }

    /**
     * Administration -> Financial -> Expense details
     *
     * @param id Residential Expense id
     */
    @GetMapping("/administration/financial/details/{id}")
    @PreAuthorize("@securityService.checkExpenseModeratorAccess(#id, authentication)")
    public ModelAndView expenseDetails(@PathVariable("id") Long id) {

        Expense expense = financialService.findById(id);

        return new ModelAndView("administration-financial-details")
                .addObject("userViewModel", getUserViewModel())
                .addObject("expense", expense);
    }

    /**
     * Administration -> Financial -> Expense details -> Upload document
     *
     * @param id Residential Expense id
     */
    @PostMapping("/uploadDocument/{id}")
    @PreAuthorize("@securityService.checkExpenseModeratorAccess(#id, authentication)")
    public ModelAndView uploadDocument(@RequestParam("document") MultipartFile file,
                                       @PathVariable("id") Long id) {

        Expense expense = financialService.findById(id);

        ModelAndView modelAndView = new ModelAndView("administration-financial-details")
                .addObject("userViewModel", getUserViewModel())
                .addObject("expense", expense);

        try {
            String relativePath = financialService.saveDocument(file, id);
            financialService.updateExpenseDocument(expense, relativePath);
        } catch (IllegalArgumentException | IOException e) {
            modelAndView.addObject("errorMessage", e.getMessage());
        }
        return modelAndView;
    }

    /**
     * Administration -> Financial -> Expense details -> Delete document
     *
     * @param id Residential Expense id
     */
    @PostMapping("/deleteDocument/{id}")
    @PreAuthorize("@securityService.checkExpenseModeratorAccess(#id, authentication)")
    public ModelAndView deleteDocument(@PathVariable("id") Long id) {

        Expense expense = financialService.findById(id);

        ModelAndView modelAndView = new ModelAndView("administration-financial-details")
                .addObject("userViewModel", getUserViewModel())
                .addObject("expense", expense);

        if (expense.getPicturePath() != null) {
            String documentPath = expense.getPicturePath();

            // Delete the document file from the file system
            String absolutePath = "src/main/resources/static" + documentPath;
            File file = new File(absolutePath);

            if (file.exists()) {
                if (file.delete()) {
                    financialService.deleteDocumentFromExpense(expense);
                } else {
                    modelAndView.addObject("deleteError", "Failed to delete the document!");
                }
            } else {
                modelAndView.addObject("deleteError", "Document not found!");
            }
        } else {
            modelAndView.addObject("deleteError", "No document associated with this expense!");
        }

        return new ModelAndView("redirect:/administration/financial/details/ " + expense.getId());
    }

    /**
     * View residential entity expense doc by property owner (member of this Residential entity)
     * Used in Property -> ExpensesRE -> Doc
     *
     * @param id expense id
     * @return expense-document
     */
    @GetMapping("/financial/details/{id}")
    @PreAuthorize("@securityService.checkExpenseUserAccess(#id, authentication)")
    public ModelAndView residentialEntityExpenseDetails(@PathVariable("id") Long id) {

        Expense expense = financialService.findById(id);
        return new ModelAndView("expense-document")
                .addObject(expense);
    }


    /**
     * Administration -> Financial -> Incomes
     *
     * @param id Residential entity Id
     */
    @GetMapping("/administration/financial/incomes/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityFinancialIncomes(@PathVariable("id") Long id) {

        IncomesBindingModel incomesBindingModel = financialService.mapIncomesBindingModel(id);

        return new ModelAndView("administration-financial-incomes")
                .addObject("userViewModel", getUserViewModel())
                .addObject("residentialEntity", getResidentialEntity(id))
                .addObject("incomesBindingModel", incomesBindingModel);
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