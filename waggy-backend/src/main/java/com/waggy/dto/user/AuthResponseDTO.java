package com.waggy.dto.user;

public record AuthResponseDTO(
        String token,
        Integer userId,
        String name,
        String email
) {}
