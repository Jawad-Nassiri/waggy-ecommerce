package com.waggy.dto.product;

import com.waggy.dto.category.CategoryResponseDTO;

import java.math.BigDecimal;

public record ProductResponseDTO(
        Integer id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        String image,
        CategoryResponseDTO category
) {
}
