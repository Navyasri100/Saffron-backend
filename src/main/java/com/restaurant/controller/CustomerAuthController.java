package com.restaurant.controller;

import com.restaurant.model.Reservation;
import com.restaurant.repository.ReservationRepository;
import com.restaurant.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/customer")
public class CustomerAuthController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerAuthController.class);

    @Autowired private ReservationRepository reservationRepository;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping("/verify-access")
    public ResponseEntity<?> verifyAccess(@RequestBody Map<String, String> body) {
        String contact = body.getOrDefault("contact", "").trim();

        if (contact.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email or phone is required"));
        }

        logger.info("Customer access attempt with: {}", contact);

        // Check if email or phone has a reservation
        Optional<Reservation> reservation = reservationRepository.findFirstByEmailIgnoreCase(contact);
        if (reservation.isEmpty()) {
            reservation = reservationRepository.findFirstByPhone(contact);
        }

        if (reservation.isEmpty()) {
            logger.warn("No reservation found for: {}", contact);
            return ResponseEntity.status(404).body(Map.of(
                "error", "No booking found. Please make a reservation first."
            ));
        }

        Reservation res = reservation.get();
        String token = jwtUtil.generateToken("customer_" + contact);

        logger.info("✅ Access granted for: {}", contact);

        return ResponseEntity.ok(Map.of(
            "token", token,
            "customerName", res.getName(),
            "contact", contact,
            "reservationDate", res.getDate().toString(),
            "reservationTime", res.getTime() != null ? res.getTime() : "",
            "message", "Access granted. Welcome " + res.getName() + "!"
        ));
    }

    // Mask email: navya@gmail.com → n***a@gmail.com
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return "your email";
        String[] parts = email.split("@");
        String local = parts[0];
        if (local.length() <= 2) return email;
        return local.charAt(0) + "***" + local.charAt(local.length() - 1) + "@" + parts[1];
    }
}
