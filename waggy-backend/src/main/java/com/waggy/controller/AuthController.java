package com.waggy.controller;

import com.waggy.dto.user.LoginRequestDTO;
import com.waggy.dto.user.UserRequestDTO;
import com.waggy.dto.user.UserResponseDTO;
import com.waggy.entity.User;
import com.waggy.exception.AuthenticationException;
import com.waggy.service.JwtService;
import com.waggy.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/login")
    public String login(@RequestBody LoginRequestDTO loginRequest) {

        User user = userService.findUserByEmail(loginRequest.email());

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new AuthenticationException("Wrong password");
        }

        return jwtService.generateToken(user.getEmail(), user.getRole());
    }

    @PostMapping("/register")
    public UserResponseDTO register(@RequestBody UserRequestDTO userRequestDTO) {
        return userService.saveUserInDb(userRequestDTO);
    }
}
