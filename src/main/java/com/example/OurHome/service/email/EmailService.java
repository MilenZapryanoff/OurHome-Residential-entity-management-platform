package com.example.OurHome.service.email;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender emailSender;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendContactFormEmail(String name, String email, String subject, String message) {
        String mailSubject = "New Contact Form Submission";
        String emailContent = "Name: " + name + "\nEmail: " + email + "\nMessage: " + subject + "\nMessage: " + message;

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo("office.ourhome@gmail.com");
        mailMessage.setSubject(mailSubject);
        mailMessage.setText(emailContent);

        emailSender.send(mailMessage);
    }

    public void sendResetPasswordEmail(String email, String validationCode) {
        String mailSubject = "OurHome - Password reset request";
        String emailContent = "You have submitted a password restore request!\n\n" +
                "Your verification code is: " + validationCode +
                "\n\nUse this code to set a new password in the platform.";

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email);
        mailMessage.setSubject(mailSubject);
        mailMessage.setText(emailContent);

        emailSender.send(mailMessage);
    }

}
