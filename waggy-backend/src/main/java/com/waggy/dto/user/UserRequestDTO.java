package com.waggy.dto.user;

public record UserRequestDTO(
        String firstName,
        String lastName,
        String email,
        String password
) {
}

