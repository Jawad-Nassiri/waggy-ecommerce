package com.waggy.dto.user;

import com.waggy.entity.Role;

public record UserResponseDTO(
        Integer id,
        String name,
        String email,
        Role role
) {
}
