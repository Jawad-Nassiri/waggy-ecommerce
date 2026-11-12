package com.waggy.repository;

import com.waggy.entity.PasswordResetToken;
import com.waggy.entity.Role;
import com.waggy.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class PasswordResetTokenRepositoryTest {
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private UserRepository userRepository;

    User createUser() {
        User user = new User();
        user.setName("Jawad");
        user.setEmail("test@example.com");
        user.setPassword("password");
        user.setRole(Role.USER);

        return user;
    }

    @Test
    void findByToken_shouldFindByToken() {
        User user = createUser();

        userRepository.save(user);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken("test-token");
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));

        passwordResetTokenRepository.save(resetToken);

        assertTrue(passwordResetTokenRepository.findByToken("test-token").isPresent());
    }

    @Test
    void deleteByToken_shouldDeleteToken() {
        User user = createUser();
        userRepository.save(user);

        PasswordResetToken token = new PasswordResetToken();
        token.setToken("delete-token");
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(30));

        passwordResetTokenRepository.save(token);
        passwordResetTokenRepository.deleteByToken("delete-token");

        assertFalse(passwordResetTokenRepository.findByToken("delete-token").isPresent());
    }

    @Test
    void deleteByUser_shouldDeleteAllUserTokens() {
        User user = createUser();
        userRepository.save(user);

        PasswordResetToken token1 = new PasswordResetToken();
        token1.setToken("token-1");
        token1.setUser(user);
        token1.setExpiryDate(LocalDateTime.now().plusMinutes(30));

        PasswordResetToken token2 = new PasswordResetToken();
        token2.setToken("token-2");
        token2.setUser(user);
        token2.setExpiryDate(LocalDateTime.now().plusMinutes(30));

        passwordResetTokenRepository.save(token1);
        passwordResetTokenRepository.save(token2);

        passwordResetTokenRepository.deleteByUser(user);

        assertTrue(passwordResetTokenRepository.findAll().isEmpty());
    }
}
