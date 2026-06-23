package com.restaurant.service;

import com.restaurant.model.Reservation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final String FROM_NAME = "Saffron & Soul";

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendReservationConfirmation(Reservation r) {
        String body =
            "Dear " + r.getName() + ",\n\n" +
            "Your reservation at Saffron & Soul has been confirmed!\n\n" +
            "Date: " + r.getDate() + "\n" +
            "Time: " + r.getTime() + "\n" +
            "Guests: " + r.getGuests() + "\n\n" +
            "We look forward to welcoming you.\n\n" +
            "— Team Saffron & Soul\n" +
            "saffronsoul2024@gmail.com";

        send(r.getEmail(), "Reservation Confirmed — Saffron & Soul", body);
    }

    @Async
    public void sendOwnerNotification(Reservation r) {
        String body =
            "New reservation received:\n\n" +
            "Name: " + r.getName() + "\n" +
            "Email: " + r.getEmail() + "\n" +
            "Phone: " + r.getPhone() + "\n" +
            "Date: " + r.getDate() + "\n" +
            "Time: " + r.getTime() + "\n" +
            "Guests: " + r.getGuests() + "\n" +
            "Notes: " + r.getNotes();

        send(fromEmail, "New Reservation - " + r.getName(), body);
    }

    @Async
    public void sendOtp(String toEmail, String customerName, String otp) {
        String body =
            "Dear " + customerName + ",\n\n" +
            "Your One-Time Password to access the Saffron & Soul menu is:\n\n" +
            "        " + otp + "\n\n" +
            "This OTP is valid for 10 minutes.\n\n" +
            "Enjoy your dining experience!\n" +
            "— Team Saffron & Soul";

        send(toEmail, "Saffron & Soul — Your Menu Access OTP", body);
    }

    private void send(String toEmail, String subject, String text) {
        try {
            if (fromEmail == null || fromEmail.isEmpty()) {
                logger.error("Email configuration missing: spring.mail.username not set");
                return;
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_NAME + " <" + fromEmail + ">");
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(text);

            logger.info("Sending email to: {} with subject: {}", toEmail, subject);
            mailSender.send(message);
            logger.info("Email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", toEmail, e.getMessage(), e);
        }
    }
}
