package com.restaurant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RestaurantApplicationTest {

  @Autowired
  private ApplicationContext applicationContext;

  @Test
  void testApplicationContextLoads() {
    assertNotNull(applicationContext);
  }

  @Test
  void testApplicationStarts() {
    assertTrue(applicationContext.containsBean("restaurantApplication"));
  }

  @Test
  void testApplicationContextIsNotEmpty() {
    assertTrue(applicationContext.getBeanDefinitionCount() > 0);
  }

  @Test
  void testMainMethodCanRun() {
    assertDoesNotThrow(() -> RestaurantApplication.main(new String[]{}));
  }
}
