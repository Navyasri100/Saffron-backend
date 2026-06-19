package com.restaurant.config;

import com.restaurant.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import jakarta.transaction.Transactional;
import java.time.LocalDate;

@Component
public class ReservationCleanupScheduler {

    @Autowired private ReservationRepository reservationRepository;

    // Runs every day at midnight
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deletePastReservations() {
        reservationRepository.deleteByDateBefore(LocalDate.now());
        System.out.println("Scheduled cleanup: past reservations deleted.");
    }
}
