package com.restaurant.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OtpServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private OtpService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "fromEmail", "no-reply@example.com");
    }

    @Test
    void generateAndSendStoresOtpAndSendsMail() {
        service.generateAndSend("  USER@MAIL.COM ", "User", "user@mail.com");

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertEquals("no-reply@example.com", message.getFrom());
        assertEquals("user@mail.com", message.getTo()[0]);
        assertTrue(message.getSubject().contains("OTP"));
        assertNotNull(service.getCustomerName("user@mail.com"));
    }

    @Test
    void verifyReturnsTrueForCorrectOtpAndRemovesEntry() {
        service.generateAndSend("user@mail.com", "User", "user@mail.com");
        String otp = extractOtpFromLastEmail();

        boolean verified = service.verify("user@mail.com", otp);

        assertTrue(verified);
        assertNull(service.getCustomerName("user@mail.com"));
    }

    @Test
    void verifyReturnsFalseForWrongOtp() {
        service.generateAndSend("user@mail.com", "User", "user@mail.com");

        boolean verified = service.verify("user@mail.com", "000000");

        assertFalse(verified);
        assertEquals("User", service.getCustomerName("user@mail.com"));
    }

    @Test
    void verifyReturnsFalseWhenContactNotPresent() {
        assertFalse(service.verify("none@mail.com", "123456"));
    }

    private String extractOtpFromLastEmail() {
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        String text = captor.getValue().getText();
        Matcher matcher = Pattern.compile("\\b(\\d{6})\\b").matcher(text);
        assertTrue(matcher.find());
        return matcher.group(1);
    }
}

