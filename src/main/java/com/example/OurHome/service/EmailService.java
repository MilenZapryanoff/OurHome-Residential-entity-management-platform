package com.example.OurHome.service;

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
}
