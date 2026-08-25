package com.waggy.dto.user;

import com.waggy.entity.Role;

import java.time.LocalDateTime;

public record UserResponseDTO(
        Integer id,
        String name,
        String email,
        Role role,
        LocalDateTime createdAt
) {
}
