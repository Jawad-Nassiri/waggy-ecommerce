package com.waggy.dto.order;

public record OrderItemRequestDTO(
        Integer productId,
        Integer quantity
) {
}
