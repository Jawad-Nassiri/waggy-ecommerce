package com.waggy.dto.user;

import com.waggy.entity.Role;

public record UserUpdateDTO(
        String name,
        String email,
        Role role
) {
}
