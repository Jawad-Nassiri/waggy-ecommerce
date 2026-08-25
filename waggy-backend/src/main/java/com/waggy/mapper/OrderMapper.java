package com.waggy.mapper;

import com.waggy.dto.order.OrderRequestDTO;
import com.waggy.dto.order.OrderResponseDTO;
import com.waggy.entity.Order;
import com.waggy.entity.OrderItem;
import com.waggy.entity.Product;
import com.waggy.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderMapper {

    private final OrderItemMapper orderItemMapper;
    private final ProductRepository productRepository;

    public Order toEntity(OrderRequestDTO dto) {
        Order order = new Order();

        List<OrderItem> orderItems = dto.items()
                .stream()
                .map(item -> {
                    Product product = productRepository.findById(item.productId())
                            .orElseThrow();

                    return orderItemMapper.toEntity(item, product);
                })
                .toList();

        order.setOrderItems(orderItems);

        return order;
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
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );
    }


}
