package com.waggy.repository;

import com.waggy.entity.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class CartItemRepositoryTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void findByCartIdAndProductId_shouldReturnCartItem_whenExists() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@gmail.com");
        user.setPassword("Password123!");
        user.setRole(Role.USER);
        user = userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(user);
        cart = cartRepository.save(cart);

        Category category = new Category();
        category.setName("Dog Food");
        category.setSlug("dog_food");
        category = categoryRepository.save(category);

        Product product = new Product();
        product.setName("Premium Dog Food");
        product.setDescription("Healthy food for dogs");
        product.setPrice(new BigDecimal("25.00"));
        product.setStock(100);
        product.setImage("dog-food.jpg");
        product.setCategory(category);
        product = productRepository.save(product);

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItemRepository.save(cartItem);

        Optional<CartItem> result =
                cartItemRepository.findByCartIdAndProductId(
                        cart.getId(),
                        product.getId()
                );

        assertTrue(result.isPresent());
        assertEquals(cart.getId(), result.get().getCart().getId());
        assertEquals(product.getId(), result.get().getProduct().getId());
        assertEquals(2, result.get().getQuantity());
    }

    @Test
    void findByCartIdAndProductId_shouldReturnEmpty_whenCartItemDoesNotExist() {
        Optional<CartItem> result =
                cartItemRepository.findByCartIdAndProductId(999, 999);

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteAllByCartId_shouldDeleteWhenCartExists() {
        User user = new User();
        user.setName("John");
        user.setEmail("john@gmail.com");
        user.setPassword("Password123!");
        user.setRole(Role.USER);
        user = userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(user);
        cart = cartRepository.save(cart);

        Category category = new Category();
        category.setName("Dog Food");
        category.setSlug("dog_food");
        category = categoryRepository.save(category);

        Product product = new Product();
        product.setName("Premium Dog Food");
        product.setDescription("Healthy food for dogs");
        product.setPrice(new BigDecimal("25.00"));
        product.setStock(100);
        product.setImage("dog-food.jpg");
        product.setCategory(category);
        product = productRepository.save(product);

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItemRepository.save(cartItem);

        Integer cartId = cart.getId();

        cartItemRepository.deleteAllByCartId(cartId);

        List<CartItem> result = cartItemRepository.findAll()
                .stream()
                .filter(item -> item.getCart().getId().equals(cartId))
                .toList();

        assertTrue(result.isEmpty());
    }
}