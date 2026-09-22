package com.waggy.dto.user;

public record AuthResponseDTO(
        Integer userId,
        String name,
        String email
) {}
