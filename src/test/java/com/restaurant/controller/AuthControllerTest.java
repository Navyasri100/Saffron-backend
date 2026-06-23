package com.restaurant.controller;

import com.restaurant.dto.LoginRequest;
import com.restaurant.dto.LoginResponse;
import com.restaurant.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthController controller;

    @Test
    void loginReturnsTokenAndUsername() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("secret");

        UserDetails user = User.withUsername("admin").password("x").authorities("ROLE_ADMIN").build();
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(user);
        when(jwtUtil.generateToken("admin")).thenReturn("jwt-token");

        ResponseEntity<LoginResponse> response = controller.login(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("jwt-token", response.getBody().getToken());
        assertEquals("admin", response.getBody().getUsername());
        verify(authenticationManager).authenticate(any());
    }

    @Test
    void loginThrowsWhenAuthenticationFails() {
        LoginRequest request = new LoginRequest();
        request.setUsername("admin");
        request.setPassword("wrong");

        doThrow(new BadCredentialsException("bad creds")).when(authenticationManager).authenticate(any());

        assertThrows(BadCredentialsException.class, () -> controller.login(request));
    }
}

