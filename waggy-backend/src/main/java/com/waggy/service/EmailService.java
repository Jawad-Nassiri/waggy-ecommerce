package com.waggy.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final Resend resend;
    private final String from;

    public EmailService(
            Resend resend,
            @Value("${resend.from}") String from) {
        this.resend = resend;
        this.from = from;
    }

    public void sendResetEmail(String to, String resetLink) {
        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(from)
                .to(to)
                .subject("Waggy - Password Reset")
                .text("Click this link to reset your password:\n" + resetLink)
                .build();

        try {
            resend.emails().send(params);
        } catch (ResendException e) {
            throw new RuntimeException("Failed to send reset email", e);
        }
    }
}