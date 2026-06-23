package com.restaurant.controller;

import com.restaurant.model.Reservation;
import com.restaurant.repository.ReservationRepository;
import com.restaurant.security.JwtUtil;
import com.restaurant.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    @Mock
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private ReservationController controller;

    @Test
    void availableSlotsExcludesFullyBookedSlots() {
        LocalDate date = LocalDate.now().plusDays(5);
        Reservation full = reservation("a@mail.com", "999", date, "7:00 PM", 100, Reservation.ReservationStatus.CONFIRMED);
        Reservation cancelled = reservation("b@mail.com", "888", date, "8:00 PM", 100, Reservation.ReservationStatus.CANCELLED);
        when(reservationRepository.findByDateOrderByTimeAsc(date)).thenReturn(List.of(full, cancelled));

        ResponseEntity<List<String>> response = controller.availableSlots(date.toString());

        assertEquals(200, response.getStatusCode().value());
        assertTrue(!response.getBody().contains("7:00 PM"));
        assertTrue(response.getBody().contains("8:00 PM"));
    }

    @Test
    void createDelegatesToService() {
        Reservation reservation = reservation("n@mail.com", "111", LocalDate.now().plusDays(1), "6:30 PM", 2, Reservation.ReservationStatus.CONFIRMED);
        when(reservationService.create(reservation)).thenReturn(reservation);

        ResponseEntity<Reservation> response = controller.create(reservation);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(reservation, response.getBody());
    }

    @Test
    void getAllDelegatesToService() {
        when(reservationService.getAll()).thenReturn(List.of(new Reservation()));

        List<Reservation> response = controller.getAll();

        assertEquals(1, response.size());
    }

    @Test
    void updateStatusDelegatesToService() {
        Reservation updated = new Reservation();
        updated.setStatus(Reservation.ReservationStatus.CANCELLED);
        when(reservationService.updateStatus(1L, Reservation.ReservationStatus.CANCELLED)).thenReturn(updated);

        ResponseEntity<Reservation> response = controller.updateStatus(1L, Reservation.ReservationStatus.CANCELLED);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(Reservation.ReservationStatus.CANCELLED, response.getBody().getStatus());
    }

    @Test
    void deleteDelegatesToService() {
        ResponseEntity<Void> response = controller.delete(5L);

        verify(reservationService).delete(5L);
        assertEquals(204, response.getStatusCode().value());
    }

    @Test
    void getMyReservationReturnsUnauthorizedWhenTokenInvalid() {
        when(jwtUtil.validateToken("token")).thenReturn(false);

        ResponseEntity<?> response = controller.getMyReservation("Bearer token");

        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void getMyReservationReturnsNotFoundWhenNoReservationForContact() {
        mockCustomerToken("abc@mail.com");
        when(reservationRepository.findFirstByEmailIgnoreCase("abc@mail.com")).thenReturn(Optional.empty());
        when(reservationRepository.findFirstByPhone("abc@mail.com")).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.getMyReservation("Bearer token");

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void getMyReservationReturnsReservationWhenFound() {
        Reservation reservation = reservation("abc@mail.com", "111", LocalDate.now().plusDays(3), "8:00 PM", 3, Reservation.ReservationStatus.CONFIRMED);
        mockCustomerToken("abc@mail.com");
        when(reservationRepository.findFirstByEmailIgnoreCase("abc@mail.com")).thenReturn(Optional.of(reservation));

        ResponseEntity<?> response = controller.getMyReservation("Bearer token");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(reservation, response.getBody());
    }

    @Test
    void updateMyReservationReturnsUnauthorizedWhenTokenInvalid() {
        when(jwtUtil.validateToken("token")).thenReturn(false);

        ResponseEntity<?> response = controller.updateMyReservation(1L, Map.of(), "Bearer token");

        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void updateMyReservationReturnsNotFoundWhenReservationMissing() {
        mockCustomerToken("abc@mail.com");
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.updateMyReservation(1L, Map.of(), "Bearer token");

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void updateMyReservationReturnsForbiddenWhenContactDoesNotMatch() {
        Reservation reservation = reservation("other@mail.com", "222", LocalDate.now().plusDays(2), "7:00 PM", 2, Reservation.ReservationStatus.CONFIRMED);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        mockCustomerToken("abc@mail.com");

        ResponseEntity<?> response = controller.updateMyReservation(1L, Map.of(), "Bearer token");

        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    void updateMyReservationRejectsPastReservation() {
        Reservation reservation = reservation("abc@mail.com", "222", LocalDate.now().minusDays(1), "7:00 PM", 2, Reservation.ReservationStatus.CONFIRMED);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        mockCustomerToken("abc@mail.com");

        ResponseEntity<?> response = controller.updateMyReservation(1L, Map.of("notes", "x"), "Bearer token");

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void updateMyReservationRejectsWhenNewSlotIsFullyBooked() {
        LocalDate date = LocalDate.now().plusDays(4);
        Reservation current = reservation("abc@mail.com", "222", date, "7:00 PM", 2, Reservation.ReservationStatus.CONFIRMED);
        current.setId(10L);

        Reservation other = reservation("x@mail.com", "333", date, "7:00 PM", 100, Reservation.ReservationStatus.CONFIRMED);
        other.setId(11L);

        when(reservationRepository.findById(10L)).thenReturn(Optional.of(current));
        when(reservationRepository.findByDateOrderByTimeAsc(date)).thenReturn(List.of(other));
        mockCustomerToken("abc@mail.com");

        ResponseEntity<?> response = controller.updateMyReservation(10L, Map.of("time", "7:00 PM", "guests", 1), "Bearer token");

        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void updateMyReservationUpdatesAndSavesWhenValid() {
        LocalDate date = LocalDate.now().plusDays(5);
        Reservation current = reservation("abc@mail.com", "222", date, "7:00 PM", 2, Reservation.ReservationStatus.CONFIRMED);
        current.setId(20L);
        when(reservationRepository.findById(20L)).thenReturn(Optional.of(current));
        when(reservationRepository.findByDateOrderByTimeAsc(date)).thenReturn(List.of());
        when(reservationRepository.save(current)).thenReturn(current);
        mockCustomerToken("abc@mail.com");

        Map<String, Object> body = new HashMap<>();
        body.put("time", "8:00 PM");
        body.put("guests", 4);
        body.put("notes", "Birthday");

        ResponseEntity<?> response = controller.updateMyReservation(20L, body, "Bearer token");

        assertEquals(200, response.getStatusCode().value());
        Reservation saved = (Reservation) response.getBody();
        assertEquals("8:00 PM", saved.getTime());
        assertEquals(4, saved.getGuests());
        assertEquals("Birthday", saved.getNotes());
    }

    @Test
    void cancelMyReservationReturnsUnauthorizedWhenTokenInvalid() {
        when(jwtUtil.validateToken("token")).thenReturn(false);

        ResponseEntity<?> response = controller.cancelMyReservation(1L, "Bearer token");

        assertEquals(401, response.getStatusCode().value());
    }

    @Test
    void cancelMyReservationReturnsNotFoundWhenReservationMissing() {
        mockCustomerToken("abc@mail.com");
        when(reservationRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.cancelMyReservation(1L, "Bearer token");

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void cancelMyReservationReturnsForbiddenForDifferentContact() {
        Reservation reservation = reservation("other@mail.com", "999", LocalDate.now().plusDays(1), "7:30 PM", 2, Reservation.ReservationStatus.CONFIRMED);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        mockCustomerToken("abc@mail.com");

        ResponseEntity<?> response = controller.cancelMyReservation(1L, "Bearer token");

        assertEquals(403, response.getStatusCode().value());
    }

    @Test
    void cancelMyReservationMarksReservationCancelled() {
        Reservation reservation = reservation("abc@mail.com", "999", LocalDate.now().plusDays(1), "7:30 PM", 2, Reservation.ReservationStatus.CONFIRMED);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        mockCustomerToken("abc@mail.com");

        ResponseEntity<?> response = controller.cancelMyReservation(1L, "Bearer token");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(Reservation.ReservationStatus.CANCELLED, reservation.getStatus());
        verify(reservationRepository).save(reservation);
    }

    private void mockCustomerToken(String contact) {
        when(jwtUtil.validateToken("token")).thenReturn(true);
        when(jwtUtil.extractUsername("token")).thenReturn("customer_" + contact);
    }

    private Reservation reservation(String email, String phone, LocalDate date, String time, int guests,
                                    Reservation.ReservationStatus status) {
        Reservation r = new Reservation();
        r.setEmail(email);
        r.setPhone(phone);
        r.setDate(date);
        r.setTime(time);
        r.setGuests(guests);
        r.setStatus(status);
        return r;
    }
}

