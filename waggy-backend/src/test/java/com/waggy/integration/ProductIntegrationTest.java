package com.waggy.integration;

import com.waggy.entity.Category;
import com.waggy.entity.Product;
import com.waggy.entity.Role;
import com.waggy.repository.CategoryRepository;
import com.waggy.repository.ProductRepository;
import com.waggy.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private CategoryRepository categoryRepository;

    @Autowired
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
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

        mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
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

        mockMvc.perform(put("/products/{id}", product.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
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
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        assert productRepository.findById(product.getId()).isEmpty();
    }
}