package com.waggy.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.waggy.entity.PasswordResetToken;
import com.waggy.entity.User;
import com.waggy.repository.PasswordResetTokenRepository;
import com.waggy.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PasswordResetService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public String generateToken() {
        return UUID.randomUUID().toString();
    }

    public void saveToken(User user, String token) {
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));

        passwordResetTokenRepository.save(resetToken);
    }

    public PasswordResetToken validateToken(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.delete(resetToken);
            throw new IllegalArgumentException("Token has expired");
        }

        return resetToken;
    }

    public void deleteToken(String token) {
        passwordResetTokenRepository.deleteByToken(token);
    }

    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = generateToken();
        saveToken(user, token);

        String resetLink = "https://waggy-frontend.onrender.com/reset-password?token=" + token;

        emailService.sendResetEmail(user.getEmail(), resetLink);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = validateToken(token);

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);
        deleteToken(token);
    }
}
