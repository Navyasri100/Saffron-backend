package com.restaurant.service;

import com.restaurant.model.Reservation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
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
        logger.info("📧 EmailService initialized with JavaMailSender");
    }

    @jakarta.annotation.PostConstruct
    public void init() {
        logger.info("=== EMAIL CONFIGURATION ===");
        logger.info("From Email: {}", fromEmail);
        logger.info("Mail Sender: {}", mailSender != null ? "✅ Configured" : "❌ NOT configured");
        logger.info("========================");
    }

    @Async("taskExecutor")
    public void sendReservationConfirmation(Reservation r) {
        logger.info("[ASYNC] Starting reservation confirmation email for: {}", r.getEmail());
        Thread.currentThread().setName("email-customer-" + r.getId());

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
            logger.info("[ASYNC] Reservation confirmation completed for: {}", r.getEmail());
        } catch (Exception e) {
            logger.error("[ASYNC] Reservation confirmation failed for {}: {}", r.getEmail(), e.getMessage(), e);
        }
    }

    @Async("taskExecutor")
    public void sendOwnerNotification(Reservation r) {
        logger.info("[ASYNC] Starting owner notification for reservation: {}", r.getId());

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
            logger.info("[ASYNC] Owner notification completed for reservation: {}", r.getId());
        } catch (Exception e) {
            logger.error("[ASYNC] Owner notification failed for reservation {}: {}", r.getId(), e.getMessage(), e);
        }
    }

    @Async("taskExecutor")
    public void sendOtp(String toEmail, String customerName, String otp) {
        logger.info("[ASYNC] Starting OTP email for: {}", toEmail);

        try {
            String body =
                "Dear " + customerName + ",\n\n" +
                "Your One-Time Password to access the Saffron & Soul menu is:\n\n" +
                "        " + otp + "\n\n" +
                "This OTP is valid for 10 minutes.\n\n" +
                "Enjoy your dining experience!\n" +
                "— Team Saffron & Soul";

            send(toEmail, "Saffron & Soul — Your Menu Access OTP", body);
            logger.info("[ASYNC] OTP email completed for: {}", toEmail);
        } catch (Exception e) {
            logger.error("[ASYNC] OTP email failed for {}: {}", toEmail, e.getMessage(), e);
        }
    }

    private void send(String toEmail, String subject, String text) {
        try {
            if (fromEmail == null || fromEmail.isEmpty()) {
                logger.error("❌ EMAIL CONFIG ERROR: spring.mail.username is not set!");
                logger.error("❌ Emails cannot be sent without Mail Server configuration");
                return;
            }

            logger.debug("Preparing email - From: {}, To: {}, Subject: {}", fromEmail, toEmail, subject);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_NAME + " <" + fromEmail + ">");
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(text);

            logger.info("🚀 Sending email to: {} | Subject: {}", toEmail, subject);
            mailSender.send(message);
            logger.info("✅ EMAIL SENT SUCCESSFULLY to: {}", toEmail);

        } catch (Exception e) {
            logger.error("❌ EMAIL SEND FAILED - To: {}, Error: {}", toEmail, e.getClass().getSimpleName());
            logger.error("❌ Exception details: {}", e.getMessage());
            if (e.getCause() != null) {
                logger.error("❌ Root cause: {}", e.getCause().getMessage());
            }
            logger.error("❌ Stack trace: ", e);
        }
    }
}
