package com.waggy.dto.order;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OrderRequestDTO(
        @NotEmpty(message = "Order must have at least one item")
        List<OrderItemRequestDTO> items
) {
}
