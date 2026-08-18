package com.waggy.dto.user;

public record LoginRequestDTO(
        String email,
        String password
) {
}
