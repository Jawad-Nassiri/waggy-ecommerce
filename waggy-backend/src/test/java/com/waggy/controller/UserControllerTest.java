package com.waggy.controller;

import com.waggy.dto.user.AdminUserUpdateDTO;
import com.waggy.dto.user.UserRequestDTO;
import com.waggy.dto.user.UserResponseDTO;
import com.waggy.dto.user.UserUpdateDTO;
import com.waggy.entity.Role;
import com.waggy.service.JwtService;
import com.waggy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserService userService;

    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        UserRequestDTO request = new UserRequestDTO(
                "Jawad",
                "jawad@test.com",
                "Jn000&"
        );

        UserResponseDTO response = new UserResponseDTO(
                1,
                "Jawad",
                "jawad@test.com",
                Role.USER,
                LocalDateTime.parse("2020-12-12T00:00:00")
        );

        when(userService.saveUserInDb(request)).thenReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Jawad",
                                    "email": "jawad@test.com",
                                    "password": "Jn000&"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Jawad"))
                .andExpect(jsonPath("$.email").value("jawad@test.com"));

        verify(userService).saveUserInDb(request);
    }

    @Test
    void getAllUsers_shouldReturnUsers() throws Exception {
        UserResponseDTO user1 = new UserResponseDTO(
                1,
                "Jawad",
                "jawad@test.com",
                Role.USER,
                LocalDateTime.parse("2020-12-12T00:00:00")
        );

        UserResponseDTO user2 = new UserResponseDTO(
                2,
                "Ali",
                "ali@test.com",
                Role.USER,
                LocalDateTime.parse("2020-12-13T00:00:00")
        );

        List<UserResponseDTO> users = List.of(user1, user2);

        when(userService.findAllUsers()).thenReturn(users);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Jawad"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Ali"));

        verify(userService).findAllUsers();
    }

    @Test
    void getLoggedInUser_shouldReturnCurrentUser() throws Exception {
        UserResponseDTO response = new UserResponseDTO(
                1,
                "Jawad",
                "jawad@test.com",
                Role.USER,
                LocalDateTime.parse("2020-12-12T00:00:00")
        );

        when(userService.getCurrentUser()).thenReturn(response);

        mockMvc.perform(get("/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Jawad"))
                .andExpect(jsonPath("$.email").value("jawad@test.com"));

        verify(userService).getCurrentUser();
    }

    @Test
    void getUserById_shouldReturnUser() throws Exception {
        UserResponseDTO response = new UserResponseDTO(
                1,
                "Jawad",
                "jawad@test.com",
                Role.USER,
                LocalDateTime.parse("2020-12-12T00:00:00")
        );

        when(userService.findUserById(1)).thenReturn(response);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Jawad"))
                .andExpect(jsonPath("$.email").value("jawad@test.com"));

        verify(userService).findUserById(1);
    }

    @Test
    void updateCurrentUser_shouldReturnUpdatedUser() throws Exception {
        UserUpdateDTO request = new UserUpdateDTO(
                "Jawad",
                "jawadUpdated@test.com"
        );

        UserResponseDTO response = new UserResponseDTO(
                1,
                "Jawad",
                "jawadUpdated@test.com",
                Role.USER,
                LocalDateTime.parse("2020-12-12T00:00:00")
        );

        when(userService.updateCurrentUser(request)).thenReturn(response);

        mockMvc.perform(put("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Jawad",
                                    "email": "jawadUpdated@test.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Jawad"))
                .andExpect(jsonPath("$.email").value("jawadUpdated@test.com"));

        verify(userService).updateCurrentUser(request);
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        AdminUserUpdateDTO request = new AdminUserUpdateDTO(
                "Jawad",
                "jawadUpdated@test.com",
                Role.USER
        );

        UserResponseDTO response = new UserResponseDTO(
                1,
                "Jawad",
                "jawadUpdated@test.com",
                Role.USER,
                LocalDateTime.parse("2020-12-12T00:00:00")
        );

        when(userService.updateUser(1, request)).thenReturn(response);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Jawad",
                                    "email": "jawadUpdated@test.com",
                                    "role": "USER"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Jawad"))
                .andExpect(jsonPath("$.email").value("jawadUpdated@test.com"))
                .andExpect(jsonPath("$.role").value("USER"));

        verify(userService).updateUser(1, request);
    }

    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(1);
    }

    @Test
    void deleteCurrentUser_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/users/me"))
                .andExpect(status().isNoContent());

        verify(userService).deleteCurrentUser();
    }
}