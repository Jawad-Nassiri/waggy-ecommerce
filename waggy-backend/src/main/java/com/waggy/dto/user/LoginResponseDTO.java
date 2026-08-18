package com.waggy.dto.user;

public record LoginResponseDTO(
        String token,
        String role
) {
}
