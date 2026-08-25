package com.waggy.dto.order;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
        Integer id,
        LocalDateTime orderDate,
        List<OrderItemResponseDTO> items,
        BigDecimal totalPrice
) {
}
