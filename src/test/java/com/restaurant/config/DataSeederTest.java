package com.restaurant.config;

import com.restaurant.model.Admin;
import com.restaurant.repository.AdminRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DataSeederTest {

  @SpyBean
  private DataSeeder dataSeeder;

  @Autowired
  private AdminRepository adminRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Test
  void testSeederExists() {
    assertNotNull(dataSeeder);
  }

  @Test
  void testAdminRepositoryExists() {
    assertNotNull(adminRepository);
  }

  @Test
  void testPasswordEncoderExists() {
    assertNotNull(passwordEncoder);
  }

  @Test
  void testDefaultAdminExists() {
    Optional<Admin> admin = adminRepository.findByUsername("admin");
    assertTrue(admin.isPresent());
    assertEquals("admin", admin.get().getUsername());
  }

  @Test
  void testSeederImplementsCommandLineRunner() {
    assertTrue(dataSeeder instanceof org.springframework.boot.CommandLineRunner);
  }
}
