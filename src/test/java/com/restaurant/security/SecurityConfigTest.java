package com.restaurant.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SecurityConfigTest {

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Test
  void testPasswordEncoderExists() {
    assertNotNull(passwordEncoder);
  }

  @Test
  void testPasswordEncode() {
    String password = "testPassword123";
    String encoded = passwordEncoder.encode(password);
    assertNotNull(encoded);
    assertNotEquals(password, encoded);
  }

  @Test
  void testPasswordMatches() {
    String password = "testPassword123";
    String encoded = passwordEncoder.encode(password);
    assertTrue(passwordEncoder.matches(password, encoded));
  }

  @Test
  void testPasswordDoesNotMatch() {
    String password = "testPassword123";
    String encoded = passwordEncoder.encode(password);
    assertFalse(passwordEncoder.matches("wrongPassword", encoded));
  }

  @Test
  void testMultipleEncodesProduceDifferentResults() {
    String password = "testPassword123";
    String encoded1 = passwordEncoder.encode(password);
    String encoded2 = passwordEncoder.encode(password);
    assertNotEquals(encoded1, encoded2);
  }
}
