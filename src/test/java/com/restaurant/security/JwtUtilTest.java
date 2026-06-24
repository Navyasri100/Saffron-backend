package com.restaurant.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

  private JwtUtil jwtUtil;

  @BeforeEach
  void setUp() {
    jwtUtil = new JwtUtil();
    ReflectionTestUtils.setField(jwtUtil, "secret", "mySecretKeyForJWTTokenGenerationRestaurantApp2024");
    ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
  }

  @Test
  void testGenerateToken() {
    String token = jwtUtil.generateToken("testuser");
    assertNotNull(token);
    assertTrue(token.length() > 0);
  }

  @Test
  void testExtractUsername() {
    String username = "testuser";
    String token = jwtUtil.generateToken(username);
    String extractedUsername = jwtUtil.extractUsername(token);
    assertEquals(username, extractedUsername);
  }

  @Test
  void testValidateTokenValid() {
    String token = jwtUtil.generateToken("testuser");
    assertTrue(jwtUtil.validateToken(token));
  }

  @Test
  void testValidateTokenInvalid() {
    String invalidToken = "invalid.token.here";
    assertFalse(jwtUtil.validateToken(invalidToken));
  }

  @Test
  void testValidateTokenEmpty() {
    assertFalse(jwtUtil.validateToken(""));
  }

  @Test
  void testValidateTokenNull() {
    assertFalse(jwtUtil.validateToken(null));
  }

  @Test
  void testExtractUsernameFromInvalidToken() {
    assertThrows(JwtException.class, () -> jwtUtil.extractUsername("invalid.token"));
  }

  @Test
  void testMultipleTokensIndependent() {
    String token1 = jwtUtil.generateToken("user1");
    String token2 = jwtUtil.generateToken("user2");
    assertNotEquals(jwtUtil.extractUsername(token1), jwtUtil.extractUsername(token2));
  }
}
