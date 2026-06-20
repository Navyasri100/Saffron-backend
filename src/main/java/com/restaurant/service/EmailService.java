package com.restaurant.service;

import com.restaurant.model.Reservation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class EmailService {

    @Value("${resend.api-key}")
    private String apiKey;

    @Value("${resend.from}")
    private String from;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String RESEND_URL = "https://api.resend.com/emails";

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

        send("saffronsoul2024@gmail.com", "New Reservation - " + r.getName(), body);
    }

    private void send(String to, String subject, String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> payload = Map.of(
            "from", from,
            "to", new String[]{to},
            "subject", subject,
            "text", text
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        restTemplate.postForEntity(RESEND_URL, request, String.class);
    }
}
