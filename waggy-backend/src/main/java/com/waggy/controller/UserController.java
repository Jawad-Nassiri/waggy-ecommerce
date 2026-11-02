package com.waggy.controller;

import com.waggy.dto.user.AdminUserUpdateDTO;
import com.waggy.dto.user.UserRequestDTO;
import com.waggy.dto.user.UserResponseDTO;
import com.waggy.dto.user.UserUpdateDTO;
import com.waggy.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/users")
@RestController
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(
            summary = "Create a user",
            description = "Creates a new user account."
    )
    @PostMapping
    public UserResponseDTO createUser(@Valid @RequestBody UserRequestDTO dto) {
        return userService.saveUserInDb(dto);
    }

    @Operation(
            summary = "Get all users",
            description = "Returns a list of all registered users."
    )
    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return userService.findAllUsers();
    }


    @Operation(
            summary = "Get logged-in user",
            description = "Returns the profile of the currently authenticated user."
    )
    @GetMapping("/me")
    public UserResponseDTO getLoggedInUser() {
        return userService.getCurrentUser();
    }


    @Operation(
            summary = "Get user by ID",
            description = "Returns the user with the specified ID."
    )
    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@PathVariable Integer id) {
        return userService.findUserById(id);
    }

    @Operation(
            summary = "Update current user",
            description = "Updates the profile of the currently authenticated user."
    )
    @PutMapping("/me")
    public UserResponseDTO updateCurrentUser(@Valid @RequestBody UserUpdateDTO dto) {
        return userService.updateCurrentUser(dto);
    }


    @Operation(
            summary = "Update user",
            description = "Updates the user with the specified ID."
    )
    @PutMapping("/{id}")
    public UserResponseDTO updateUser(
            @PathVariable Integer id,
            @Valid
            @RequestBody AdminUserUpdateDTO dto) {
        return userService.updateUser(id, dto);
    }


    @Operation(
            summary = "Delete user",
            description = "Deletes the user with the specified ID."
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
    }


    @Operation(
            summary = "Delete current user",
            description = "Deletes the account of the currently authenticated user."
    )
    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCurrentUser() {
        userService.deleteCurrentUser();
    }


}
