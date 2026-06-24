package com.restaurant.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockFilterChain;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CorsGlobalFilterTest {

  @Autowired
  private CorsGlobalFilter corsGlobalFilter;

  @Test
  void testCorsHeadersAdded() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("GET");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    corsGlobalFilter.doFilter(request, response, chain);

    assertNotNull(response.getHeader("Access-Control-Allow-Origin"));
    assertNotNull(response.getHeader("Access-Control-Allow-Methods"));
    assertNotNull(response.getHeader("Access-Control-Allow-Headers"));
  }

  @Test
  void testPreflightRequest() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("OPTIONS");
    request.addHeader("Origin", "http://localhost:3000");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    corsGlobalFilter.doFilter(request, response, chain);

    assertEquals(200, response.getStatus());
  }

  @Test
  void testRegularRequest() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setMethod("GET");
    request.setRequestURI("/api/menu");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    corsGlobalFilter.doFilter(request, response, chain);

    assertNotNull(response.getHeader("Access-Control-Allow-Origin"));
  }
}
