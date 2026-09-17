package com.waggy.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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