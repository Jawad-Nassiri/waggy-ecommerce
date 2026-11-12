
package com.waggy.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendResetEmail_shouldSendEmailWithCorrectDetails() {
        String email = "test@example.com";
        String resetLink = "http://localhost:4200/reset-password?token=test-token";

        emailService.sendResetEmail(email, resetLink);

        var captor = forClass(SimpleMailMessage.class);

        verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();

        assertEquals(email, message.getTo()[0]);
        assertEquals("Waggy - Password Reset", message.getSubject());
        assertEquals(
                "Click this link to reset your password:\n" + resetLink,
                message.getText()
        );
    }
}
