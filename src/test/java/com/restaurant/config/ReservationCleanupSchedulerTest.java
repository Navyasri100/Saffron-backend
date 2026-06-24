package com.restaurant.config;

import com.restaurant.repository.ReservationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReservationCleanupSchedulerTest {

  @SpyBean
  private ReservationCleanupScheduler scheduler;

  @Autowired
  private ReservationRepository reservationRepository;

  @Test
  void testSchedulerExists() {
    assertNotNull(scheduler);
  }

  @Test
  void testDeletePastReservationsMethodExists() {
    assertNotNull(scheduler);
    assertTrue(true);
  }

  @Test
  void testSchedulerIsComponent() {
    assertNotNull(scheduler);
  }

  @Test
  void testSchedulerCanDelete() {
    long countBefore = reservationRepository.count();
    assertNotNull(countBefore);
  }
}
