package com.restaurant.service;

import com.restaurant.model.Reservation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    @Value("${brevo.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";
    private static final String FROM_EMAIL = "saffronsoul2024@gmail.com";
    private static final String FROM_NAME = "Saffron & Soul";

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

        send(r.getEmail(), r.getName(), "Reservation Confirmed — Saffron & Soul", body);
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

        send("saffronsoul2024@gmail.com", "Owner", "New Reservation - " + r.getName(), body);
    }

    public void sendOtp(String toEmail, String customerName, String otp) {
        String body =
            "Dear " + customerName + ",\n\n" +
            "Your One-Time Password to access the Saffron & Soul menu is:\n\n" +
            "        " + otp + "\n\n" +
            "This OTP is valid for 10 minutes.\n\n" +
            "Enjoy your dining experience!\n" +
            "— Team Saffron & Soul";
        send(toEmail, customerName, "Saffron & Soul — Your Menu Access OTP", body);
    }

    private void send(String toEmail, String toName, String subject, String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", apiKey);

        Map<String, Object> payload = Map.of(
            "sender", Map.of("name", FROM_NAME, "email", FROM_EMAIL),
            "to", List.of(Map.of("email", toEmail, "name", toName)),
            "subject", subject,
            "textContent", text
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        restTemplate.postForEntity(BREVO_URL, request, String.class);
    }
}
