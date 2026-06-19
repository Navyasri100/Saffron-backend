package com.restaurant.service;

import com.restaurant.model.Reservation;
import com.restaurant.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private EmailService emailService;

    public Reservation create(Reservation reservation) {
        Reservation saved = reservationRepository.save(reservation);
        try {
            emailService.sendReservationConfirmation(saved);
            emailService.sendOwnerNotification(saved);
        } catch (Exception e) {
            System.err.println("Email sending failed: " + e.getMessage());
        }
        return saved;
    }

    public List<Reservation> getAll() {
        return reservationRepository.findAll();
    }

    public Reservation updateStatus(Long id, Reservation.ReservationStatus status) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found: " + id));
        reservation.setStatus(status);
        return reservationRepository.save(reservation);
    }

    public void delete(Long id) {
        reservationRepository.deleteById(id);
    }
}
