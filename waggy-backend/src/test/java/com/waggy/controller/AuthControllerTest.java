package com.waggy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waggy.dto.user.LoginRequestDTO;
import com.waggy.dto.user.UserRequestDTO;
import com.waggy.dto.user.UserResponseDTO;
import com.waggy.entity.Role;
import com.waggy.entity.User;
import com.waggy.service.JwtService;
import com.waggy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtService jwtService;

    private LoginRequestDTO createLoginRequest() {
        return new LoginRequestDTO(
                "john@example.com",
                "password123"
        );
    }

    private User createUser() {
        User user = new User();
        user.setEmail("john@example.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.USER);
        return user;
    }

    private UserRequestDTO createRegisterRequest() {
        return new UserRequestDTO(
                "John",
                "john@example.com",
                "password123&"
        );
    }

    private UserResponseDTO createUserResponse() {
        return new UserResponseDTO(
                1,
                "John",
                "john@example.com",
                Role.USER,
                LocalDateTime.now()
        );
    }

    @Test
    void login_shouldReturnToken() throws Exception {
        LoginRequestDTO request = createLoginRequest();
        User user = createUser();

        when(userService.findUserByEmail(request.email())).thenReturn(user);
        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user.getEmail(), user.getRole()))
                .thenReturn("jwt-token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("jwt-token"));

        verify(userService).findUserByEmail(request.email());
        verify(passwordEncoder).matches(request.password(), user.getPassword());
        verify(jwtService).generateToken(user.getEmail(), user.getRole());
    }

    @Test
    void register_shouldReturnCreatedUser() throws Exception {
        UserRequestDTO request = createRegisterRequest();
        UserResponseDTO response = createUserResponse();

        when(userService.saveUserInDb(request)).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(userService).saveUserInDb(request);
    }
}