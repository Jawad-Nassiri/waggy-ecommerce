package com.waggy.dto.order;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
        Integer id,
        String userName,
        Integer userId,
        LocalDateTime orderDate,
        List<OrderItemResponseDTO> items,
        BigDecimal totalPrice,
        String status
) {
}
