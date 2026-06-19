package com.restaurant.config;

import com.restaurant.model.Admin;
import com.restaurant.repository.AdminRepository;
import com.restaurant.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import jakarta.transaction.Transactional;
import java.time.LocalDate;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired private AdminRepository adminRepository;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (adminRepository.findByUsername("admin").isEmpty()) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            adminRepository.save(admin);
            System.out.println("Default admin created: admin / admin123");
        }

        // Delete reservations from previous days
        reservationRepository.deleteByDateBefore(LocalDate.now());
        System.out.println("Cleared past reservations.");
    }
}
