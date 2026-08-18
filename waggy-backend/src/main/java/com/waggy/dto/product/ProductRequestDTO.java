package com.waggy.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProductRequestDTO(
        @NotBlank(message = "Name must not be empty")
        String name,

        @NotBlank(message = "Description must not be empty")
        String description,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        BigDecimal price,

        @NotNull(message = "Stock is required")
        @PositiveOrZero(message = "Stock cannot be negative")
        Integer stock,

        @NotBlank(message = "Image must not be empty")
        String image,

        @NotNull(message = "Category is required")
        Integer categoryId
) {
}
