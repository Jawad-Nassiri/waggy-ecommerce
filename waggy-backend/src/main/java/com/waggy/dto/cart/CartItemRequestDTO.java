package com.waggy.dto.cart;

public record CartItemRequestDTO(
        Integer productId,
        Integer quantity
) {
}
