package com.waggy.mapper;

import com.waggy.dto.order.OrderItemRequestDTO;
import com.waggy.dto.order.OrderItemResponseDTO;
import com.waggy.entity.OrderItem;
import com.waggy.entity.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemMapperTest {

    private final OrderItemMapper orderItemMapper = new OrderItemMapper();

    @Test
    void shouldMapRequestDtoToEntity() {

        OrderItemRequestDTO dto = new OrderItemRequestDTO(
                1,
                2
        );

        Product product = new Product();
        product.setId(1);
        product.setName("Dog Food");
        product.setPrice(BigDecimal.valueOf(25.99));

        OrderItem orderItem = orderItemMapper.toEntity(dto, product);

        assertEquals(product, orderItem.getProduct());
        assertEquals(2, orderItem.getQuantity());
        assertEquals(BigDecimal.valueOf(25.99), orderItem.getPrice());
    }

    @Test
    void shouldMapEntityToResponseDto() {

        Product product = new Product();
        product.setId(1);
        product.setName("Dog Food");

        OrderItem orderItem = new OrderItem();
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        orderItem.setPrice(BigDecimal.valueOf(25.99));

        OrderItemResponseDTO dto = orderItemMapper.toDTO(orderItem);

        assertEquals(1, dto.productId());
        assertEquals("Dog Food", dto.productName());
        assertEquals(2, dto.quantity());
        assertEquals(BigDecimal.valueOf(25.99), dto.price());
        assertEquals(BigDecimal.valueOf(51.98), dto.subtotal());
    }
}