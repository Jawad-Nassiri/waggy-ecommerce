package com.waggy.integration;

import com.waggy.entity.Category;
import com.waggy.entity.Product;
import com.waggy.entity.Role;
import com.waggy.repository.CategoryRepository;
import com.waggy.repository.ProductRepository;
import com.waggy.service.JwtService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM order_items");
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    private Product createProduct(Category category) {
        Product product = new Product();
        product.setName("Dog Food");
        product.setDescription("Premium food for dogs");
        product.setPrice(new BigDecimal("29.99"));
        product.setStock(50);
        product.setImage("dog-food.jpg");
        product.setCategory(category);

        return productRepository.save(product);
    }

    private String generateToken() {
        return jwtService.generateToken(
                "admin@test.com",
                Role.ADMIN
        );
    }

    @Test
    void createProduct_shouldCreateProduct() throws Exception {
        String token = generateToken();

        Category category = new Category();
        category.setName("Dog Food");
        category.setSlug("dog_food");
        category = categoryRepository.save(category);

        String request = """
                {
                    "name": "Dog Food",
                    "description": "Premium food for dogs",
                    "price": 29.99,
                    "stock": 50,
                    "image": "dog-food.jpg",
                    "categoryId": %d
                }
                """.formatted(category.getId());

        mockMvc.perform(multipart("/products")
                        .file(new MockMultipartFile(
                                "product", "", MediaType.APPLICATION_JSON_VALUE,
                                request.getBytes()
                        ))
                        .file(new MockMultipartFile(
                                "image", "dog-food.jpg", "image/jpeg",
                                "fake image content".getBytes()
                        ))
                        .cookie(new Cookie("token", token))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Product product = productRepository.findAll().getFirst();

        assert product.getName().equals("Dog Food");
        assert product.getDescription().equals("Premium food for dogs");
        assert product.getPrice().doubleValue() == 29.99;
        assert product.getStock() == 50;
        assert product.getCategory().getId().equals(category.getId());
    }

    @Test
    void getProductById_shouldReturnProduct() throws Exception {
        Category category = new Category();
        category.setName("Dog Food");
        category.setSlug("dog_food");
        category = categoryRepository.save(category);

        Product product = createProduct(category);

        mockMvc.perform(get("/products/{id}", product.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void updateProduct_shouldUpdateProduct() throws Exception {
        String token = generateToken();

        Category category = new Category();
        category.setName("Dog Food");
        category.setSlug("dog_food");
        category = categoryRepository.save(category);

        Product product = createProduct(category);

        String request = """
                {
                    "name": "Premium Dog Food",
                    "description": "Updated food",
                    "price": 39.99,
                    "stock": 100,
                    "image": "premium-dog-food.jpg",
                    "categoryId": %d
                }
                """.formatted(category.getId());

        mockMvc.perform(multipart("/products/{id}", product.getId())
                        .file(new MockMultipartFile(
                                "product",
                                "",
                                MediaType.APPLICATION_JSON_VALUE,
                                request.getBytes()
                        ))
                        .file(new MockMultipartFile(
                                "image",
                                "premium-dog-food.jpg",
                                "image/jpeg",
                                "fake image content".getBytes()
                        ))
                        .with(requestBuilder -> {
                            requestBuilder.setMethod("PUT");
                            return requestBuilder;
                        })
                        .cookie(new Cookie("token", token))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Product updated = productRepository.findById(product.getId()).orElseThrow();

        assert updated.getName().equals("Premium Dog Food");
        assert updated.getPrice().doubleValue() == 39.99;
        assert updated.getStock() == 100;
    }

    @Test
    void deleteProduct_shouldDeleteProduct() throws Exception {
        String token = generateToken();

        Category category = new Category();
        category.setName("Dog Food");
        category.setSlug("dog_food");
        category = categoryRepository.save(category);

        Product product = createProduct(category);

        mockMvc.perform(delete("/products/{id}", product.getId())
                        .cookie(new Cookie("token", token)))
                .andExpect(status().isNoContent());

        assert productRepository.findById(product.getId()).isEmpty();
    }
}