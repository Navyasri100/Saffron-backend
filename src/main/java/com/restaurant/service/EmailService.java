package com.restaurant.service;

import com.restaurant.model.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendReservationConfirmation(Reservation r) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(r.getEmail());
        msg.setSubject("Reservation Confirmed — Saffron & Soul");
        msg.setText(
            "Dear " + r.getName() + ",\n\n" +
            "Your reservation at Saffron & Soul has been confirmed!\n\n" +
            "Date: " + r.getDate() + "\n" +
            "Time: " + r.getTime() + "\n" +
            "Guests: " + r.getGuests() + "\n\n" +
            "We look forward to welcoming you.\n\n" +
            "— Team Saffron & Soul\n" +
            "saffronsoul2024@gmail.com"
        );
        mailSender.send(msg);
    }

    public void sendOwnerNotification(Reservation r) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("saffronsoul2024@gmail.com");
        msg.setSubject("New Reservation - " + r.getName());
        msg.setText(
            "New reservation received:\n\n" +
            "Name: " + r.getName() + "\n" +
            "Email: " + r.getEmail() + "\n" +
            "Phone: " + r.getPhone() + "\n" +
            "Date: " + r.getDate() + "\n" +
            "Time: " + r.getTime() + "\n" +
            "Guests: " + r.getGuests() + "\n" +
            "Notes: " + r.getNotes()
        );
        mailSender.send(msg);
    }
}
