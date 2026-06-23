package com.restaurant.controller;

import com.restaurant.model.Reservation;
import com.restaurant.repository.ReservationRepository;
import com.restaurant.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerAuthControllerTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private CustomerAuthController controller;

    @Test
    void verifyAccessReturnsBadRequestWhenContactMissing() {
        ResponseEntity<?> response = controller.verifyAccess(Map.of());

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void verifyAccessReturnsNotFoundWhenNoReservationExists() {
        when(reservationRepository.findFirstByEmailIgnoreCase("none@example.com")).thenReturn(Optional.empty());
        when(reservationRepository.findFirstByPhone("none@example.com")).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.verifyAccess(Map.of("contact", "none@example.com"));

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void verifyAccessReturnsTokenWhenReservationFound() {
        Reservation reservation = reservation("Navya", "navya@gmail.com", "9999999999");
        when(reservationRepository.findFirstByEmailIgnoreCase("navya@gmail.com")).thenReturn(Optional.of(reservation));
        when(jwtUtil.generateToken("customer_navya@gmail.com")).thenReturn("test-token");

        ResponseEntity<?> response = controller.verifyAccess(Map.of("contact", "navya@gmail.com"));

        assertEquals(200, response.getStatusCode().value());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("test-token", body.get("token"));
        assertEquals("Navya", body.get("customerName"));
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

