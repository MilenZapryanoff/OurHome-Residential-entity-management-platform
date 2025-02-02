package com.example.OurHome.controller;

import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.Report;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.Reports.ReportAddBindingModel;
import com.example.OurHome.model.dto.BindingModels.Reports.ReportEditBindingModel;
import com.example.OurHome.repo.ReportRepository;
import com.example.OurHome.service.PropertyService;
import com.example.OurHome.service.ReportService;
import com.example.OurHome.service.ResidentialEntityService;
import com.example.OurHome.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;
import java.util.Optional;

@Controller
public class ReportsController {

    private final ResidentialEntityService residentialEntityService;
    private final ReportService reportService;
    private final UserService userService;
    private final ReportRepository reportRepository;
    private final PropertyService propertyService;

    public ReportsController(ResidentialEntityService residentialEntityService, ReportService reportService, UserService userService, ReportRepository reportRepository, PropertyService propertyService) {
        this.residentialEntityService = residentialEntityService;
        this.reportService = reportService;
        this.userService = userService;
        this.reportRepository = reportRepository;
        this.propertyService = propertyService;
    }

    /**
     * Reports in Administration
     *
     * @param id Condominium id
     * @return view administration - PROPERTY TYPES
     */
    @GetMapping("/administration/reports/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView residentialEntityReports(@PathVariable("id") Long id,
                                                 @CookieValue(value = "lang", defaultValue = "bg") String lang) {
        return new ModelAndView(lang + "/administration/administration-reports")
                .addObject("residentialEntity", getResidentialEntity(id));
    }

    /**
     * Add new report in Administration -> Reports
     *
     * @param id Condominium id
     * @return view administration - PROPERTY TYPES add form
     */
    @GetMapping("/administration/reports/add/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView addReportByManager(@PathVariable("id") Long id,
                                           @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ReportAddBindingModel reportAddBindingModel = new ReportAddBindingModel();

        return new ModelAndView(lang + "/administration/administration-reports-add")
                .addObject("residentialEntity", getResidentialEntity(id))
                .addObject("reportAddBindingModel", reportAddBindingModel);
    }

    /**
     * Process and save a new report with images
     *
     * @param id                    Condominium id
     * @param reportAddBindingModel Report binding model
     * @return Redirect to reports list
     */
    @PostMapping("/administration/reports/add/{id}")
    @PreAuthorize("@securityService.checkResidentialEntityModeratorAccess(#id, authentication)")
    public ModelAndView addReportByManager(@PathVariable("id") Long id,
                                           @ModelAttribute("reportAddBindingModel") ReportAddBindingModel reportAddBindingModel,
                                           @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        try {
            // Извикваме сервиза за обработка и валидиране на данните
            reportService.processReport(reportAddBindingModel, id, getLoggedUser());
        } catch (IllegalArgumentException e) {
            // Обработка на грешка

            return new ModelAndView(lang + "/administration/administration-reports-add")
                    .addObject("residentialEntity", getResidentialEntity(id))
                    .addObject("reportAddBindingModel", reportAddBindingModel)
                    .addObject("addFailed", true)
                    .addObject("errorMsg", e.getMessage());
        }
        return new ModelAndView("redirect:/administration/reports/" + id);
    }

    /**
     * Edit report in Administration -> Reports
     *
     * @param id Condominium id
     * @return view administration - PROPERTY TYPES add form
     */
    @GetMapping("/administration/reports/edit/{id}")
    @PreAuthorize("@securityService.checkReportModeratorAccess(#id, authentication)")
    public ModelAndView editReportByManager(@PathVariable("id") Long id,
                                            @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ReportEditBindingModel reportEditBindingModel = reportService.mapReportToBindingModel(id);

        return new ModelAndView(lang + "/administration/administration-reports-edit")
                .addObject("reportEditBindingModel", reportEditBindingModel)
                .addObject("reportId", id);
    }

    /**
     * Edit report in Administration -> Reports
     *
     * @param id                     Condominium id
     * @param reportEditBindingModel Report binding model
     * @return Redirect to reports list
     */
    @PostMapping("/administration/reports/edit/{id}")
    @PreAuthorize("@securityService.checkReportModeratorAccess(#id, authentication)")
    public ModelAndView editReportByManager(@PathVariable("id") Long id,
                                            @ModelAttribute("reportEditBindingModel") ReportEditBindingModel reportEditBindingModel,
                                            @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        Report report = reportRepository.findById(id).orElse(null);
        Long residentialEntityId = Objects.requireNonNull(report).getResidentialEntity().getId();

        try {
            // Извикваме сервиза за обработка и валидиране на данните
            reportService.resolveReport(reportEditBindingModel, id, getLoggedUser());
        } catch (IllegalArgumentException e) {
            // Обработка на грешка

            return new ModelAndView(lang + "/administration/administration-reports-edit")
                    .addObject("residentialEntity", getResidentialEntity(id))
                    .addObject("reportEditBindingModel", reportEditBindingModel)
                    .addObject("addFailed", true)
                    .addObject("errorMsg", e.getMessage());
        }
        return new ModelAndView("redirect:/administration/reports/" + residentialEntityId);
    }

    /**
     * View Report document (picture)
     *
     * @param id report id
     * @return expense-document
     */
    @GetMapping("/reports/details/{id}")
    @PreAuthorize("@securityService.checkReportImageViewAccess(#id, authentication)")
    public ModelAndView reportDetails(@PathVariable("id") Long id,
                                      @RequestParam(name = "picture", required = false) String picture) {

        Report report = reportRepository.findById(id).orElse(null);

        ModelAndView view = new ModelAndView("report-document")
                .addObject("report", report);

        if (picture != null) {
            view.addObject("picture", picture);
        }

        return view;
    }

    /**
     * Delete report with images
     *
     * @param id                    Condominium id
     * @param reportAddBindingModel Report binding model
     * @return Redirect to reports list
     */
    @PostMapping("/administration/reports/delete/{id}")
    @PreAuthorize("@securityService.checkReportModeratorAccess(#id, authentication)")
    public ModelAndView deleteReportByManager(@PathVariable("id") Long id,
                                              @ModelAttribute("reportAddBindingModel") ReportAddBindingModel reportAddBindingModel,
                                              @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        Report report = reportRepository.findById(id).orElse(null);
        Long residentialEntityId = Objects.requireNonNull(report).getResidentialEntity().getId();

        try {
            // Calling service for deleting a report
            reportService.deleteReport(id);
        } catch (IllegalArgumentException e) {
            // Handling errors
            return new ModelAndView(lang + "/administration/administration-reports")
                    .addObject("residentialEntity", getResidentialEntity(id))
                    .addObject("reportAddBindingModel", reportAddBindingModel)
                    .addObject("deleteFailed", true)
                    .addObject("errorMsg", e.getMessage());
        }
        return new ModelAndView("redirect:/administration/reports/" + residentialEntityId);
    }


    /**
     * PROPERTY -> Reports Section
     */
    @GetMapping("/property/reports/{id}")
    @PreAuthorize("@securityService.checkPropertyOwnerFullAccess(#id, authentication)")
    public ModelAndView reportsAll(
            @PathVariable("id") Long id,
            @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/property/property-reports-all") : new ModelAndView("en/property/property-reports-all");

        return view
                .addObject("property", getProperty(id))
                .addObject("loggedUser", getLoggedUser());
    }

    /**
     * PROPERTY -> Reports Section
     */
    @GetMapping("/property/reports/my/{id}")
    @PreAuthorize("@securityService.checkPropertyOwnerFullAccess(#id, authentication)")
    public ModelAndView myReports(
            @PathVariable("id") Long id,
            @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/property/property-reports-my") : new ModelAndView("en/property/property-reports-my");

        return view
                .addObject("property", getProperty(id))
                .addObject("loggedUser", getLoggedUser());
    }

    /**
     * PROPERTY -> Reports Section
     */
    @GetMapping("/property/reports/view/{id}")
    @PreAuthorize("@securityService.checkReportUserViewAccess(#id, authentication)")
    public ModelAndView viewReportByOwner(
            @PathVariable("id") Long id,
            @CookieValue(value = "lang", defaultValue = "bg") String lang,
            @RequestParam(value = "propertyId", required = false) Long propertyId) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/property/property-reports-view") : new ModelAndView("en/property/property-reports-view");

        return view
                .addObject("property", getProperty(propertyId))
                .addObject("loggedUser", getLoggedUser())
                .addObject("report", reportRepository.findById(id).orElse(null));
    }


    /**
     * Add new report as an owner of property
     *
     * @param id property id
     * @return view property - list of all reports
     */
    @GetMapping("/property/reports/add/{id}")
    @PreAuthorize("@securityService.checkPropertyOwnerFullAccess(#id, authentication)")
    public ModelAndView addReportByOwner(@PathVariable("id") Long id,
                                         @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ReportAddBindingModel reportAddBindingModel = new ReportAddBindingModel();

        return new ModelAndView(lang + "/property/property-reports-add")
                .addObject("property", getProperty(id))
                .addObject("reportAddBindingModel", reportAddBindingModel);
    }

    /**
     * Add new report as an owner of property
     *
     * @param id property id
     * @return view property - list of all reports
     */
    @PostMapping("/property/reports/add/{id}")
    @PreAuthorize("@securityService.checkPropertyOwnerFullAccess(#id, authentication)")
    public ModelAndView viewReportByOwner(@PathVariable("id") Long id,
                                          @ModelAttribute("reportAddBindingModel") ReportAddBindingModel reportAddBindingModel,
                                          @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        Property property = propertyService.findPropertyById(id);

        try {
            // Извикваме сервиза за обработка и валидиране на данните
            reportService.processReport(reportAddBindingModel, property.getResidentialEntity().getId(), getLoggedUser());
        } catch (IllegalArgumentException e) {
            // Обработка на грешка

            return new ModelAndView(lang + "/property/property-reports-add")
                    .addObject("property", getProperty(id))
                    .addObject("reportAddBindingModel", reportAddBindingModel)
                    .addObject("addFailed", true)
                    .addObject("errorMsg", e.getMessage());
        }
        return new ModelAndView("redirect:/property/reports/my/" + id);
    }

    /**
     * Edit report in Proeprty -> My Reports
     *
     * @param id Condominium id
     * @return view administration - PROPERTY TYPES add form
     */
    @GetMapping("/property/reports/edit/{id}")
    @PreAuthorize("@securityService.checkReportOwnerAccess(#id, authentication)")
    public ModelAndView editReportByCreator(@PathVariable("id") Long id,
                                            @CookieValue(value = "lang", defaultValue = "bg") String lang,
                                            @RequestParam(value = "propertyId", required = false) Long propertyId) {

        ReportEditBindingModel reportEditBindingModel = reportService.mapReportToBindingModel(id);
        Report report = reportRepository.findById(id).orElse(null);

        return new ModelAndView(lang + "/property/property-reports-edit")
                .addObject("reportEditBindingModel", reportEditBindingModel)
                .addObject("propertyId", propertyId)
                .addObject("report", report);
    }

    /**
     * Edit report in Proeprty -> My Reports
     *
     * @param id                     Condominium id
     * @param reportEditBindingModel Report binding model
     * @return Redirect to reports list
     */
    @PostMapping("/property/reports/edit/{id}")
    @PreAuthorize("@securityService.checkReportOwnerAccess(#id, authentication)")
    public ModelAndView editReportByCreator(@PathVariable("id") Long id,
                                            @ModelAttribute("reportEditBindingModel") ReportEditBindingModel reportEditBindingModel,
                                            @RequestParam(value = "propertyId", required = false) Long propertyId,
                                            @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        Report report = reportRepository.findById(id).orElse(null);

        try {
            // Извикваме сервиза за обработка и валидиране на данните
            reportService.editReport(reportEditBindingModel, id, getLoggedUser());
        } catch (IllegalArgumentException e) {
            // Обработка на грешка
            return new ModelAndView(lang + "/reports/reports-reports-edit")
                    .addObject("reportEditBindingModel", reportEditBindingModel)
                    .addObject("addFailed", true)
                    .addObject("errorMsg", e.getMessage());
        }
        return new ModelAndView("redirect:/property/reports/my/" + propertyId);
    }


    /**
     * Delete report with images by report owner
     *
     * @param id Condominium id
     * @return Redirect to reports list
     */
    @PostMapping("/property/reports/delete/{id}")
    @PreAuthorize("@securityService.checkReportOwnerAccess(#id, authentication)")
    public ModelAndView deleteReportByUser(@PathVariable("id") Long id,
                                           @CookieValue(value = "lang", defaultValue = "bg") String lang,
                                           @RequestParam(value = "propertyId", required = false) Long propertyId) {

        try {
            // Calling service for deleting a report
            reportService.deleteReport(id);
        } catch (IllegalArgumentException e) {
            // Handling errors
            return new ModelAndView(lang + "/property/property-reports-my")
                    .addObject("deleteFailed", true)
                    .addObject("errorMsg", e.getMessage());
        }
        return new ModelAndView("redirect:/property/reports/my/" + propertyId);
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
     * Method returns currently logged user
     *
     * @return UserEntity
     */
    private UserEntity getLoggedUser() {
        return userService.findUserByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
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

    /**
     * This private method returns a Property by id
     *
     * @param id property id
     * @return Property
     */
    private Property getProperty(Long id) {

        return propertyService.findPropertyById(id);
    }
}
