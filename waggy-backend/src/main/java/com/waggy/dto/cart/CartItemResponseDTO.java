package com.waggy.dto.cart;

import java.math.BigDecimal;

public record CartItemResponseDTO(
        Integer productId,
        String productName,
        Integer quantity,
        BigDecimal price,
        BigDecimal subtotal
) {
}
