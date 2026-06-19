package com.restaurant.repository;

import com.restaurant.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByDateOrderByTimeAsc(LocalDate date);
    void deleteByDateBefore(LocalDate date);
    List<Reservation> findByStatus(Reservation.ReservationStatus status);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByPhone(String phone);
    java.util.Optional<Reservation> findFirstByEmailIgnoreCase(String email);
    java.util.Optional<Reservation> findFirstByPhone(String phone);
}
