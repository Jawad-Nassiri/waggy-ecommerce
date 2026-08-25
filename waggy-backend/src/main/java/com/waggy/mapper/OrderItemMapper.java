package com.waggy.mapper;

import com.waggy.dto.order.OrderItemRequestDTO;
import com.waggy.dto.order.OrderItemResponseDTO;
import com.waggy.entity.OrderItem;
import com.waggy.entity.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderItemMapper {

    public OrderItem toEntity(OrderItemRequestDTO dto, Product product) {
        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(dto.quantity());
        orderItem.setPrice(product.getPrice());
        return orderItem;
    }


    public OrderItemResponseDTO toDTO(OrderItem orderItem) {
        return new OrderItemResponseDTO(
                orderItem.getProduct().getId(),
                orderItem.getProduct().getName(),
                orderItem.getQuantity(),
                orderItem.getPrice(),
                orderItem.getPrice()
                        .multiply(BigDecimal.valueOf(orderItem.getQuantity()))
        );
    }

}
