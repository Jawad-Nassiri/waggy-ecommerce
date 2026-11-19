package com.waggy.dto.user;

import com.waggy.entity.Role;

public record AdminUserCreateDTO(
        String name,
        String email,
        Role role
) {
}
