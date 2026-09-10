package com.waggy.dto.user;

import com.waggy.entity.Role;

public record AdminUserUpdateDTO(
        String name,
        String email,
        Role role
) {
}
