package com.waggy.dto.order;

import java.math.BigDecimal;

public record OrderItemResponseDTO(
        Integer productId,
        String productName,
        Integer quantity,
        BigDecimal price,
        BigDecimal subtotal
) {
}
