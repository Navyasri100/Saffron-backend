package com.restaurant.repository;

import com.restaurant.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByDateOrderByTimeAsc(LocalDate date);
    long deleteByDateBefore(LocalDate date);
    List<Reservation> findByStatus(Reservation.ReservationStatus status);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByPhone(String phone);

    @Query("SELECT r FROM Reservation r WHERE LOWER(r.email) = LOWER(:email) ORDER BY r.date DESC, r.createdAt DESC LIMIT 1")
    Optional<Reservation> findLatestByEmailIgnoreCase(@Param("email") String email);

    @Query("SELECT r FROM Reservation r WHERE r.phone = :phone ORDER BY r.date DESC, r.createdAt DESC LIMIT 1")
    Optional<Reservation> findLatestByPhone(@Param("phone") String phone);
}
