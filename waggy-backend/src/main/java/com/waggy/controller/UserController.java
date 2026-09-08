package com.waggy.controller;

import com.waggy.dto.user.UserRequestDTO;
import com.waggy.dto.user.UserResponseDTO;
import com.waggy.dto.user.UserUpdateDTO;
import com.waggy.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/users")
@RestController
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserResponseDTO createUser(@RequestBody UserRequestDTO dto) {
        return userService.saveUserInDb(dto);
    }

    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return userService.findAllUsers();
    }


    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@PathVariable Integer id) {
        return userService.findUserById(id);
    }


    @GetMapping("/me")
    public UserResponseDTO getLoggedInUser() {
        return userService.getCurrentUser();
    }


    @PutMapping("/{id}")
    public UserResponseDTO updateUser(
            @PathVariable Integer id,
            @RequestBody UserUpdateDTO dto) {
        return userService.updateUser(id, dto);
    }

    @PutMapping("/me")
    public UserResponseDTO updateCurrentUser(@RequestBody UserUpdateDTO dto) {
        return userService.updateCurrentUser(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
    }


    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCurrentUser() {
        userService.deleteCurrentUser();
    }


}
