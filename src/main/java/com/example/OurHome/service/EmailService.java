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

    public void sendContactFormEmail(String name, String email, String message) {
        // Prepare email content
        String subject = "New Contact Form Submission";
        String emailContent = "Name: " + name + "\nEmail: " + email + "\nMessage: " + message;

        // Set up email message
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo("office.ourhome@gmail.com"); // Set your recipient email address here
        mailMessage.setSubject(subject);
        mailMessage.setText(emailContent);

        // Send the email
        emailSender.send(mailMessage);
    }

}
