package com.restaurant.service;

import com.restaurant.model.Reservation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService service;

    @Test
    void sendReservationConfirmationBuildsExpectedMessage() {
        Reservation reservation = reservation();

        service.sendReservationConfirmation(reservation);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage msg = captor.getValue();
        assertEquals("guest@mail.com", msg.getTo()[0]);
        assertTrue(msg.getSubject().contains("Reservation Confirmed"));
        assertTrue(msg.getText().contains("Guests: 4"));
    }

    @Test
    void sendOwnerNotificationBuildsExpectedMessage() {
        Reservation reservation = reservation();

        service.sendOwnerNotification(reservation);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage msg = captor.getValue();
        assertEquals("saffronsoul2024@gmail.com", msg.getTo()[0]);
        assertTrue(msg.getSubject().contains("New Reservation"));
        assertTrue(msg.getText().contains("Name: Guest"));
    }

    private Reservation reservation() {
        Reservation reservation = new Reservation();
        reservation.setName("Guest");
        reservation.setEmail("guest@mail.com");
        reservation.setPhone("9999999999");
        reservation.setDate(LocalDate.now().plusDays(1));
        reservation.setTime("8:00 PM");
        reservation.setGuests(4);
        reservation.setNotes("Window");
        return reservation;
    }
}

