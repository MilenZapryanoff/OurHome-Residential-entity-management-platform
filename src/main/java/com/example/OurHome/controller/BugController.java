package com.example.OurHome.controller;

import com.example.OurHome.model.dto.BindingModels.ReportBug.ReportBugBindingModel;
import com.example.OurHome.service.email.EmailService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class BugController {

    private final EmailService emailService;

    public BugController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("/report/bug")
    public ModelAndView reportBug(@ModelAttribute("reportBugBindingModel") ReportBugBindingModel reportBugBindingModel,
                                  @CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/report-bug") : new ModelAndView("en/report-bug");

        return view;
    }

    @GetMapping("/report/bug/success")
    public ModelAndView reportBugSuccess(@CookieValue(value = "lang", defaultValue = "bg") String lang) {

        ModelAndView view = resolveView(lang) ?
                new ModelAndView("bg/report-bug-success") : new ModelAndView("en/report-bug-success");

        return view;
    }

    @PostMapping("/report/bug")
    public ModelAndView reportBug(
            @ModelAttribute("reportBugBindingModel") @Valid ReportBugBindingModel reportBugBindingModel,
            BindingResult bindingResult,
            @CookieValue(value = "lang", defaultValue = "bg") String lang) {


        ModelAndView result = new ModelAndView(lang + "/report-bug")
                .addObject("reportBugBindingModel", reportBugBindingModel);

        if (bindingResult.hasErrors()) {
            return result;
        }

        try {
            // mail service call
            emailService.reportBug(reportBugBindingModel);
        } catch (IllegalArgumentException e) {
            // Обработка на грешка
            return result
                    .addObject("bugSubmitFailed", true)
                    .addObject("errorMsg", e.getMessage());
        }
        return new ModelAndView("redirect:/report/bug/success");
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
