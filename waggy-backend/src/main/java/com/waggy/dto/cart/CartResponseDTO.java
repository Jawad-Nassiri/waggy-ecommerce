package com.waggy.dto.cart;

import java.util.List;

public record CartResponseDTO(
        Integer id,
        Integer userId,
        List<CartItemResponseDTO> items
) {
}
