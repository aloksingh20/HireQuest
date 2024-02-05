/**
 * 
 */
package com.alok91340.gethired.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import org.springframework.core.io.ByteArrayResource;
/*
 *
 * @author aloksingh
 *
 */
@Service
public class EmailServiceImpl {
	@Autowired
	private final JavaMailSender mailSender;
	
    

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

  
    public void sendSimpleEmail(String to, String subject, String otp) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // Set recipient, subject, and sender
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom("HireQuest <a91340singh@gmail.com>"); // Update with your actual email address

//        // Download image data from URL
//        String imageUrl = "https://example.com/logo.png"; // Replace with your actual URL
//        URL url = new URL(imageUrl);
//        byte[] imageData = url.openStream().readAllBytes();
//
//        // Embed the image in the email
//        helper.addInline("logo", new ByteArrayResource(imageData));

        // Build email body with OTP
        String bodyTemplate = "<h1>Welcome to HireQuest!</h1>" +
                "<p>This is your chance to connect with amazing opportunities.</p>" +
                "<p>Your OTP:</p>" +
                otp +
                "<p>Start exploring today!</p>";

        // Replace ${otp} with actual value
        String finalBody = String.format(bodyTemplate, otp);

        // Set the email body
        helper.setText(finalBody, true);

        // Send the email
        mailSender.send(message);
    }
}

