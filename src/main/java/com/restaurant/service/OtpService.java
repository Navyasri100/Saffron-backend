package com.restaurant.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static class OtpEntry {
        String otp;
        String customerName;
        String email; // store email for sending
        LocalDateTime expiry;

        OtpEntry(String otp, String customerName, String email) {
            this.otp = otp;
            this.customerName = customerName;
            this.email = email;
            this.expiry = LocalDateTime.now().plusMinutes(10);
        }

        boolean isExpired() {
            return LocalDateTime.now().isAfter(expiry);
        }
    }

    private final Map<String, OtpEntry> store = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public void generateAndSend(String contact, String customerName, String customerEmail) {
        String otp = String.format("%06d", random.nextInt(1_000_000));
        store.put(contact.toLowerCase().trim(), new OtpEntry(otp, customerName, customerEmail));
        System.out.println("OTP for [" + contact + "]: " + otp);
        sendEmail(customerEmail, customerName, otp);
    }

    private void sendEmail(String toEmail, String customerName, String otp) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(fromEmail);
            msg.setTo(toEmail);
            msg.setSubject("Saffron & Soul — Your Menu Access OTP");
            msg.setText(
                "Dear " + customerName + ",\n\n" +
                "Your One-Time Password to access the Saffron & Soul menu is:\n\n" +
                "        " + otp + "\n\n" +
                "This OTP is valid for 10 minutes.\n\n" +
                "Enjoy your dining experience!\n" +
                "— Team Saffron & Soul"
            );
            mailSender.send(msg);
            System.out.println("OTP email sent to: " + toEmail);
        } catch (Exception e) {
            System.err.println("Failed to send OTP email: " + e.getMessage());
        }
    }

    public boolean verify(String contact, String otp) {
        OtpEntry entry = store.get(contact.toLowerCase().trim());
        if (entry == null || entry.isExpired()) return false;
        if (!entry.otp.equals(otp)) return false;
        store.remove(contact.toLowerCase().trim());
        return true;
    }

    public String getCustomerName(String contact) {
        OtpEntry entry = store.get(contact.toLowerCase().trim());
        return entry != null ? entry.customerName : null;
    }
}
