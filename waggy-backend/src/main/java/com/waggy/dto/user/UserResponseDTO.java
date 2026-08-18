package com.waggy.dto.user;

import com.waggy.entity.Role;

public record UserResponseDTO(
        Integer id,
        String firstName,
        String lastName,
        String email,
        Role role
) {
}
