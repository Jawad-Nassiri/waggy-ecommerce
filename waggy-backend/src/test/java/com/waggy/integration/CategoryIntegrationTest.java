package com.waggy.integration;

import com.waggy.dto.category.CategoryRequestDTO;
import com.waggy.entity.Category;
import com.waggy.entity.Role;
import com.waggy.repository.CategoryRepository;
import com.waggy.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;


import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CategoryIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM products");
        jdbcTemplate.update("DELETE FROM categories");
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String generateToken() {
        return jwtService.generateToken(
                "admin@test.com",
                Role.ADMIN
        );
    }

    private CategoryRequestDTO createRequestDTO() {
        return new CategoryRequestDTO(
                "Dog Food",
                "dog_food"
        );
    }

    @Test
    void createCategory_shouldSaveCategory() throws Exception {

        String token = generateToken();
        CategoryRequestDTO request = createRequestDTO();

        mockMvc.perform(post("/categories")
                        .header("Authorization", "Bearer " + token)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Dog Food"))
                .andExpect(jsonPath("$.slug").value("dog_food"));
    }

    @Test
    void findCategoryById_shouldReturnCategory() throws Exception {

        Category category = new Category();
        category.setName("Dog Food");
        category.setSlug("dog_food");

        Category savedCategory = categoryRepository.save(category);

        String token = generateToken();

        mockMvc.perform(get("/categories/{id}", savedCategory.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedCategory.getId()))
                .andExpect(jsonPath("$.name").value("Dog Food"))
                .andExpect(jsonPath("$.slug").value("dog_food"));
    }

    @Test
    void updateCategory_shouldUpdateCategory() throws Exception {

        Category category = new Category();
        category.setName("Dog Food");
        category.setSlug("dog_food");

        Category savedCategory = categoryRepository.save(category);

        String token = generateToken();

        mockMvc.perform(put("/categories/{id}", savedCategory.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "name": "Cat Food",
                                "slug": "cat_food"
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedCategory.getId()))
                .andExpect(jsonPath("$.name").value("Cat Food"))
                .andExpect(jsonPath("$.slug").value("cat_food"));
    }
}
