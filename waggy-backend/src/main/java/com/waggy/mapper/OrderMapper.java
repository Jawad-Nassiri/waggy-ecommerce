package com.waggy.mapper;

import com.waggy.dto.order.OrderResponseDTO;
import com.waggy.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final OrderItemMapper orderItemMapper;


    public Order toEntity() {
        return new Order();
    }


    public OrderResponseDTO toDTO(Order order) {
        return new OrderResponseDTO(
                order.getId(),
                order.getOrderDate(),
                order.getOrderItems()
                        .stream()
                        .map(orderItemMapper::toDTO)
                        .toList(),
                order.getOrderItems()
                        .stream()
                        .map(item -> item.getPrice()
                                .multiply(BigDecimal.valueOf(item.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add),
                order.getStatus()
        );
    }


}
