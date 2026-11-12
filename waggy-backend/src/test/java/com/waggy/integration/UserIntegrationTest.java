package com.waggy.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private JavaMailSender mailSender;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM order_items");
        jdbcTemplate.update("DELETE FROM cart_items");
        jdbcTemplate.update("DELETE FROM carts");
        jdbcTemplate.update("DELETE FROM orders");
        jdbcTemplate.update("DELETE FROM products");
        jdbcTemplate.update("DELETE FROM categories");
        jdbcTemplate.update("DELETE FROM password_reset_token");
        jdbcTemplate.update("DELETE FROM users");
    }

    @Test
    void createUser_shouldSaveUserAndReturnUser() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content("""
        {
            "name": "John5",
            "email": "john5@gmail.com",
            "password": "Password123!"
        }
        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John5"))
                .andExpect(jsonPath("$.email").value("john5@gmail.com"));
    }

    @Test
    void createUser_shouldReturnConflict_whenEmailAlreadyExists() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content("""
                    {
                        "name": "jawad",
                        "email": "jawad@gmail.com",
                        "password": "Password123!"
                    }
                    """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/register")
                        .contentType(APPLICATION_JSON)
                        .content("""
                    {
                        "name": "jawad",
                        "email": "jawad@gmail.com",
                        "password": "Password123!"
                    }
                    """))
                .andExpect(status().isConflict());
    }
}