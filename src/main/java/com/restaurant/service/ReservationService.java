package com.restaurant.service;

import com.restaurant.model.Reservation;
import com.restaurant.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@Service
public class ReservationService {

    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private EmailService emailService;

    public Reservation create(Reservation reservation) {
        Reservation saved = reservationRepository.save(reservation);
        logger.info("Reservation created with ID: {} for email: {}", saved.getId(), saved.getEmail());

        try {
            emailService.sendReservationConfirmation(saved);
            emailService.sendOwnerNotification(saved);
            logger.info("Email sending initiated for reservation ID: {}", saved.getId());
        } catch (Exception e) {
            logger.error("Error initiating email send for reservation {}: {}", saved.getId(), e.getMessage(), e);
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
