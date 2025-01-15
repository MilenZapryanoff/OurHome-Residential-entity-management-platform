package com.example.OurHome.controller.Administration;

import com.example.OurHome.model.Entity.Expense;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.dto.BindingModels.Financial.ExpenseAddBindingModel;
import com.example.OurHome.model.dto.BindingModels.Financial.ExpenseEditBindingModel;
import com.example.OurHome.model.dto.BindingModels.Financial.ExpenseFilterBindingModel;
import com.example.OurHome.model.dto.BindingModels.Financial.IncomesBindingModel;
import com.example.OurHome.service.FinancialService;
import com.example.OurHome.service.ResidentialEntityService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

@Controller
public class FinancialController {

    private final ResidentialEntityService residentialEntityService;
    private final FinancialService financialService;

    public FinancialController(ResidentialEntityService residentialEntityService, FinancialService financialService) {
        this.residentialEntityService = residentialEntityService;
        this.financialService = financialService;
    }

    /**
     * Administration -> Financial
     *
     * @param id Condominium Id
     */
    @GetMapping("/administration/financial/expenses/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityFinancials(@PathVariable("id") Long id,
                                                    @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ExpenseFilterBindingModel expenseFilter = financialService.createDefaultExpenseFilter(getResidentialEntity(id));

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-financial-expenses") : new ModelAndView("en/administration/administration-financial-expenses");

        return view
                .addObject("residentialEntity", getResidentialEntity(id))
                .addObject("expenseFilterBindingModel", expenseFilter);
    }

    /**
     * Administration -> Financial -> Custom filter
     *
     * @param id Condominium Id
     */
    @PostMapping("/administration/financial/expenses/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityFinancials(@PathVariable("id") Long id,
                                                    @Valid ExpenseFilterBindingModel expenseFilter,
                                                    BindingResult bindingResult,
                                                    @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-financial-expenses") : new ModelAndView("en/administration/administration-financial-expenses");

        view.addObject("residentialEntity", getResidentialEntity(id));


        if (bindingResult.hasErrors()) {
            return view;
        }

        view.addObject("expenseFilterBindingModel", financialService.createCustomExpenseFilter(expenseFilter.getPeriodStart(), expenseFilter.getPeriodEnd(), getResidentialEntity(id)));

        return view;
    }

    /**
     * Administration -> Financial -> Add expense
     *
     * @param id Condominium Id
     */
    @GetMapping("/administration/financial/expenses/add/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView addExpense(@PathVariable("id") Long id,
                                   @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ExpenseAddBindingModel expenseAddBindingModel = new ExpenseAddBindingModel();
        expenseAddBindingModel.setExpenseDate(LocalDate.now());

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-financial-add") : new ModelAndView("en/administration/administration-financial-add");

        return view
                .addObject("residentialEntity", getResidentialEntity(id))
                .addObject("expenseAddBindingModel", expenseAddBindingModel);
    }

    /**
     * Administration -> Financial -> Add expense
     *
     * @param id Condominium id
     *           POST
     */
    @PostMapping("/administration/financial/expenses/add/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView addExpense(@PathVariable("id") Long id,
                                   @Valid ExpenseAddBindingModel expenseAddBindingModel,
                                   BindingResult bindingResult,
                                   @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-financial-add") : new ModelAndView("en/administration/administration-financial-add");

        if (bindingResult.hasErrors()) {
            return view.addObject("expenseAddBindingModel", expenseAddBindingModel);
        }

        financialService.createExpense(getResidentialEntity(id), expenseAddBindingModel);

        return new ModelAndView("redirect:/administration/financial/expenses/" + id);
    }

    /**
     * Administration -> Financial -> Edit expense
     *
     * @param id Condominium Id
     */
    @GetMapping("/administration/financial/expenses/edit/{id}")
    @PreAuthorize("@securityService.checkExpenseModeratorAccess(#id, authentication)")
    public ModelAndView editExpense(@PathVariable("id") Long id,
                                    @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        Expense expense = financialService.findById(id);
        ExpenseEditBindingModel expenseEditBindingModel = financialService.mapExpenseToBindingModel(expense);

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-financial-edit") : new ModelAndView("en/administration/administration-financial-edit");

        return view.addObject("expenseEditBindingModel", expenseEditBindingModel)
                .addObject("entityId", expense.getResidentialEntity().getId());
    }

    /**
     * Administration -> Financial -> Edit expense
     *
     * @param id Condominium id
     *           POST
     */
    @PostMapping("/administration/financial/expenses/edit/{id}")
    @PreAuthorize("@securityService.checkExpenseModeratorAccess(#id, authentication)")
    public ModelAndView editExpense(@PathVariable("id") Long id,
                                    @Valid ExpenseEditBindingModel expenseEditBindingModel,
                                    BindingResult bindingResult,
                                    @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-financial-edit") : new ModelAndView("en/administration/administration-financial-edit");

        if (bindingResult.hasErrors()) {
            return view.addObject("expenseEditBindingModel", expenseEditBindingModel);
        }

        Expense expense = financialService.findById(id);
        financialService.editExpense(expenseEditBindingModel, expense);

        return new ModelAndView("redirect:/administration/financial/expenses/" + expense.getResidentialEntity().getId());
    }

    /**
     * Administration -> Financial -> Delete expense
     *
     * @param id Condominium id
     *           POST
     */
    @PostMapping("/administration/financial/expenses/delete/{id}")
    @PreAuthorize("@securityService.checkExpenseModeratorAccess(#id, authentication)")
    public ModelAndView deleteExpense(@PathVariable("id") Long id) {

        Expense expense = financialService.findById(id);
        Long entityId = expense.getResidentialEntity().getId();
        financialService.deleteExpense(expense);

        return new ModelAndView("redirect:/administration/financial/expenses/" + entityId);
    }

    /**
     * Administration -> Financial -> Expense details
     *
     * @param id Residential Expense id
     */
    @GetMapping("/administration/financial/expenses/details/{id}")
    @PreAuthorize("@securityService.checkExpenseModeratorAccess(#id, authentication)")
    public ModelAndView expenseDetails(@PathVariable("id") Long id,
                                       @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        Expense expense = financialService.findById(id);

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-financial-details") : new ModelAndView("en/administration/administration-financial-details");

        return view.addObject("expense", expense);
    }

    /**
     * Administration -> Financial -> Expense details -> Upload document
     *
     * @param id Residential Expense id
     */
    @PostMapping("/uploadDocument/{id}")
    @PreAuthorize("@securityService.checkExpenseModeratorAccess(#id, authentication)")
    public ModelAndView uploadDocument(@RequestParam("document") MultipartFile file,
                                       @PathVariable("id") Long id,
                                       @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        Expense expense = financialService.findById(id);

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-financial-details") : new ModelAndView("en/administration/administration-financial-details");

        view.addObject("expense", expense);

        try {
            String relativePath = financialService.saveDocument(file, id);
            financialService.updateExpenseDocument(expense, relativePath);
        } catch (IllegalArgumentException | IOException e) {
            view.addObject("errorMessage", e.getMessage());
        }
        return view;
    }

    /**
     * Administration -> Financial -> Expense details -> Delete document
     *
     * @param id Residential Expense id
     */
    @PostMapping("/deleteDocument/{id}")
    @PreAuthorize("@securityService.checkExpenseModeratorAccess(#id, authentication)")
    public ModelAndView deleteDocument(@PathVariable("id") Long id,
                                       @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        Expense expense = financialService.findById(id);

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-financial-details") : new ModelAndView("en/administration/administration-financial-details");

        view.addObject("expense", expense);

        if (expense.getPicturePath() != null) {
            String documentPath = expense.getPicturePath();

            // Delete the document file from the file system
            String absolutePath = "src/main/resources/static" + documentPath;
            File file = new File(absolutePath);

            if (file.exists()) {
                if (file.delete()) {
                    financialService.deleteDocumentFromExpense(expense);
                } else {
                    view.addObject("deleteError", "Failed to delete the document!");
                }
            } else {
                view.addObject("deleteError", "Document not found!");
            }
        } else {
            view.addObject("deleteError", "No document associated with this expense!");
        }

        return new ModelAndView("redirect:/administration/financial/expenses/details/ " + expense.getId());
    }

    /**
     * Administration -> Financial -> Toggle expense visibility
     *
     * @param id Residential Expense id
     */
    @PostMapping("/administration/financial/expenses/changeExpensesVisibility/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView changeExpensesVisibility(@PathVariable("id") Long id) {

        residentialEntityService.changeExpensesVisibility(id);

        return new ModelAndView("redirect:/administration/financial/expenses/" + id + "#fees");
    }


    /**
     * View Condominium expense doc by property owner (member of this Condominium)
     * Used in Property -> ExpensesRE -> Doc
     *
     * @param id expense id
     * @return expense-document
     */
    @GetMapping("/expenses/details/{id}")
    @PreAuthorize("@securityService.checkExpenseUserAccess(#id, authentication)")
    public ModelAndView residentialEntityExpenseDetails(@PathVariable("id") Long id) {

        Expense expense = financialService.findById(id);
        return new ModelAndView("en/expense-document")
                .addObject(expense);
    }


    /**
     * Administration -> Financial -> Incomes
     *
     * @param id Condominium Id
     */
    @GetMapping("/administration/financial/incomes/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityFinancialIncomes(@PathVariable("id") Long id,
                                                          @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        IncomesBindingModel incomesBindingModel = financialService.mapIncomesBindingModel(id);

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/administration/administration-financial-incomes") : new ModelAndView("en/administration/administration-financial-incomes");

        return view
                .addObject("residentialEntity", getResidentialEntity(id))
                .addObject("incomesBindingModel", incomesBindingModel);
    }

    /**
     * Administration -> Financial -> Toggle incomes visibility
     *
     * @param id Residential Expense id
     */
    @PostMapping("/administration/financial/changeIncomesVisibility/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView changeIncomesVisibility(@PathVariable("id") Long id) {

        residentialEntityService.changeIncomesVisibility(id);

        return new ModelAndView("redirect:/administration/financial/incomes/" + id + "#fees");
    }

    /**
     * Method returns a ResidentialEntity
     *
     * @param id Condominium id
     * @return ResidentialEntity
     */
    private ResidentialEntity getResidentialEntity(Long id) {
        return residentialEntityService.findResidentialEntityById(id).orElse(null);
    }

    /**
     * Language resolver
     *
     * @param lang This value shows the language
     * @return boolean
     */
    private boolean resolveView(String lang) {
        return "bg".equals(lang);
    }
}