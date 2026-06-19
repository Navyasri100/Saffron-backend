package com.restaurant.controller;

import com.restaurant.model.Reservation;
import com.restaurant.repository.ReservationRepository;
import com.restaurant.security.JwtUtil;
import com.restaurant.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/customer")
public class CustomerAuthController {

    @Autowired private ReservationRepository reservationRepository;
    @Autowired private OtpService otpService;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping("/request-otp")
    public ResponseEntity<?> requestOtp(@RequestBody Map<String, String> body) {
        String contact = body.getOrDefault("contact", "").trim();
        if (contact.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Contact is required"));
        }

        // Past reservations are deleted on startup, so any match here is current/upcoming
        Optional<Reservation> reservation = reservationRepository.findFirstByEmailIgnoreCase(contact);
        if (reservation.isEmpty()) {
            reservation = reservationRepository.findFirstByPhone(contact);
        }
        if (reservation.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of(
                "error", "No booking found for this email or phone number. Please make a reservation first."
            ));
        }

        Reservation res = reservation.get();

        String customerName = res.getName();
        String customerEmail = res.getEmail();

        otpService.generateAndSend(contact, customerName, customerEmail);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "OTP sent to your booking email: " + maskEmail(customerEmail),
            "customerName", customerName
        ));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> body) {
        String contact = body.getOrDefault("contact", "").trim();
        String otp = body.getOrDefault("otp", "").trim();

        if (contact.isEmpty() || otp.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Contact and OTP are required"));
        }

        String customerName = otpService.getCustomerName(contact);
        boolean valid = otpService.verify(contact, otp);

        if (!valid) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid or expired OTP. Please try again."));
        }

        Optional<Reservation> reservation = reservationRepository.findFirstByEmailIgnoreCase(contact);
        if (reservation.isEmpty()) reservation = reservationRepository.findFirstByPhone(contact);

        String token = jwtUtil.generateToken("customer_" + contact);

        String reservationDate = reservation.map(r -> r.getDate().toString()).orElse("");
        String reservationTime = reservation.map(r -> r.getTime() != null ? r.getTime().toString() : "").orElse("");

        return ResponseEntity.ok(Map.of(
            "token", token,
            "customerName", customerName != null ? customerName : "",
            "contact", contact,
            "reservationDate", reservationDate,
            "reservationTime", reservationTime
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
