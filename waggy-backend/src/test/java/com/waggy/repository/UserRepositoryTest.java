package com.waggy.repository;

import com.waggy.entity.Role;
import com.waggy.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User createUser() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@gmail.com");
        user.setPassword("Password123!");
        user.setRole(Role.USER);

        return user;
    }

    @Test
    void findByEmail_shouldReturnUser() {
        User user = createUser();
        userRepository.save(user);

        Optional<User> result = userRepository.findByEmail("john@gmail.com");

        assertTrue(result.isPresent());
        assertEquals("john@gmail.com", result.get().getEmail());
    }

    @Test
    void existsByEmail_shouldReturnTrue_whenEmailExist() {
        User user = createUser();
        userRepository.save(user);

        Boolean result = userRepository.existsByEmail("john@gmail.com");

        assertTrue(result);
    }

    @Test
    void findByEmail_shouldReturnEmpty_whenEmailDoesNotExist() {
        Optional<User> result = userRepository.findByEmail("unknown@gmail.com");

        assertTrue(result.isEmpty());
    }

    @Test
    void existsByEmail_shouldReturnFalse_whenEmailDoesNotExist() {
        Boolean result = userRepository.existsByEmail("unknown@gmail.com");

        assertFalse(result);
    }
}