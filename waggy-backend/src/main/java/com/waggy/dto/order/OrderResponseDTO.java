package com.waggy.dto.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record OrderResponseDTO(
        Integer id,
        LocalDate orderDate,
        List<OrderItemResponseDTO> items,
        BigDecimal totalPrice
) {
}
