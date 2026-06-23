package com.restaurant.service;

import com.restaurant.model.Reservation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final String FROM_NAME = "Saffron & Soul";

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

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
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_NAME + " <" + fromEmail + ">");
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Email send failed to " + toEmail + ": " + e.getMessage());
        }
    }
}
