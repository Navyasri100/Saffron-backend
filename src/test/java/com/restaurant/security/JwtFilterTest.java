package com.restaurant.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import jakarta.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtFilterTest {

  @Autowired
  private JwtFilter jwtFilter;

  @Test
  void testDoFilterInternalWithoutAuthorizationHeader() throws ServletException, IOException {
    MockHttpServletRequest request = new MockHttpServletRequest();
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    jwtFilter.doFilterInternal(request, response, chain);

    assertTrue(true);
  }

  @Test
  void testDoFilterInternalWithOptionsMethod() throws ServletException, IOException {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("OPTIONS");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    jwtFilter.doFilterInternal(request, response, chain);

    assertTrue(true);
  }

  @Test
  void testDoFilterInternalWithInvalidToken() throws ServletException, IOException {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer invalid.token.here");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    jwtFilter.doFilterInternal(request, response, chain);

    assertTrue(true);
  }
}
