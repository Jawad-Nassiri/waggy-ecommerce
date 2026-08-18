package com.waggy.dto.category;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequestDTO(
        @NotBlank(message = "Name must not be empty")
        String name,

        @NotBlank(message = "Slug must not be empty")
        String slug
) {
}
