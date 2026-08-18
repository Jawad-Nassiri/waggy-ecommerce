package com.waggy.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserRequestDTO(
        @NotBlank(message = "First name must not be empty")
        String name,

        @NotBlank(message = "Email must not be empty")
        String email,

        @NotBlank(message = "Password must not be empty")
        @Pattern(
                // At least 6 characters, with a letter, number, and special character
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{6,}$",
                message = "Password must be 6+ chars with a letter, number, and special char"
        )
        String password
) {
}

