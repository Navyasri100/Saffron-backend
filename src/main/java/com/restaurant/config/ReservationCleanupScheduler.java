package com.restaurant.config;

import com.restaurant.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class ReservationCleanupScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ReservationCleanupScheduler.class);

    @Autowired private ReservationRepository reservationRepository;

    // Runs every day at midnight (00:00)
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deletePastReservations() {
        LocalDate today = LocalDate.now();
        logger.info("╔════════════════════════════════════════════╗");
        logger.info("║ 🧹 SCHEDULED RESERVATION CLEANUP RUNNING 🧹 ║");
        logger.info("╚════════════════════════════════════════════╝");
        logger.info("Today's date: {}", today);
        logger.info("Deleting all reservations with date BEFORE: {}", today);

        try {
            long deletedCount = reservationRepository.deleteByDateBefore(today);
            logger.info("✅ Cleanup completed. Deleted {} past reservations", deletedCount);
        } catch (Exception e) {
            logger.error("❌ Cleanup failed: {}", e.getMessage(), e);
        }

        logger.info("────────────────────────────────────────────");
        logger.info("Reservations KEPT: All reservations for {} and beyond", today);
        logger.info("────────────────────────────────────────────\n");
    }
}
