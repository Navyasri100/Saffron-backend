package com.restaurant.controller;

import com.restaurant.model.Reservation;
import com.restaurant.repository.ReservationRepository;
import com.restaurant.security.JwtUtil;
import com.restaurant.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ReservationController {

    @Autowired private ReservationService reservationService;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private JwtUtil jwtUtil;

    private static final int MAX_OCCUPANCY = 100;

    private static final List<String> ALL_SLOTS = Arrays.asList(
        "12:30 PM","1:00 PM","1:30 PM","2:00 PM","2:30 PM","3:00 PM","3:30 PM",
        "6:30 PM","7:00 PM","7:30 PM","8:00 PM","8:30 PM","9:00 PM","9:30 PM","10:00 PM","10:30 PM"
    );

    // Public: get available slots for a date (filters out full slots)
    @GetMapping("/reservations/available-slots")
    public ResponseEntity<List<String>> availableSlots(@RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);

        // Sum guests already booked per slot on this date
        List<Reservation> existing = reservationRepository.findByDateOrderByTimeAsc(localDate);
        Map<String, Integer> slotOccupancy = existing.stream()
            .filter(r -> r.getStatus() != Reservation.ReservationStatus.CANCELLED)
            .collect(Collectors.groupingBy(
                Reservation::getTime,
                Collectors.summingInt(r -> r.getGuests() != null ? r.getGuests() : 0)
            ));

        List<String> available = ALL_SLOTS.stream()
            .filter(slot -> slotOccupancy.getOrDefault(slot, 0) < MAX_OCCUPANCY)
            .collect(Collectors.toList());

        return ResponseEntity.ok(available);
    }

    // Public: create reservation
    @PostMapping("/reservations")
    public ResponseEntity<Reservation> create(@Valid @RequestBody Reservation reservation) {
        return ResponseEntity.ok(reservationService.create(reservation));
    }

    // Admin: get all reservations
    @GetMapping("/admin/reservations")
    public List<Reservation> getAll() {
        return reservationService.getAll();
    }

    // Admin: update reservation status
    @PutMapping("/admin/reservations/{id}/status")
    public ResponseEntity<Reservation> updateStatus(
            @PathVariable Long id,
            @RequestParam Reservation.ReservationStatus status) {
        return ResponseEntity.ok(reservationService.updateStatus(id, status));
    }

    // Admin: delete reservation
    @DeleteMapping("/admin/reservations/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ── Customer self-service endpoints ──────────────────────────────────────

    /** Extract and validate customer contact from Bearer token. Returns null if invalid. */
    private String extractCustomerContact(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) return null;
        String username = jwtUtil.extractUsername(token);
        if (username == null || !username.startsWith("customer_")) return null;
        return username.substring("customer_".length());
    }

    private Optional<Reservation> findByContact(String contact) {
        Optional<Reservation> r = reservationRepository.findFirstByEmailIgnoreCase(contact);
        return r.isPresent() ? r : reservationRepository.findFirstByPhone(contact);
    }

    // GET /api/reservations/mine
    @GetMapping("/reservations/mine")
    public ResponseEntity<?> getMyReservation(@RequestHeader("Authorization") String authHeader) {
        String contact = extractCustomerContact(authHeader);
        if (contact == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        return findByContact(contact)
            .<ResponseEntity<?>>map(ResponseEntity::ok)
            .orElse(ResponseEntity.status(404).body(Map.of("error", "No reservation found")));
    }

    // PUT /api/reservations/{id}
    @PutMapping("/reservations/{id}")
    public ResponseEntity<?> updateMyReservation(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body,
            @RequestHeader("Authorization") String authHeader) {
        String contact = extractCustomerContact(authHeader);
        if (contact == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        Optional<Reservation> opt = reservationRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "Reservation not found"));

        Reservation res = opt.get();
        if (!contact.equalsIgnoreCase(res.getEmail()) && !contact.equals(res.getPhone()))
            return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));

        if (LocalDate.now().isAfter(res.getDate()))
            return ResponseEntity.badRequest().body(Map.of("error", "Cannot edit a past reservation"));

        // Parse new values
        String newDateStr = (String) body.get("date");
        String newTime   = (String) body.get("time");
        Integer newGuests = body.get("guests") != null
            ? Integer.parseInt(body.get("guests").toString()) : null;
        String newNotes  = (String) body.get("notes");

        LocalDate targetDate = newDateStr != null ? LocalDate.parse(newDateStr) : res.getDate();
        if (newDateStr != null && !targetDate.isAfter(LocalDate.now().minusDays(1)))
            return ResponseEntity.badRequest().body(Map.of("error", "Date must be today or in the future"));

        String targetTime   = newTime   != null ? newTime   : res.getTime();
        int    targetGuests = newGuests != null ? newGuests : (res.getGuests() != null ? res.getGuests() : 1);

        // Occupancy check for the new slot (exclude current reservation)
        List<Reservation> dayReservations = reservationRepository.findByDateOrderByTimeAsc(targetDate);
        int occupied = dayReservations.stream()
            .filter(r -> r.getStatus() != Reservation.ReservationStatus.CANCELLED)
            .filter(r -> r.getTime().equals(targetTime))
            .filter(r -> !r.getId().equals(id))
            .mapToInt(r -> r.getGuests() != null ? r.getGuests() : 0)
            .sum();
        if (occupied + targetGuests > MAX_OCCUPANCY)
            return ResponseEntity.badRequest().body(Map.of("error", "Selected time slot is fully booked"));

        res.setDate(targetDate);
        res.setTime(targetTime);
        res.setGuests(targetGuests);
        if (newNotes != null) res.setNotes(newNotes);

        return ResponseEntity.ok(reservationRepository.save(res));
    }

    // DELETE /api/reservations/{id} — marks as CANCELLED
    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<?> cancelMyReservation(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        String contact = extractCustomerContact(authHeader);
        if (contact == null) return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));

        Optional<Reservation> opt = reservationRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.status(404).body(Map.of("error", "Reservation not found"));

        Reservation res = opt.get();
        if (!contact.equalsIgnoreCase(res.getEmail()) && !contact.equals(res.getPhone()))
            return ResponseEntity.status(403).body(Map.of("error", "Forbidden"));

        res.setStatus(Reservation.ReservationStatus.CANCELLED);
        reservationRepository.save(res);
        return ResponseEntity.ok(Map.of("message", "Reservation cancelled successfully"));
    }
}
