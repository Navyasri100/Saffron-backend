package com.restaurant.controller;

import com.restaurant.model.Reservation;
import com.restaurant.repository.ReservationRepository;
import com.restaurant.security.JwtUtil;
import com.restaurant.service.OtpService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerAuthControllerTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private OtpService otpService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private CustomerAuthController controller;

    @Test
    void requestOtpReturnsBadRequestWhenContactMissing() {
        ResponseEntity<?> response = controller.requestOtp(Map.of());

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void requestOtpReturnsNotFoundWhenNoReservationExists() {
        when(reservationRepository.findFirstByEmailIgnoreCase("none@example.com")).thenReturn(Optional.empty());
        when(reservationRepository.findFirstByPhone("none@example.com")).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.requestOtp(Map.of("contact", "none@example.com"));

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void requestOtpSendsOtpWhenReservationFound() {
        Reservation reservation = reservation("Navya", "navya@gmail.com", "9999999999");
        when(reservationRepository.findFirstByEmailIgnoreCase("navya@gmail.com")).thenReturn(Optional.of(reservation));

        ResponseEntity<?> response = controller.requestOtp(Map.of("contact", "navya@gmail.com"));

        assertEquals(200, response.getStatusCode().value());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertTrue(body.get("message").toString().contains("n***a@gmail.com"));
        verify(otpService).generateAndSend("navya@gmail.com", "Navya", "navya@gmail.com");
    }

    @Test
    void verifyOtpReturnsBadRequestWhenInputIsMissing() {
        ResponseEntity<?> response = controller.verifyOtp(Map.of("contact", "x"));

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void verifyOtpReturnsUnauthorizedWhenOtpInvalid() {
        when(otpService.getCustomerName("abc@example.com")).thenReturn("Abc");
        when(otpService.verify("abc@example.com", "111111")).thenReturn(false);

        ResponseEntity<?> response = controller.verifyOtp(Map.of("contact", "abc@example.com", "otp", "111111"));

        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void verifyOtpReturnsTokenAndReservationDetailsWhenValid() {
        Reservation reservation = reservation("Nita", "nita@gmail.com", "8888888888");
        when(otpService.getCustomerName("nita@gmail.com")).thenReturn("Nita");
        when(otpService.verify("nita@gmail.com", "222222")).thenReturn(true);
        when(reservationRepository.findFirstByEmailIgnoreCase("nita@gmail.com")).thenReturn(Optional.of(reservation));
        when(jwtUtil.generateToken("customer_nita@gmail.com")).thenReturn("cust-token");

        Map<String, String> body = new HashMap<>();
        body.put("contact", "nita@gmail.com");
        body.put("otp", "222222");

        ResponseEntity<?> response = controller.verifyOtp(body);

        assertEquals(200, response.getStatusCode().value());
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("cust-token", responseBody.get("token"));
        assertEquals("Nita", responseBody.get("customerName"));
        assertEquals(reservation.getDate().toString(), responseBody.get("reservationDate"));
    }

    private Reservation reservation(String name, String email, String phone) {
        Reservation r = new Reservation();
        r.setName(name);
        r.setEmail(email);
        r.setPhone(phone);
        r.setDate(LocalDate.now().plusDays(2));
        r.setTime("7:00 PM");
        r.setGuests(2);
        return r;
    }
}

