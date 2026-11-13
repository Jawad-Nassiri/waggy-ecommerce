package com.waggy.controller;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waggy.dto.auth.ForgotPasswordRequestDTO;
import com.waggy.dto.auth.ResetPasswordRequestDTO;
import com.waggy.dto.user.AuthResponseDTO;
import com.waggy.dto.user.LoginRequestDTO;
import com.waggy.dto.user.UserRequestDTO;
import com.waggy.dto.user.UserResponseDTO;
import com.waggy.entity.User;
import com.waggy.exception.AuthenticationException;
import com.waggy.service.JwtService;
import com.waggy.service.PasswordResetService;
import com.waggy.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final PasswordResetService passwordResetService;


    @Operation(summary = "Login", description = "Authenticates a user and creates a JWT authentication cookie.")
    @PostMapping("/login")
    public void login(
            @Valid @RequestBody LoginRequestDTO loginRequest,
            HttpServletResponse response
    ) {
        User user = userService.findUserByEmail(loginRequest.email());

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new AuthenticationException("Wrong password");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getRole());

        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(true) // true in production with HTTPS
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("None")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }


    @Operation(summary = "Register", description = "Creates a new user account and automatically logs in the user.")
    @PostMapping("/register")
    public AuthResponseDTO register(
            @Valid @RequestBody UserRequestDTO userRequestDTO,
            HttpServletResponse response
    ) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            throw new AuthenticationException("You are already logged in");
        }

        UserResponseDTO user = userService.saveUserInDb(userRequestDTO);

        String token = jwtService.generateToken(user.email(), user.role());

        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("None")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());

        return new AuthResponseDTO(
                user.id(),
                user.name(),
                user.email()
        );
    }


    @Operation(summary = "Logout", description = "Logs out the user by clearing the JWT authentication cookie.")
    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    public void logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("None")
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }

    @Operation(
            summary = "Request password reset",
            description = "Sends a password reset link to the user's email address."
    )
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequestDTO dto) {

        passwordResetService.requestPasswordReset(dto.email());

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Reset password",
            description = "Updates the user's password using a valid reset token."
    )
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDTO dto) {

        passwordResetService.resetPassword(dto.token(), dto.newPassword());

        return ResponseEntity.ok().build();
    }

    @GetMapping("/validate-reset-token")
    public ResponseEntity<Void> validateResetToken(@RequestParam String token) {
        passwordResetService.validateToken(token);
        return ResponseEntity.ok().build();
    }

}
