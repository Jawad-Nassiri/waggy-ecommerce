package com.waggy.integration;

import com.waggy.entity.Category;
import com.waggy.entity.Product;
import com.waggy.entity.Role;
import com.waggy.entity.User;
import com.waggy.repository.*;
import com.waggy.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.jdbc.core.JdbcTemplate;
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CartIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM cart_items");
        jdbcTemplate.update("DELETE FROM carts");
        jdbcTemplate.update("DELETE FROM products");
        jdbcTemplate.update("DELETE FROM categories");
        jdbcTemplate.update("DELETE FROM users");
    }

    private User createUser() {
        User user = new User();
        user.setName("Test User");
        user.setEmail("user@test.com");
        user.setPassword("password");
        user.setRole(Role.USER);

        return userRepository.save(user);
    }

    private String generateUserToken() {
        return jwtService.generateToken(
                "user@test.com",
                Role.USER
        );
    }

    private Product createProduct() {
        Category category = new Category();
        category.setName("Dog Food");
        category.setSlug("dog_food");
        category = categoryRepository.save(category);

        Product product = new Product();
        product.setName("Dog Food");
        product.setDescription("Premium food for dogs");
        product.setPrice(new BigDecimal("29.99"));
        product.setStock(50);
        product.setImage("dog-food.jpg");
        product.setCategory(category);

        return productRepository.save(product);
    }

    private String createCartRequest(Integer productId) {
        return """
                {
                    "productId": %d,
                    "quantity": 2
                }
                """.formatted(productId);
    }

    @Test
    void createCart_shouldCreateCart() throws Exception {
        createUser();

        String token = generateUserToken();

        mockMvc.perform(post("/carts")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void addItemToCart_shouldAddItem() throws Exception {
        createUser();
        Product product = createProduct();

        String token = generateUserToken();

        mockMvc.perform(post("/carts")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        String request = createCartRequest(product.getId());

        mockMvc.perform(post("/carts/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk());
    }

    @Test
    void updateCartItem_shouldUpdateItem() throws Exception {
        createUser();
        Product product = createProduct();

        String token = generateUserToken();

        mockMvc.perform(post("/carts")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        String addRequest = createCartRequest(product.getId());

        mockMvc.perform(post("/carts/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addRequest))
                .andExpect(status().isOk());

        String updateRequest = """
                {
                    "productId": %d,
                    "quantity": 5
                }
                """.formatted(product.getId());

        mockMvc.perform(put("/carts/items/{productId}", product.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateRequest))
                .andExpect(status().isOk());
    }

    @Test
    void clearCart_shouldClearCart() throws Exception {
        createUser();
        Product product = createProduct();

        String token = generateUserToken();

        mockMvc.perform(post("/carts")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        String request = createCartRequest(product.getId());

        mockMvc.perform(post("/carts/items")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/carts/me/items")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());
    }
}