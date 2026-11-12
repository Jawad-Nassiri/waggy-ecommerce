package com.waggy.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequestDTO(
        @NotBlank String token,
        @NotBlank String newPassword
) {
}
