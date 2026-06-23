package com.restaurant.service;

import com.restaurant.model.Reservation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final String FROM_NAME = "Saffron & Soul";

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
        logger.info("✅ EmailService initialized with JavaMailSender");
    }

    @jakarta.annotation.PostConstruct
    public void init() {
        logger.info("╔════════════════════════════════════════════╗");
        logger.info("║ 📧 EMAIL CONFIGURATION INITIALIZED 📧      ║");
        logger.info("╠════════════════════════════════════════════╣");
        logger.info("║ From Email: {}", String.format("%-33s║", fromEmail));
        logger.info("║ Mail Sender: {}", String.format("%-30s║", mailSender != null ? "✅ CONFIGURED" : "❌ NOT CONFIGURED"));
        logger.info("╚════════════════════════════════════════════╝");
    }

    public void sendReservationConfirmation(Reservation r) {
        logger.info("════════════════════════════════════════════");
        logger.info("📨 SENDING RESERVATION CONFIRMATION EMAIL");
        logger.info("════════════════════════════════════════════");
        logger.info("To: {}", r.getEmail());
        logger.info("Name: {}", r.getName());
        logger.info("Reservation ID: {}", r.getId());

        try {
            String body =
                "Dear " + r.getName() + ",\n\n" +
                "Your reservation at Saffron & Soul has been confirmed!\n\n" +
                "Date: " + r.getDate() + "\n" +
                "Time: " + r.getTime() + "\n" +
                "Guests: " + r.getGuests() + "\n\n" +
                "We look forward to welcoming you.\n\n" +
                "— Team Saffron & Soul\n" +
                "saffronsoul2024@gmail.com";

            send(r.getEmail(), "Reservation Confirmed — Saffron & Soul", body);
            logger.info("✅ RESERVATION CONFIRMATION EMAIL SENT SUCCESSFULLY");
        } catch (Exception e) {
            logger.error("❌ RESERVATION CONFIRMATION EMAIL FAILED: {}", e.getMessage(), e);
        }
        logger.info("════════════════════════════════════════════\n");
    }

    public void sendOwnerNotification(Reservation r) {
        logger.info("════════════════════════════════════════════");
        logger.info("📨 SENDING OWNER NOTIFICATION EMAIL");
        logger.info("════════════════════════════════════════════");
        logger.info("To: {}", fromEmail);
        logger.info("Customer: {}", r.getName());
        logger.info("Reservation ID: {}", r.getId());

        try {
            String body =
                "New reservation received:\n\n" +
                "Name: " + r.getName() + "\n" +
                "Email: " + r.getEmail() + "\n" +
                "Phone: " + r.getPhone() + "\n" +
                "Date: " + r.getDate() + "\n" +
                "Time: " + r.getTime() + "\n" +
                "Guests: " + r.getGuests() + "\n" +
                "Notes: " + r.getNotes();

            send(fromEmail, "New Reservation - " + r.getName(), body);
            logger.info("✅ OWNER NOTIFICATION EMAIL SENT SUCCESSFULLY");
        } catch (Exception e) {
            logger.error("❌ OWNER NOTIFICATION EMAIL FAILED: {}", e.getMessage(), e);
        }
        logger.info("════════════════════════════════════════════\n");
    }

    public void sendOtp(String toEmail, String customerName, String otp) {
        logger.info("════════════════════════════════════════════");
        logger.info("📨 SENDING OTP EMAIL");
        logger.info("════════════════════════════════════════════");
        logger.info("To: {}", toEmail);
        logger.info("Customer: {}", customerName);
        logger.info("OTP: {}", otp);

        try {
            String body =
                "Dear " + customerName + ",\n\n" +
                "Your One-Time Password to access the Saffron & Soul menu is:\n\n" +
                "        " + otp + "\n\n" +
                "This OTP is valid for 10 minutes.\n\n" +
                "Enjoy your dining experience!\n" +
                "— Team Saffron & Soul";

            send(toEmail, "Saffron & Soul — Your Menu Access OTP", body);
            logger.info("✅ OTP EMAIL SENT SUCCESSFULLY");
        } catch (Exception e) {
            logger.error("❌ OTP EMAIL FAILED: {}", e.getMessage(), e);
        }
        logger.info("════════════════════════════════════════════\n");
    }

    private void send(String toEmail, String subject, String text) {
        logger.info("─────────────────────────────────────────────");
        logger.info("  Preparing to send email...");
        logger.info("─────────────────────────────────────────────");

        try {
            if (fromEmail == null || fromEmail.isEmpty()) {
                logger.error("❌ CRITICAL: spring.mail.username is NOT configured!");
                logger.error("❌ Set MAIL_USERNAME environment variable");
                throw new Exception("Mail username not configured");
            }

            logger.info("✓ From: {}", fromEmail);
            logger.info("✓ To: {}", toEmail);
            logger.info("✓ Subject: {}", subject);
            logger.info("✓ Mail Sender available: YES");

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_NAME + " <" + fromEmail + ">");
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(text);

            logger.info("🚀 ATTEMPTING TO SEND...");
            mailSender.send(message);
            logger.info("✅ SUCCESS! Email delivered to: {}", toEmail);

        } catch (Exception e) {
            logger.error("═══════════════════════════════════════════");
            logger.error("❌ EMAIL SEND FAILED");
            logger.error("═══════════════════════════════════════════");
            logger.error("❌ Recipient: {}", toEmail);
            logger.error("❌ Error Type: {}", e.getClass().getName());
            logger.error("❌ Error Message: {}", e.getMessage());

            if (e.getCause() != null) {
                logger.error("❌ Root Cause: {}", e.getCause().getClass().getName());
                logger.error("❌ Root Message: {}", e.getCause().getMessage());
            }

            logger.error("❌ Stack Trace:", e);
            logger.error("═══════════════════════════════════════════");
        }
    }
}
