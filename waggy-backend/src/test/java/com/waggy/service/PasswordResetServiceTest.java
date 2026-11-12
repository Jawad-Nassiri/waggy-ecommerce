
package com.waggy.service;

import com.waggy.entity.PasswordResetToken;
import com.waggy.entity.User;
import com.waggy.repository.PasswordResetTokenRepository;
import com.waggy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetService passwordResetService;

    @Test
    void generateToken_shouldReturnUniqueTokens() {
        String token1 = passwordResetService.generateToken();
        String token2 = passwordResetService.generateToken();

        assertNotNull(token1);
        assertNotEquals(token1, token2);
    }

    @Test
    void saveToken_shouldSaveToken() {
        User user = new User();

        passwordResetService.saveToken(user, "test-token");

        verify(passwordResetTokenRepository).save(argThat(token ->
                token.getToken().equals("test-token")
                        && token.getUser() == user
                        && token.getExpiryDate() != null
        ));
    }

    @Test
    void validateToken_shouldReturnTokenWhenValid() {
        PasswordResetToken token = createToken(
                "valid-token",
                LocalDateTime.now().plusMinutes(30)
        );

        when(passwordResetTokenRepository.findByToken("valid-token"))
                .thenReturn(Optional.of(token));

        PasswordResetToken result =
                passwordResetService.validateToken("valid-token");

        assertEquals(token, result);
    }

    @Test
    void validateToken_shouldThrowWhenTokenDoesNotExist() {
        when(passwordResetTokenRepository.findByToken("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> passwordResetService.validateToken("unknown"));
    }

    @Test
    void validateToken_shouldDeleteExpiredTokenAndThrow() {
        PasswordResetToken token = createToken(
                "expired-token",
                LocalDateTime.now().minusMinutes(1)
        );

        when(passwordResetTokenRepository.findByToken("expired-token"))
                .thenReturn(Optional.of(token));

        assertThrows(IllegalArgumentException.class,
                () -> passwordResetService.validateToken("expired-token"));

        verify(passwordResetTokenRepository).delete(token);
    }

    @Test
    void deleteToken_shouldDeleteTokenByTokenValue() {
        passwordResetService.deleteToken("test-token");

        verify(passwordResetTokenRepository).deleteByToken("test-token");
    }

    @Test
    void resetPassword_shouldUpdatePasswordAndDeleteToken() {
        User user = new User();

        PasswordResetToken token = createToken(
                "valid-token",
                LocalDateTime.now().plusMinutes(30)
        );
        token.setUser(user);

        when(passwordResetTokenRepository.findByToken("valid-token"))
                .thenReturn(Optional.of(token));

        when(passwordEncoder.encode("new-password"))
                .thenReturn("hashed-password");

        passwordResetService.resetPassword("valid-token", "new-password");

        assertEquals("hashed-password", user.getPassword());
        verify(userRepository).save(user);
        verify(passwordResetTokenRepository).deleteByToken("valid-token");
    }

    private PasswordResetToken createToken(
            String value, LocalDateTime expiryDate) {

        PasswordResetToken token = new PasswordResetToken();
        token.setToken(value);
        token.setExpiryDate(expiryDate);

        return token;
    }
}
