package com.waggy.service;

import com.waggy.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        ReflectionTestUtils.setField(
                jwtService,
                "secretKey",
                "my-test-secret-key-that-is-long-enough-for-hs256"
        );
    }

    @Test
    void generateToken_shouldGenerateToken() {
        String token = jwtService.generateToken(
                "jawad@test.com",
                Role.USER
        );

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void extractEmail_shouldReturnEmail() {
        String token = jwtService.generateToken(
                "jawad@test.com",
                Role.USER
        );

        String email = jwtService.extractEmail(token);

        assertEquals("jawad@test.com", email);
    }

    @Test
    void extractRole_shouldReturnRole() {
        String token = jwtService.generateToken(
                "jawad@test.com",
                Role.USER
        );

        String role = jwtService.extractRole(token);

        assertEquals("USER", role);
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {
        String token = jwtService.generateToken(
                "jawad@test.com",
                Role.USER
        );

        boolean result = jwtService.isTokenValid(token);

        assertTrue(result);
    }

    @Test
    void isTokenValid_shouldReturnFalseForInvalidToken() {
        boolean result = jwtService.isTokenValid("invalid-token");

        assertFalse(result);
    }
}