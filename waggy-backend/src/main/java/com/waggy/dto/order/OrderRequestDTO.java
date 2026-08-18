package com.waggy.dto.order;

import java.util.List;

public record OrderRequestDTO(
        List<OrderItemRequestDTO> items
) {
}
