package com.waggy.service;

import com.resend.Resend;
import com.resend.services.emails.Emails;
import com.resend.services.emails.model.CreateEmailOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private Resend resend;

    @Mock
    private Emails emails;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        when(resend.emails()).thenReturn(emails);
    }

    @Test
    void sendResetEmail_shouldSendEmail() throws Exception {
        emailService.sendResetEmail(
                "test@example.com",
                "http://localhost:4200/reset-password?token=test-token"
        );

        verify(emails).send(any(CreateEmailOptions.class));
    }
}