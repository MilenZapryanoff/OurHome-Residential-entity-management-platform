package com.example.OurHome.service.email;

import com.example.OurHome.model.dto.BindingModels.ReportBug.ReportBugBindingModel;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender emailSender;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendContactFormEmail(String name, String email, String subject, String message) {
        String mailSubject = "New Contact Form Submission";
        String emailContent = "<h3>New Contact Form Submission</h3>" +
                "<p><strong>Name:</strong> " + name + "</p>" +
                "<p><strong>Email:</strong> " + email + "</p>" +
                "<p><strong>Subject:</strong> " + subject + "</p>" +
                "<p><strong>Message:</strong> " + message + "</p>";

        sendHtmlEmail("office.ourhome@gmail.com", mailSubject, emailContent);
    }

    public void sendResetPasswordEmail(String email, String validationCode) {
        String mailSubject = "OurHome - Password reset request";
        String emailContent = "<h3>You have submitted a password restore request!</h3>" +
                "<p>Your verification code is: <strong>" + validationCode + "</strong></p>" +
                "<p>Use this code to set a new password in the platform.</p>";

        sendHtmlEmail(email, mailSubject, emailContent);
    }

    public void reportBug(ReportBugBindingModel reportBugBindingModel) {
        String mailSubject = reportBugBindingModel.getSubject();
        String emailContent = "<h3>Bug Report</h3>" +
                "<p><strong>Name:</strong> " + reportBugBindingModel.getName() + "</p>" +
                "<p><strong>Email:</strong> " + reportBugBindingModel.getEmail() + "</p>" +
                "<p><strong>Message:</strong> " + reportBugBindingModel.getMessage() + "</p>";

        sendHtmlEmail("office.ourhome@gmail.com", mailSubject, emailContent);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true -> HTML формат

            emailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
