package com.restaurant.service;

import com.restaurant.model.Reservation;
import com.restaurant.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private ReservationService service;

    @Test
    void createSavesAndSendsEmails() {
        Reservation reservation = sampleReservation();
        when(reservationRepository.save(reservation)).thenReturn(reservation);

        Reservation result = service.create(reservation);

        assertEquals(reservation, result);
        verify(emailService).sendReservationConfirmation(reservation);
        verify(emailService).sendOwnerNotification(reservation);
    }

    @Test
    void createStillReturnsSavedReservationWhenEmailFails() {
        Reservation reservation = sampleReservation();
        when(reservationRepository.save(reservation)).thenReturn(reservation);
        doThrow(new RuntimeException("mail down")).when(emailService).sendReservationConfirmation(reservation);

        Reservation result = service.create(reservation);

        assertEquals(reservation, result);
        verify(emailService).sendReservationConfirmation(reservation);
    }

    @Test
    void getAllReturnsAllReservations() {
        when(reservationRepository.findAll()).thenReturn(List.of(new Reservation(), new Reservation()));

        List<Reservation> result = service.getAll();

        assertEquals(2, result.size());
    }

    @Test
    void updateStatusUpdatesAndSaves() {
        Reservation reservation = sampleReservation();
        reservation.setStatus(Reservation.ReservationStatus.PENDING);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(reservation)).thenReturn(reservation);

        Reservation result = service.updateStatus(1L, Reservation.ReservationStatus.CONFIRMED);

        assertEquals(Reservation.ReservationStatus.CONFIRMED, result.getStatus());
        verify(reservationRepository).save(reservation);
    }

    @Test
    void updateStatusThrowsWhenReservationMissing() {
        when(reservationRepository.findById(50L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.updateStatus(50L, Reservation.ReservationStatus.CANCELLED));

        assertEquals("Reservation not found: 50", ex.getMessage());
    }

    @Test
    void deleteDelegatesToRepository() {
        service.delete(2L);

        verify(reservationRepository).deleteById(2L);
    }

    private Reservation sampleReservation() {
        Reservation reservation = new Reservation();
        reservation.setName("User");
        reservation.setEmail("user@mail.com");
        reservation.setPhone("9999999999");
        reservation.setDate(LocalDate.now().plusDays(1));
        reservation.setTime("7:00 PM");
        reservation.setGuests(2);
        return reservation;
    }
}

