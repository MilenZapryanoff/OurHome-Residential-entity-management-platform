package com.example.OurHome.service.email;

import com.example.OurHome.model.Entity.Event;
import com.example.OurHome.model.Entity.Property;
import com.example.OurHome.model.Entity.ResidentialEntity;
import com.example.OurHome.model.Entity.UserEntity;
import com.example.OurHome.model.dto.BindingModels.ReportBug.ReportBugBindingModel;
import com.example.OurHome.service.LogService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmailService {

    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;
    private final LogService logService;

    public EmailService(JavaMailSender emailSender, TemplateEngine templateEngine, LogService logService) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
        this.logService = logService;
    }


    /**
     * Contact form mail
     *
     * @param name    is the name of the sender in contact form
     * @param email   is the contact user email
     * @param subject is the topic of the message
     * @param message is the content of the message
     */
    public void sendContactFormEmail(String name, String email, String subject, String message) {
        String mailSubject = "New Contact Form Submission";
        String emailContent = "<h3>New Contact Form Submission</h3>" +
                "<p><strong>Name:</strong> " + name + "</p>" +
                "<p><strong>Email:</strong> " + email + "</p>" +
                "<p><strong>Subject:</strong> " + subject + "</p>" +
                "<p><strong>Message:</strong> " + message + "</p>";

        sendHtmlEmail("office.ourhome@gmail.com", mailSubject, emailContent);
    }


    /**
     * Report a bug email
     *
     * @param reportBugBindingModel contains information about the Name of the user, email address and content of the message
     */
    public void reportBug(ReportBugBindingModel reportBugBindingModel) {
        String mailSubject = reportBugBindingModel.getSubject();
        String emailContent = "<h3>Bug Report</h3>" +
                "<p><strong>Name:</strong> " + reportBugBindingModel.getName() + "</p>" +
                "<p><strong>Email:</strong> " + reportBugBindingModel.getEmail() + "</p>" +
                "<p><strong>Message:</strong> " + reportBugBindingModel.getMessage() + "</p>";

        sendHtmlEmail("office.ourhome@gmail.com", mailSubject, emailContent);
    }

    /**
     * Send reset password email method
     *
     * @param user      is the user that requested triggered forgot password functionality
     * @param resetCode is the current resetCode for restoring password
     */
    @Async
    public void sendResetPasswordEmail(UserEntity user, String resetCode) {

        try {
            String mailSubject = "Възстановяване на забравена парола";

            Context context = new Context();
            context.setVariable("recipientName", user.getFirstName() + " " + user.getLastName());
            context.setVariable("resetCode", resetCode);
            context.setVariable("resetPasswordLink", "https://ourhome.bg/forgot-password"); // Линк към съобщенията

            // Генерираме HTML съдържанието
            String emailContent = templateEngine.process("emails/password-reset-email", context);

            sendHtmlEmail(user.getEmail(), mailSubject, emailContent);

            logService.info("✅[sendResetPasswordEmail ->] Reset password email successfully sent to user with id: {}", user.getId());
        } catch (Exception e) {
            logService.error("❌[sendResetPasswordEmail ->] Failed to send reset password email to user with id: {}! {}", user.getId(), e);
        }
    }


    /**
     * Method for sending email notification for a new message.
     *
     * @param recipient is receiver of the mail
     * @param sender    is the sender of the message
     * @param message   is the content of the message
     */
    @Async
    public void newMessageEmailNotification(UserEntity recipient, UserEntity sender, String message) {

        try {
            if (recipient == null || recipient.getEmail() == null) {
                logService.warn("⚠️[-> newMessageEmailNotification] Property owner or email is null. Skipping email notification.");
                return;
            }

            String mailSubject = "Известие за ново съобщение!";

            // Създаваме контекст с Thymeleaf променливи
            Context context = new Context();
            context.setVariable("recipientName", recipient.getFirstName() + " " + recipient.getLastName());
            context.setVariable("senderName", sender.getFirstName() + " " + sender.getLastName());
            context.setVariable("messageContent", message);
            context.setVariable("messagesLink", "https://ourhome.bg/messages"); // Линк към съобщенията

            // Генерираме HTML съдържанието
            String emailContent = templateEngine.process("emails/new-message-email", context);

            sendHtmlEmail(recipient.getEmail(), mailSubject, emailContent);

            logService.info("✅[-> newMessageEmailNotification] New EMAIL successfully sent to user with id: {}", recipient.getId());
        } catch (Exception e) {
            assert recipient != null;
            logService.error("❌[-> newMessageEmailNotification] Failed to send new EMAIL to user with id: {}! {}", recipient.getId(), e);
        }
    }

    /**
     * Method for sending email notification for a new monthly fee.
     *
     * @param bccEmails List of property owners emails.
     */
    @Async
    public void newFeeEmailNotification(List<String> bccEmails, ResidentialEntity residentialEntity) {

        try {
            String mailSubject = "Известие за нова месечна такса!";

            // Създаваме контекст с Thymeleaf променливи
            Context context = new Context();
            context.setVariable("residentialEntity", residentialEntity);
            context.setVariable("feesLink", "https://ourhome.bg/property");

            // Генерираме HTML съдържанието
            String emailContent = templateEngine.process("emails/new-fee-email", context);

            sendHtmlEmailWithBcc(bccEmails, mailSubject, emailContent);

            logService.info("✅[-> newFeeEmailNotification] New monthly fee EMAIL successfully sent to property owners in condominium id: {}", residentialEntity.getId());
        } catch (Exception e) {
            logService.error("❌[-> newFeeEmailNotification] Failed to send new monthly fee EMAIL for property owners in condominium id:{}. {}", residentialEntity.getId(), e);
        }
    }


    /**
     * Method for sending email notification for a new Event.
     *
     * @param event             is the event that triggers email send
     * @param residentialEntity is the residential entity where the event occures
     */
    @Async
    public void newEventEmailToAllVerifiedPropertyOwners(Event event, ResidentialEntity residentialEntity) {
        try {
            List<String> bccEmails = new ArrayList<>();
            List<Property> properties = residentialEntity.getProperties();

            for (Property property : properties) {
                if (property.getOwner() != null && property.isObtained() && property.getOwner().isEventEmail()) {
                    //collecting all property owners emails
                    bccEmails.add(property.getOwner().getEmail());
                }
            }

            if (bccEmails.isEmpty()) {
                logService.warn("⚠️ [-> newEventEmailToAllVerifiedPropertyOwners] No verified property owners found for condominium {}", residentialEntity.getId());
                return;
            }

            String mailSubject = "Известие за ново събитие в календара на Етажната собственост!";

            // Създаваме контекст с Thymeleaf променливи
            Context context = new Context();
            context.setVariable("residentialEntity", residentialEntity);
            context.setVariable("event", event);
            context.setVariable("feesLink", "https://ourhome.bg/property/");

            // Генерираме HTML съдържанието
            String emailContent = templateEngine.process("emails/new-event-email", context);

            sendHtmlEmailWithBcc(bccEmails, mailSubject, emailContent);

            logService.info("✅[-> newEventEmailToAllVerifiedPropertyOwners] New EVENT email successfully sent to all property owners in condominium {}", residentialEntity.getId());
        } catch (Exception e) {
            logService.error("❌[-> newEventEmailToAllVerifiedPropertyOwners] Failed to send new EVENT email to all property owners in condominium {}", residentialEntity.getId(), e);
        }
    }

    /**
     * Private method for sending email
     *
     * @param to          is the received of the mail
     * @param subject     is the subject of the email
     * @param htmlContent is the message
     */
    private void sendHtmlEmail(String to, String subject, String htmlContent) {

        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true -> HTML формат

            emailSender.send(message);
        } catch (MessagingException e) {
            logService.error("❌[E-MAIL client] Failed to send html email: ", e);
        }
    }


    /**
     * Sends an HTML email with BCC recipients.
     *
     * @param bccEmails   list of email addresses for BCC
     * @param subject     email subject
     * @param htmlContent email body
     */
    private void sendHtmlEmailWithBcc(List<String> bccEmails, String subject, String htmlContent) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true -> HTML формат

            // Добавяне на всички имейли в BCC
            helper.setBcc(bccEmails.toArray(new String[0]));

            emailSender.send(message);
        } catch (MessagingException e) {
            logService.error("❌[E-MAIL client] Failed to send HTML email with BCC: ", e);
        }
    }
}
