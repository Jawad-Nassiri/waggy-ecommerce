package com.waggy.mapper;

import com.waggy.dto.order.OrderResponseDTO;
import com.waggy.entity.Order;
import com.waggy.entity.OrderItem;
import com.waggy.entity.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {

    private final OrderMapper orderMapper =
            new OrderMapper(new OrderItemMapper());

    @Test
    void shouldCreateOrderEntity() {

        Order order = orderMapper.toEntity();

        assertNotNull(order);
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

        Order order = new Order();
        order.setId(1);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderItems(List.of(orderItem));
        order.setStatus("PAID");

        OrderResponseDTO dto = orderMapper.toDTO(order);

        assertEquals(1, dto.id());
        assertEquals(1, dto.items().size());
        assertEquals(BigDecimal.valueOf(51.98), dto.totalPrice());
        assertEquals("PAID", dto.status());
    }
}