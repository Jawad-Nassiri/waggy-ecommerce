package com.waggy.repository;

import com.waggy.entity.Cart;
import com.waggy.entity.Role;
import com.waggy.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class CartRepositoryTest {
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUserId_shouldReturnCart_whenUserExists() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@gmail.com");
        user.setPassword("Password123!");
        user.setRole(Role.USER);

        user = userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);

        Optional<Cart> result = cartRepository.findByUserId(user.getId());

        assertTrue(result.isPresent());
        assertEquals(user.getId(), result.get().getUser().getId());
    }

    @Test
    void findByUser_shouldReturnCart_whenUserExists() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@gmail.com");
        user.setPassword("Password123!");
        user.setRole(Role.USER);

        user = userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(user);
        cartRepository.save(cart);

        Optional<Cart> result = cartRepository.findByUser(user);

        assertTrue(result.isPresent());
        assertEquals(user.getId(), result.get().getUser().getId());
    }

    @Test
    void findByUserId_shouldReturnEmpty_whenUserDoesNotExist() {
        Optional<Cart> result = cartRepository.findByUserId(999);

        assertTrue(result.isEmpty());
    }

    @Test
    void findByUser_shouldReturnEmpty_whenUserDoesNotExist() {
        User user = new User();
        user.setName("Unknown");
        user.setEmail("unknown@gmail.com");
        user.setPassword("Password123!");
        user.setRole(Role.USER);


        user = userRepository.save(user);
        Optional<Cart> result = cartRepository.findByUser(user);

        assertTrue(result.isEmpty());
    }

}
